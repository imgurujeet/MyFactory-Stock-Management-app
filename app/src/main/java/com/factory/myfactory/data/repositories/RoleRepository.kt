package com.factory.myfactory.data.repositories

import android.content.Context
import com.factory.myfactory.core.Constants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoleRepository @Inject constructor(
    private val db: FirebaseFirestore
) {

    /**
     * Fetch roles from Firestore for a given user UID.
     * Cache locally in SharedPreferences for 24 hours.
     */
    suspend fun fetchUserRoles(context: Context, uid: String): List<String> {
        val prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        val lastFetchTime = prefs.getLong("${Constants.KEY_ROLE_CACHE_TIME}_$uid", 0L)
        val cachedRoles = prefs.getStringSet("${Constants.KEY_USER_ROLES}_$uid", emptySet()) ?: emptySet()

        // Return cached roles if within 24 hours
        if (System.currentTimeMillis() - lastFetchTime < Constants.LOGIN_DURATION_MS && cachedRoles.isNotEmpty()) {
            return cachedRoles.toList()
        }

        // Fetch roles from Firestore
        return try {
            val doc = db.collection("users").document(uid).get().await()
            val roles = doc.get("roles") as? List<String> ?: emptyList()

            if (roles.isEmpty()) {
                listOf("Contact Admin") // fallback if no roles
            } else {
                // Cache roles locally
                prefs.edit()
                    .putStringSet("${Constants.KEY_USER_ROLES}_$uid", roles.toSet())
                    .putLong("${Constants.KEY_ROLE_CACHE_TIME}_$uid", System.currentTimeMillis())
                    .apply()
                roles
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (cachedRoles.isNotEmpty()) cachedRoles.toList() else listOf("Contact Admin")
        }
    }

    /**
     * Clear cached roles for a user (if needed)
     */
    fun clearCachedRoles(context: Context, uid: String) {
        val prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .remove("${Constants.KEY_USER_ROLES}_$uid")
            .remove("${Constants.KEY_ROLE_CACHE_TIME}_$uid")
            .apply()
    }
}
