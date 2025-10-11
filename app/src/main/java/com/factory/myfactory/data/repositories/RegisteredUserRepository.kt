package com.factory.myfactory.data.repositories

import com.factory.myfactory.data.models.RegisteredUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.imgurujeet.stockease.data.models.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegisteredUserRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private  val db = FirebaseFirestore.getInstance()

    val registeredUserDb = "registered_user"

    suspend fun registerUser(user: RegisteredUser) {
        // Generate a document reference (Firestore ID)
        val docRef = db.collection(registeredUserDb).document()

        // Copy the user with the generated UID
        val userWithUid = user.copy(uid = docRef.id)

        // Save the user with the UID
        docRef.set(userWithUid).await()
    }


    fun observeRegisteredUsers() = callbackFlow<List<RegisteredUser>> {
        val listener: ListenerRegistration = firestore.collection(registeredUserDb)
            .addSnapshotListener { snapshot, error ->
                if(error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val registered_user = snapshot?.documents?.mapNotNull{
                    it.toObject(RegisteredUser::class.java) } ?: emptyList()
                trySend(registered_user)

            }
        awaitClose { listener.remove() }

    }

    suspend fun getRegisteredUsers():List<RegisteredUser> {
        return try {
            val snapshot = firestore.collection(registeredUserDb).get().await()
            snapshot.documents.mapNotNull { it.toObject(RegisteredUser::class.java) }

        }catch (e: Exception){
            emptyList()
        }
    }

    suspend fun getRegisteredUserById(uid : String): RegisteredUser? {
        return try {
            val doc = firestore.collection(registeredUserDb).document(uid).get().await()
            doc.toObject(RegisteredUser::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleteRegisteredUser(userId: String) {
        firestore.collection(registeredUserDb)
            .document(userId)
            .delete()
            .await()
    }

    suspend fun updateRegisteredUserStatus(uid: String, newActive: Boolean) {
        val db = FirebaseFirestore.getInstance()
        db.collection("registered_user")
            .document(uid)
            .update("active", newActive)
            .await()   // use await() to actually wait
    }






}