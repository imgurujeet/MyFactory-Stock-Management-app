package com.factory.myfactory.presentation.screens.auth.viewmodel

import android.app.Activity
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.factory.myfactory.core.Constants
import com.factory.myfactory.data.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
    object CodeSent : AuthState()
    object Verified : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState
    // In-memory cache
    private var lastLoginTime: Long = 0L
    private var isUserLoggedIn: Boolean = false

    private val _roles = MutableStateFlow<List<String>>(emptyList())
    val roles: StateFlow<List<String>> = _roles

    fun sendOtp(phone: String, activity: Activity,context: Context) {
        _authState.value = AuthState.Loading

        repo.sendOtp(
            phoneNumber = phone,
            activity = activity,
            onCodeSent = { _authState.value = AuthState.CodeSent },
            onVerificationFailed = { msg -> _authState.value = AuthState.Error(msg) },
            onVerificationSuccess = { msg ->
                cacheLogin(context)
                _authState.value = AuthState.Success(msg)
                _authState.value = AuthState.Verified
            }
        )
    }

    fun verifyOtp(code: String,context: Context) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            repo.verifyOtp(code) { success, msg ->
                _authState.value =
                    if (success) {
                        cacheLogin(context)
                        AuthState.Success(msg ?: "Verified")
                    }
                    else {
                        AuthState.Error(msg ?: "Error")
                    }
            }
        }
    }

    private fun cacheLogin(context: Context) {
        val prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putLong(Constants.KEY_LAST_LOGIN, System.currentTimeMillis()).apply()
    }

    fun isLoggedIn(context: Context): Boolean {
        val prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        val lastLogin = prefs.getLong(Constants.KEY_LAST_LOGIN, 0L)
        return System.currentTimeMillis() - lastLogin < Constants.LOGIN_DURATION_MS
    }

    // Role cache
    private fun cacheRoles(context: Context, roles: List<String>) {
        val prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putStringSet(Constants.KEY_USER_ROLES, roles.toSet()).apply()
        prefs.edit().putLong(Constants.KEY_ROLE_CACHE_TIME, System.currentTimeMillis()).apply()
    }

    fun getCachedRoles(context: Context): List<String>? {
        val prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        val lastCache = prefs.getLong(Constants.KEY_ROLE_CACHE_TIME, 0L)
        return if (System.currentTimeMillis() - lastCache < Constants.LOGIN_DURATION_MS) {
            prefs.getStringSet(Constants.KEY_USER_ROLES, emptySet())?.toList()
        } else null
    }

    fun clearCache(context: Context) {
        // Clear in-memory cache
        lastLoginTime = 0L
        isUserLoggedIn = false
        _roles.value = emptyList()

        // Clear SharedPreferences cache
        val prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(Constants.KEY_LAST_LOGIN).apply()
        prefs.edit().remove(Constants.KEY_USER_ROLES).apply()
        prefs.edit().remove(Constants.KEY_ROLE_CACHE_TIME).apply()
    }



    // Fetch roles from Firestore and cache
    fun fetchRoles(uid: String, context: Context, onComplete: (List<String>?, String?) -> Unit) {
        val cachedRoles = getCachedRoles(context)
        if (cachedRoles != null && cachedRoles.isNotEmpty()) {
            onComplete(cachedRoles, null)
            return
        }

        repo.getUserRoles(uid) { roles, error ->
            if (roles != null && roles.isNotEmpty()) {
                cacheRoles(context, roles)
                onComplete(roles, null)
            } else {
                onComplete(null, error ?: "No roles found. Contact Admin.")
            }
        }
    }

    // Verify role with secondAuthKey
    fun verifyRoleAccess(uid: String, role: String, key: String, onComplete: (Boolean, String?) -> Unit) {
        repo.verifyRoleAccess(uid, role, key) { success, msg ->
            onComplete(success, msg)
        }
    }

    fun getSecondAuthKey(uid: String, role: String, onComplete: (String?) -> Unit) {
        repo.getUserRoles(uid) { roles, _ ->
            // Optional: you already have cached roles
        }
        repo.verifyRoleAccess(uid, role, "") { success, msg ->
            // If the role requires second auth key, msg will contain key info
            onComplete(if (success) null else msg)
        }
    }



    fun startObservingRoles(uid: String) {
        repo.observeUserRoles(uid) { newRoles ->
            _roles.value = newRoles // triggers recomposition in Compose
        }
    }

    fun logout(context: Context) {
        repo.logout { success, message ->
            if (success) {
                // Clear cached login
                val prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
                prefs.edit().remove(Constants.KEY_LAST_LOGIN).apply()

                // Optionally clear cached roles too
                prefs.edit().remove(Constants.KEY_USER_ROLES)
                prefs.edit().remove(Constants.KEY_ROLE_CACHE_TIME)

                _authState.value = AuthState.Success(message ?: "Logged out")
            } else {
                _authState.value = AuthState.Error(message ?: "Logout failed")
            }
        }
    }





}
