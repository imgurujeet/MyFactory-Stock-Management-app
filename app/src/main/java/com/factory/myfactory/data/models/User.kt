package com.imgurujeet.stockease.data.models

data class User(
    val uid: String = "",
    val phone: String = "",
    val roles: List<String> = listOf("guest"),
    val secondAuthKey: Any? = null,
    val name: String? = "Unknown",
    val active: Boolean? = true,
    val lastLogin: Long = System.currentTimeMillis()
)

val dummyUsers = listOf(
    User(
        uid = "U001",
        phone = "9876543210",
        roles = listOf("admin", "editor"),
        secondAuthKey = "authKey123",
        name = "Alice Johnson",
        active = true
    ),
    User(
        uid = "U002",
        phone = "9123456780",
        roles = listOf("user"),
        secondAuthKey = null,
        name = "Bob Smith",
        active = false
    ),
    User(
        uid = "U003",
        phone = "9988776655",
        roles = listOf("moderator", "user"),
        secondAuthKey = "authKeyXYZ",
        name = "Charlie Brown",
        active = true
    ),
    User(
        uid = "U004",
        phone = "9001122334",
        roles = listOf("guest"),
        secondAuthKey = null,
        name = "Unknown",
        active = true
    )
)
