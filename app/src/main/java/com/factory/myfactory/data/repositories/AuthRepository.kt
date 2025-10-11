package com.factory.myfactory.data.repositories

import android.app.Activity
import com.factory.myfactory.data.models.RegisteredUser
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.imgurujeet.stockease.data.models.User
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {
    private var verificationId: String? = null



    fun sendOtp(
        phoneNumber: String,
        activity: Activity,
        onCodeSent: () -> Unit,
        onVerificationFailed: (String) -> Unit,
        onVerificationSuccess: (String) -> Unit // NEW: success callback
    ) {
        val formattedPhone = if (phoneNumber.startsWith("+91")) phoneNumber else "+91$phoneNumber"

        val db = FirebaseFirestore.getInstance()
        val usersRef = db.collection("registered_user")

        // Check if the phone exists in RegisteredUser collection
        usersRef.whereEqualTo("phone", formattedPhone)
            .whereEqualTo("active",true) // active check
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    //  User exists → proceed with OTP sending
                    val options = PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(formattedPhone)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(activity)
                        .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                                signInWithPhoneAuthCredential(credential) { success, msg ->
                                    if (success) onVerificationSuccess(msg ?: "Auto Verified")
                                    else onVerificationFailed(msg ?: "Auto Verification Failed")
                                }
                            }

                            override fun onVerificationFailed(e: FirebaseException) {
                                onVerificationFailed(e.message ?: "Verification Failed")
                            }

                            override fun onCodeSent(
                                vid: String,
                                token: PhoneAuthProvider.ForceResendingToken
                            ) {
                                verificationId = vid
                                onCodeSent()
                            }
                        })
                        .build()

                    PhoneAuthProvider.verifyPhoneNumber(options)

                } else {
                    //  No such user
                    onVerificationFailed("Your are not a registered user")
                }
            }
            .addOnFailureListener { e ->
                onVerificationFailed("Error checking user: ${e.message}")
            }
    }


//    fun sendOtp(
//        phoneNumber: String,
//        activity: Activity,
//        onCodeSent: () -> Unit,
//        onVerificationFailed: (String) -> Unit,
//        onVerificationSuccess: (String) -> Unit // NEW: success callback
//    ) {
//        val options = PhoneAuthOptions.newBuilder(auth)
//            .setPhoneNumber("+91$phoneNumber")
//            .setTimeout(60L, TimeUnit.SECONDS)
//            .setActivity(activity)
//            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//
//                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
//                    // Auto-retrieval succeeded, sign in user automatically
//                    signInWithPhoneAuthCredential(credential) { success, msg ->
//                        if (success) onVerificationSuccess(msg ?: "Auto Verified")
//                        else onVerificationFailed(msg ?: "Auto Verification Failed")
//                    }
//                }
//
//                override fun onVerificationFailed(e: FirebaseException) {
//                    onVerificationFailed(e.message ?: "Verification Failed")
//                }
//
//                override fun onCodeSent(vid: String, token: PhoneAuthProvider.ForceResendingToken) {
//                    verificationId = vid
//                    onCodeSent()
//                }
//            })
//            .build()
//
//        PhoneAuthProvider.verifyPhoneNumber(options)
//    }

    fun verifyOtp(
        code: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val credential = verificationId?.let { PhoneAuthProvider.getCredential(it, code) }
            ?: return onResult(false, "Verification ID missing")

        signInWithPhoneAuthCredential(credential, onResult)
    }

    private fun signInWithPhoneAuthCredential(
        credential: PhoneAuthCredential,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = task.result?.user?.uid
                    val phone = auth.currentUser?.phoneNumber
                    if (uid != null && phone != null) {
                        checkOrCreateUser(uid, phone, onResult)
                    } else {
                        onResult(false, "User info missing")
                    }
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }


    private fun checkOrCreateUser(
        uid: String,
        phone: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val userRef = db.collection("users").document(uid)

        userRef.get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    onResult(true, "Login Success")
                } else {
                    // fetch details from RegisteredUser
                    val registeredUserRef = db.collection("registered_user")
                    registeredUserRef.whereEqualTo("phone", phone)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            if (!querySnapshot.isEmpty) {
                                val regDoc = querySnapshot.documents[0]
                                val regUser = regDoc.toObject(RegisteredUser::class.java)

                                val user = User(
                                    uid = uid,
                                    phone = phone,
                                    name = regUser?.name ?: "Unknown",
                                    roles = regUser?.roles ?: listOf("guest"),
                                    secondAuthKey = regUser?.secondAuthKey,
                                    active = regUser?.active ?: true,
                                    lastLogin = System.currentTimeMillis()
                                )

                                userRef.set(user)
                                    .addOnSuccessListener { onResult(true, "User Created") }
                                    .addOnFailureListener { onResult(false, it.message) }
                            } else {
                                onResult(false, "Not allowed: User not registered")
                            }
                        }
                        .addOnFailureListener { e ->
                            onResult(false, "Error fetching RegisteredUser: ${e.message}")
                        }
                }
            }
            .addOnFailureListener { onResult(false, it.message) }
    }


