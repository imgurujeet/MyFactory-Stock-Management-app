package com.factory.myfactory.data.models

data class CoilStockItem(
    val size: String = "",
    val gauge: String = "",
    val grade: String = "",
    val weight: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis(),
    val inventoryId: String? = null,

    // 🔹 New fields for user tracking (only meaningful for entries)
    val entryUserId: String? = null,
    val entryUsername: String? = null
)

