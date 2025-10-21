package com.factory.myfactory.data.repositories

import com.factory.myfactory.data.models.CoilStockItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoilRepository @Inject constructor(
    private val db: FirebaseFirestore
) {

    companion object {
        const val COIL_DB = "coil_inventory"
        const val COIL_ENTRY_DB = "coil_entry"
        private const val COIL_PREFIX = "coil_" //  prefix for coil inventory IDs
    }

    // --- Helper to create a unique inventory ID ---
    private fun buildInventoryId(size: String, gauge: String, grade: String): String {
        val rawId = "$COIL_PREFIX${size}_${gauge}_${grade}"
        return rawId
            .replace("[^a-zA-Z0-9_]".toRegex(), "_")  // replace invalid chars
            .replace("_+".toRegex(), "_")            // collapse consecutive underscores
            .trim('_')                               // remove leading/trailing underscores
            .lowercase()
    }



    // --- Add new entry linked to inventory ---
    suspend fun addStock(
        item: CoilStockItem,
        entryUserId: String?,
        entryUserName: String?
    ) {
        val inventoryId = buildInventoryId(item.size, item.gauge, item.grade)
        val inventoryRef = db.collection(COIL_DB).document(inventoryId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(inventoryRef)

            if (snapshot.exists()) {
                val existing = snapshot.toObject(CoilStockItem::class.java)
                val newWeight = (existing?.weight ?: 0.0) + item.weight
                transaction.update(inventoryRef, "weight", newWeight)
            } else {
                // Create inventory with only necessary fields
                val newInventoryMap = mapOf(
                    "size" to item.size,
                    "gauge" to item.gauge,
                    "grade" to item.grade,
                    "weight" to item.weight,
                    "timestamp" to item.timestamp,
                    "inventoryId" to inventoryId
                )
                transaction.set(inventoryRef, newInventoryMap)
            }

            val entryData = item.copy(
                inventoryId = inventoryId,
                entryUserId = entryUserId ?: "unknown_user",
                entryUsername = entryUserName ?: "Unknown"
            )
            val entryRef = db.collection(COIL_ENTRY_DB).document()
            transaction.set(entryRef, entryData)
        }.await()
    }



    fun getAllEntriesFlow(): Flow<List<CoilStockItemWithId>> = callbackFlow {
        val listener = db.collection(COIL_ENTRY_DB)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                snapshot?.let {
                    val items = it.documents.map { doc ->
                        CoilStockItemWithId(
                            id = doc.id,
                            coilStockItem = doc.toObject(CoilStockItem::class.java)!!
                        )
                    }
                    trySend(items)
                }
            }
        awaitClose { listener.remove() }
    }

    fun getInventoryFlow(): Flow<List<CoilStockItemWithId>> = callbackFlow {
        val listener = db.collection(COIL_DB)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                snapshot?.let {
                    val items = it.documents.map { doc ->
                        CoilStockItemWithId(
                            id = doc.id,
                            coilStockItem = doc.toObject(CoilStockItem::class.java)!!
                        )
                    }
                    trySend(items)
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun updateStock(itemId: String, updatedItem: CoilStockItem) {
        db.collection(COIL_ENTRY_DB)
            .document(itemId)
            .set(updatedItem)
            .await()
    }

    suspend fun deleteStock(itemId: String) {
        val entrySnapshot = db.collection(COIL_ENTRY_DB)
            .document(itemId)
            .get()
            .await()

        val entryItem = entrySnapshot.toObject(CoilStockItem::class.java) ?: return

        val inventoryId = buildInventoryId(entryItem.size, entryItem.gauge, entryItem.grade)
        val inventoryRef = db.collection(COIL_DB).document(inventoryId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(inventoryRef)
            if (snapshot.exists()) {
                val existing = snapshot.toObject(CoilStockItem::class.java)
                val newWeight = (existing?.weight ?: 0).toDouble() - entryItem.weight

                if (newWeight > 0) {
                    transaction.update(inventoryRef, "weight", newWeight)
                } else {
                    transaction.delete(inventoryRef)
                }
            }

            transaction.delete(db.collection(COIL_ENTRY_DB).document(itemId))
        }.await()
    }

    data class CoilStockItemWithId(
        val id: String,
        val coilStockItem: CoilStockItem
    )
}