//    private fun checkOrCreateUser(
//        uid: String,
//        phone: String,
//        onResult: (Boolean, String?) -> Unit
//    ) {
//        val userRef = db.collection("users").document(uid)
//
//        userRef.get()
//            .addOnSuccessListener { doc ->
//                if (doc.exists()) {
//                    onResult(true, "Login Success")
//                } else {
//                    val user = User(
//                        uid = uid,
//                        phone = phone,
//                        roles = listOf("guest"), // default role as a list
//                        secondAuthKey = null, //  safe convert
//                        active = true,
//                        lastLogin = System.currentTimeMillis()
//                    )
//
//                    userRef.set(user)
//                        .addOnSuccessListener { onResult(true, "User Created") }
//                        .addOnFailureListener { onResult(false, it.message) }
//                }
//            }
//            .addOnFailureListener { onResult(false, it.message) }
//    }

    fun verifyRoleAccess(
        uid: String,
        role: String,
        key: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val userRef = db.collection("users").document(uid)

        userRef.get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val roles = doc.get("roles") as? List<String> ?: emptyList()
                    val storedKey: String? = when(val raw = doc.get("secondAuthKey")) {
                        is String -> raw
                        is Number -> raw.toString()
                        else -> null
                    }
                    // safer than getString()

                    when {
                        roles.isEmpty() -> {
                            onResult(false, "No roles assigned. Contact Admin.")
                        }
                        !roles.contains(role) -> {
                            onResult(false, "Role '$role' not assigned. Contact Admin.")
                        }
                        storedKey.isNullOrEmpty() -> {
                            // No second auth key set, allow login
                            onResult(true, null)
                        }
                        storedKey == key -> {
                            // Correct key entered
                            onResult(true, null)
                        }
                        else -> {
                            // Key mismatch
                            onResult(false, "Incorrect access key")
                        }
                    }
                } else {
                    onResult(false, "User not found")
                }
            }
            .addOnFailureListener { onResult(false, it.message) }
    }



    fun getUserRoles(
        uid: String,
        onResult: (roles: List<String>?, error: String?) -> Unit
    ) {
        val userRef = db.collection("users").document(uid)

        userRef.get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val roles = doc.get("roles") as? List<*>
                    if (!roles.isNullOrEmpty()) {
                        // Ensure every item is a String
                        val roleList = roles.mapNotNull { it as? String }
                        onResult(roleList, null)
                    } else {
                        onResult(null, "No roles assigned. Contact Admin.")
                    }
                } else {
                    onResult(null, "User not found")
                }
            }
            .addOnFailureListener { e ->
                onResult(null, e.message ?: "Failed to fetch roles")
            }
    }

    fun getSecondAuthKey(uid: String, onResult: (String?) -> Unit) {
        val userRef = db.collection("users").document(uid)
        userRef.get()
            .addOnSuccessListener { doc ->
                val key = doc.getString("secondAuthKey")
                onResult(key)
            }
            .addOnFailureListener { onResult(null) }
    }

    fun observeUserRoles(uid: String, onUpdate: (List<String>) -> Unit) {
        val userRef = db.collection("users").document(uid)
        userRef.addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) return@addSnapshotListener
            val roles = snapshot.get("roles") as? List<*> ?: emptyList<Any>()
            val roleStrings = roles.mapNotNull { it as? String }
            onUpdate(roleStrings)
        }
    }

    fun logout(onResult: (Boolean, String?) -> Unit) {
        try {
            auth.signOut()
            onResult(true, "Logged out successfully")
        } catch (e: Exception) {
            onResult(false, e.message ?: "Logout failed")
        }
    }

    fun observeRegisteredUser(phone: String, onUserChanged: (RegisteredUser?) -> Unit) {
        val registeredUserRef = db.collection("registered_user")
            .whereEqualTo("phone", phone)
            .limit(1)

        registeredUserRef.addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener

            if (snapshot == null || snapshot.isEmpty) {
                // User deleted
                onUserChanged(null)
                return@addSnapshotListener
            }

            val regUser = snapshot.documents[0].toObject(RegisteredUser::class.java)
            onUserChanged(regUser) // could be active or inactive
        }
    }


}
