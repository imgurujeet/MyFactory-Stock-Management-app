package com.factory.myfactory.core

object Constants {
    const val OTP_LENGTH = 6
    const val OTP_TIMEOUT = 60 // seconds

    // Login duration in milliseconds (1 day)
    const val LOGIN_DURATION_MS = 24*30 * 60 * 60 * 1000L

    // SharedPreferences keys
    const val PREFS_NAME = "myfactory_prefs"
    const val KEY_LAST_LOGIN = "last_login_time"
    const val KEY_USER_ROLES = "user_roles"
    const val KEY_ROLE_CACHE_TIME = "last_role_fetch_time"
}