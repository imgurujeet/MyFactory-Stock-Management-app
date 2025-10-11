package com.factory.myfactory.data.models


data class PipeStock(
    val inventoryId: String? = null,
    val coilStockId: String? = null,
    val coilSize: String = "",
    val pipeType: String = "",
    val pipeSize: String = "",
    val gauge: String = "",
    val grade: String = "",
    val quantity: Int = 0,
    val approxWeight: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis(),
    val entryUserId: String? = null,
    val entryUserName: String? = null
)


data class ScrapStock(
    val inventoryId: String? = null,
    val coilStockId: String? = null,
    val gauge: String = "",
    val grade: String = "",
    val weight: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis(),
    val entryUserId: String? = null,
    val entryUserName: String? = null
)

data class CutPieceStock(
    val inventoryId: String? = null,
    val coilStockId: String? = null,
    val gauge: String = "",
    val grade: String = "",
    val weight: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis(),
    val entryUserId: String? = null,
    val entryUserName: String? = null
)