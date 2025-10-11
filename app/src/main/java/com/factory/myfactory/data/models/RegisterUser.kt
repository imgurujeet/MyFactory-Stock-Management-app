package com.factory.myfactory.data.models
data class RegisteredUser(
    val uid: String = "",
    val phone: String = "",
    val roles: List<String> = listOf("guest"),
    val secondAuthKey: Any? = null,
    val name: String? = "Unknown",
    val active: Boolean? = true,
)