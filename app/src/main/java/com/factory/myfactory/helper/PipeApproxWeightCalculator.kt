package com.factory.myfactory.helper

import kotlin.math.PI
import kotlin.math.pow

fun getApproxPipeWeight(size: String, gauge: String, type: String): Double {
    // Nested map: type -> size -> gauge -> weight
    val weightTable = mapOf(
        "round" to mapOf(
            "1/2\"" to mapOf(
                "16G" to 2.5, "18G" to 2.2, "20G" to 1.8, "22G" to 1.5,
                "24G" to 1.35, "26G" to 1.1, "28G" to 0.85
            ),
            "5/8\"" to mapOf(
                "16G" to 3.1, "18G" to 2.6, "20G" to 2.0, "22G" to 1.8,
                "24G" to 1.7, "26G" to 1.3, "28G" to 1.1
            ),
            "3/4\"" to mapOf(
                "16G" to 4.0, "18G" to 3.3, "20G" to 2.5, "22G" to 2.2,
                "24G" to 2.0, "26G" to 1.6, "28G" to 1.25
            ),
            "1\"" to mapOf(
                "16G" to 5.5, "18G" to 4.5, "20G" to 3.5, "22G" to 3.0,
                "24G" to 2.6, "26G" to 2.2, "28G" to 1.85
            ),
            "1.1/4\"" to mapOf(
                "16G" to 6.9, "18G" to 5.5, "20G" to 3.8, "22G" to 4.2,
                "24G" to 3.2, "26G" to 2.6, "28G" to 2.15
            ),
            "1.5\"" to mapOf(
                "16G" to 8.3, "18G" to 6.6, "20G" to 5.2, "22G" to 4.5,
                "24G" to 3.6, "26G" to 2.85, "28G" to 2.5
            ),
            "2\"" to mapOf(
                "16G" to 11.3, "18G" to 9.0, "20G" to 7.0, "22G" to 6.0,
                "24G" to 5.0, "26G" to 4.5, "28G" to 3.7
            ),
            "3\"" to mapOf(
                "16G" to 16.5, "18G" to 13.1, "20G" to 10.5, "22G" to 9.0,
                "24G" to 8.0, "26G" to 6.5, "28G" to 5.6
            )
        ),
        "rect" to mapOf(
            "1/2\" × 1/2\"" to mapOf(
                "16G" to 3.3, "18G" to 2.7, "20G" to 2.0, "22G" to 1.8,
                "24G" to 1.5, "26G" to 1.3, "28G" to 1.1
            ),
            "5/8\" × 5/8\"" to mapOf(
                "16G" to 4.0, "18G" to 3.3, "20G" to 2.6, "22G" to 2.2,
                "24G" to 2.0, "26G" to 1.6, "28G" to 1.25
            ),
            "3/4\" × 3/4\"" to mapOf(
                "16G" to 5.5, "18G" to 4.5, "20G" to 3.5, "22G" to 3.0,
                "24G" to 2.6, "26G" to 2.2, "28G" to 1.85
            ),
            "1\" × 1\"" to mapOf(
                "16G" to 7.0, "18G" to 5.5, "20G" to 3.8, "22G" to 4.2,
                "24G" to 3.2, "26G" to 2.6, "28G" to 2.15
            ),
            "1.1/4\" × 1.1/4\"" to mapOf(
                "16G" to 8.3, "18G" to 6.6, "20G" to 5.2, "22G" to 4.5,
                "24G" to 4.0, "26G" to 3.2, "28G" to 2.7
            ),
            "1.5\" × 1.5\"" to mapOf(
                "16G" to 11.0, "18G" to 9.0, "20G" to 7.0, "22G" to 6.0,
                "24G" to 4.65, "26G" to 3.95, "28G" to 3.3
            ),
            "2\" × 1\"" to mapOf(
                "16G" to 11.0, "18G" to 9.0, "20G" to 7.0, "22G" to 6.1,
                "24G" to 5.2, "26G" to 4.4, "28G" to 3.8
            ),
            "3\" × 1\"" to mapOf(
                "16G" to 14.5, "18G" to 11.25, "20G" to 8.75, "22G" to 7.5,
                "24G" to 6.6, "26G" to 5.5, "28G" to 4.6
            ),
            "3/4\" × 1.5\"" to mapOf(
                "16G" to 8.25, "18G" to 6.75, "20G" to 5.25, "22G" to 4.5,
                "24G" to 3.9, "26G" to 3.2, "28G" to 2.8
            ),
            "1/2\" × 1\"" to mapOf(
                "16G" to 5.5, "18G" to 4.5, "20G" to 3.5, "22G" to 3.0,
                "24G" to 2.6, "26G" to 2.2, "28G" to 1.85
            )
        )
    )

    val typeMap = weightTable[type.lowercase()] ?: return 0.0
    val sizeMap = typeMap[size] ?: return 0.0
    return sizeMap[gauge.uppercase()] ?: 0.0
}



fun Double.round(decimals: Int): Double {
    return "%.${decimals}f".format(this).toDouble()
}
