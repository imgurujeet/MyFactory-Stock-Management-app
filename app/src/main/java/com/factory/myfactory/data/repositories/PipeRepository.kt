package com.factory.myfactory.data.repositories

import com.factory.myfactory.data.models.PipeStock
import com.factory.myfactory.data.models.ScrapStock
import com.factory.myfactory.data.models.CutPieceStock
import com.factory.myfactory.data.models.CoilStockItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PipeRepository @Inject constructor(
    private val db: FirebaseFirestore
) {

    companion object {
        const val PIPE_DB = "pipe_inventory"
        const val PIPE_ENTRY_DB = "pipe_entry"
        const val PIPE_OUTFLOW_DB = "pipe_outflow_entry"
        //const val PIPE_OUTFLOW_ENTRY_DB = "pipe_outflow_entry"
        const val SCRAP_DB = "scrap_entry"
        const val SCRAP_INVENTORY_DB = "scrap_inventory"
        const val SCRAP_OUTFLOW_DB = "scrap_outflow_entry"
        const val CUT_PIECE_DB = "cut_piece_entry"
        const val CUT_PIECE_INVENTORY_DB = "cut_piece_inventory"
        const val CUT_PIECE_OUTFLOW_INVENTORY_DB = "cut_piece_outflow_entry"
        const val COIL_DB = "coil_inventory" // to reduce coil stock
        private const val PIPE_PREFIX = "pipe_"
        private const val PIPE_OUTFLOW_PREFIX = "pipe_outflow_"
        private const val SCRAP_PREFIX = "scrap_"
        private const val CUT_PIECE_PREFIX = "cut_piece_"
    }


    // --- Helper to create a unique inventory ID --- //PIPE_PREFIX{pipeType}_{size}_{coilSize}_{gauge}_{grade}
    private fun buildPipeInventoryId(pipeType: String,size: String, coilSize: String, gauge: String, grade: String): String {
        val rawId = "$PIPE_PREFIX${pipeType}_${size}_${coilSize}_${gauge}_${grade}"
        return rawId
            .replace("[^a-zA-Z0-9_]".toRegex(), "_")  // replace invalid chars
            .replace("_+".toRegex(), "_")            // collapse consecutive underscores
            .trim('_')                               // remove leading/trailing underscores
            .lowercase()
    }

    private fun buildPipeOutflowInventoryId(pipeType: String,size: String, coilSize: String, gauge: String, grade: String): String {
        val rawId = "$PIPE_OUTFLOW_PREFIX${pipeType}_${size}_${coilSize}_${gauge}_${grade}"
        return rawId
            .replace("[^a-zA-Z0-9_]".toRegex(), "_")  // replace invalid chars
            .replace("_+".toRegex(), "_")            // collapse consecutive underscores
            .trim('_')                               // remove leading/trailing underscores
            .lowercase()
    }

    private fun buildScrapInventoryId(gauge: String, grade: String): String {
        val rawId = "${SCRAP_PREFIX}${gauge}_${grade}"
        return rawId
            .replace("[^a-zA-Z0-9_]".toRegex(), "_")  // replace invalid chars
            .replace("_+".toRegex(), "_")            // collapse consecutive underscores
            .trim('_')                               // remove leading/trailing underscores
            .lowercase()
    }

    private fun buildCutPieceInventoryId(gauge: String, grade: String): String {
        val rawId = "$CUT_PIECE_PREFIX${gauge}_${grade}"
        return rawId
            .replace("[^a-zA-Z0-9_]".toRegex(), "_")  // replace invalid chars
            .replace("_+".toRegex(), "_")            // collapse consecutive underscores
            .trim('_')                               // remove leading/trailing underscores
            .lowercase()
    }

    // --- Fetch coil inventory ID based on specs ---
    suspend fun fetchCoilInventoryId(
        grade: String,
        gauge: String,
        coilSize: String
    ): String? {
        val coilQuery = db.collection(COIL_DB)
            .whereEqualTo("grade", grade)
            .whereEqualTo("gauge", gauge)
            .whereEqualTo("size", coilSize)
            .limit(1)
            .get()
            .await()

        return if (!coilQuery.isEmpty) {
            coilQuery.documents[0].id
        } else null
    }


    // --- Fetch pipe inventory ID based on specs ---
    suspend fun fetchPipeInventoryId(
        grade: String,
        gauge: String,
        pipeSize: String,
        pipeType: String
    ): String? {
        val coilQuery = db.collection(PIPE_DB)
            .whereEqualTo("grade", grade)
            .whereEqualTo("gauge", gauge)
            .whereEqualTo("pipeSize", pipeSize)
            .whereEqualTo("pipeType",pipeType)
            .limit(1)
            .get()
            .await()

        return if (!coilQuery.isEmpty) {
            coilQuery.documents[0].id
        } else null
    }

    // --- Fetch scrap inventory ID based on specs ---
    suspend fun fetchScrapInventoryId(
        grade: String,
        gauge: String
    ): String? {
        val scrapQuery = db.collection(SCRAP_INVENTORY_DB)
            .whereEqualTo("grade", grade)
            .whereEqualTo("gauge", gauge)
            .limit(1)
            .get()
            .await()

        return if (!scrapQuery.isEmpty) {
            scrapQuery.documents[0].id
        } else null
    }

    // --- Fetch cut piece inventory ID based on specs ---
    suspend fun fetchCutPieceInventoryId(
        grade: String,
        gauge: String
    ): String? {
        val cutPieceQuery = db.collection(CUT_PIECE_INVENTORY_DB)
            .whereEqualTo("grade", grade)
            .whereEqualTo("gauge", gauge)
            .limit(1)
            .get()
            .await()

        return if (!cutPieceQuery.isEmpty) {
            cutPieceQuery.documents[0].id
        } else null
    }



    // ---------- PIPE ENTRY & INVENTORY MANAGEMENT ----------

    suspend fun addPipeStockWithScrapAndCutPiece(
        pipeItem: PipeStock,
        scrapItem: ScrapStock,
        cutPieceItem: CutPieceStock,
        entryUserId: String?,
        entryUserName: String?
    ) {

        val coilId = fetchCoilInventoryId(pipeItem.grade, pipeItem.gauge, pipeItem.coilSize)
        val coilInventoryRef = db.collection(COIL_DB).document(coilId ?: "")
        val pipeInventoryId = buildPipeInventoryId(pipeItem.pipeType, size = pipeItem.pipeSize,coilSize =pipeItem.coilSize, pipeItem.gauge , pipeItem.grade)
        val pipeInventoryRef = db.collection(PIPE_DB).document(pipeInventoryId)


        db.runTransaction { transaction ->
            val snapshot = transaction.get(pipeInventoryRef)

            if (snapshot.exists()) {
                val existing = snapshot.toObject(PipeStock::class.java)
                val newWeight = (existing?.approxWeight ?: 0.0) + pipeItem.approxWeight
                val newPipeNumber = (existing?.quantity ?: 0) + pipeItem.quantity
                transaction.update(pipeInventoryRef, "approxWeight", newWeight)
                transaction.update(pipeInventoryRef, "quantity", newPipeNumber)
            } else {
                // Create inventory with only necessary fields
                val newPipeInventoryMap = mapOf(
                    "coilSize" to pipeItem.coilSize,
                    "coilStockId" to coilId,
                    "pipeType" to pipeItem.pipeType,
                    "pipeSize" to pipeItem.pipeSize,
                    "gauge" to pipeItem.gauge,
                    "grade" to pipeItem.grade,
                    "approxWeight" to pipeItem.approxWeight,
                    "quantity" to pipeItem.quantity,
                    "timestamp" to pipeItem.timestamp,
                    "inventoryId" to pipeInventoryId
                )
                transaction.set(pipeInventoryRef, newPipeInventoryMap)
            }

            val entryData = pipeItem.copy(
                inventoryId = pipeInventoryId,
                entryUserId = entryUserId ?: "unknown_user",
                entryUserName = entryUserName ?: "Unknown"
            )
            val pipeEntryRef = db.collection(PIPE_ENTRY_DB).document()
            transaction.set(pipeEntryRef, entryData)
        }.await()

        //  SCRAP ENTRY & INVENTORY UPDATE
        val scrapInventoryId = buildScrapInventoryId(scrapItem.gauge, scrapItem.grade)
        val scrapInventoryRef = db.collection(SCRAP_INVENTORY_DB).document(scrapInventoryId)


        db.runTransaction { transaction ->
            val snapshot = transaction.get(scrapInventoryRef)

            if (snapshot.exists()) {
                val existing = snapshot.toObject(ScrapStock::class.java)
                val newWeight = (existing?.weight ?: 0.0) + scrapItem.weight
                transaction.update(scrapInventoryRef, "weight", newWeight)
            } else {
                // Create inventory with only necessary fields
                val newScrapInventoryMap = mapOf(
                    "gauge" to scrapItem.gauge,
                    "grade" to scrapItem.grade,
                    "weight" to scrapItem.weight,
                    "timestamp" to scrapItem.timestamp,
                    "inventoryId" to scrapInventoryId,
                    "coilStockId" to coilId,
                )
                transaction.set(scrapInventoryRef, newScrapInventoryMap)
            }

            val entryData = scrapItem.copy(
                inventoryId = scrapInventoryId,
                entryUserId = entryUserId ?: "unknown_user",
                entryUserName = entryUserName ?: "Unknown"
            )
            val scrapEntryRef = db.collection(SCRAP_DB).document()
            transaction.set(scrapEntryRef, entryData)
        }.await()


        //  CutPiece ENTRY & INVENTORY UPDATE
        val cutPieceInventoryId = buildCutPieceInventoryId(cutPieceItem.gauge, cutPieceItem.grade)
        val cutPieceInventoryRef = db.collection(CUT_PIECE_INVENTORY_DB).document(cutPieceInventoryId)


        db.runTransaction { transaction ->
            val snapshot = transaction.get(cutPieceInventoryRef)

            if (snapshot.exists()) {
                val existing = snapshot.toObject(CutPieceStock::class.java)
                val newWeight = (existing?.weight ?: 0.0) + cutPieceItem.weight
                transaction.update(cutPieceInventoryRef, "weight", newWeight)
            } else {
                // Create inventory with only necessary fields
                val newCutPieceInventoryMap = mapOf(
                    "gauge" to cutPieceItem.gauge,
                    "grade" to cutPieceItem.grade,
                    "weight" to cutPieceItem.weight,
                    "timestamp" to cutPieceItem.timestamp,
                    "inventoryId" to cutPieceInventoryId,
                    "coilStockId" to coilId,
                )
                transaction.set(cutPieceInventoryRef, newCutPieceInventoryMap)
            }

            val entryData = cutPieceItem.copy(
                inventoryId = scrapInventoryId,
                entryUserId = entryUserId ?: "unknown_user",
                entryUserName = entryUserName ?: "Unknown"
            )
            val cutPieceEntryRef = db.collection(CUT_PIECE_DB).document()
            transaction.set(cutPieceEntryRef, entryData)
        }.await()



        // --- REDUCE FROM COIL INVENTORY ---
        db.runTransaction { transaction ->
            val coilSnapshot = transaction.get(coilInventoryRef)
            if (coilSnapshot.exists()) {
                val existingCoil = coilSnapshot.toObject(CoilStockItem::class.java)
                val totalReducedWeight =
                    pipeItem.approxWeight + scrapItem.weight + cutPieceItem.weight
                val newCoilWeight = (existingCoil?.weight ?: 0.0) - totalReducedWeight

                if (newCoilWeight < 0) {
                    throw IllegalStateException("Coil weight cannot go below zero!")
                }

                transaction.update(coilInventoryRef, "weight", newCoilWeight)


            } else {
                throw IllegalStateException("Coil not found for ID: $coilId")
            }
        }.await()



    }

    // --- REDUCE FROM PIPE INVENTORY and ADD PIPE OUTFLOW ENTRIES ---
    suspend fun addPipeOutflow(
        pipeItem: PipeStock,
        entryUserId: String?,
        entryUserName: String?
    ) {

        val pipeInventoryId = fetchPipeInventoryId(pipeItem.grade, pipeItem.gauge, pipeItem.pipeSize, pipeItem.pipeType)
        val pipeInventoryRef = db.collection(PIPE_DB).document(pipeInventoryId ?: "")

        db.runTransaction { transaction ->
            val pipeOutflowSnapshot = transaction.get(pipeInventoryRef)
            if (pipeOutflowSnapshot.exists()) {
                val existing = pipeOutflowSnapshot.toObject(PipeStock::class.java)
                val newWeight = (existing?.approxWeight ?: 0.0) - pipeItem.approxWeight
                val newPipeNumber = (existing?.quantity ?: 0) - pipeItem.quantity


                if (newWeight < 0 || newPipeNumber < 0) {
                    throw IllegalStateException("Pipe outflow cannot reduce inventory below zero!")
                }

                transaction.update(pipeInventoryRef, "approxWeight", newWeight)
                transaction.update(pipeInventoryRef, "quantity", newPipeNumber)

                val pipeOutflowInventoryRef = db.collection(PIPE_OUTFLOW_DB).document()

                val newPipeOutflowEntry = mapOf(
                    "inventoryId" to pipeOutflowInventoryRef.id,
                    "pipeInventoryId" to pipeInventoryId,
                    "pipeType" to pipeItem.pipeType,
                    "pipeSize" to pipeItem.pipeSize,
                    "gauge" to pipeItem.gauge,
                    "grade" to pipeItem.grade,
                    "approxWeight" to pipeItem.approxWeight,
                    "quantity" to pipeItem.quantity,
                    "timestamp" to pipeItem.timestamp,
                    "entryUserId" to (entryUserId ?: "unknown_user"),
                    "entryUserName" to (entryUserName ?: "Unknown")
                )
                transaction.set(pipeOutflowInventoryRef, newPipeOutflowEntry)

            } else {
                throw IllegalStateException("Pipe not found for ID: $pipeInventoryId")
            }

        }.await()
    }


    // --- REDUCE FROM SCRAP & CUT PIECE INVENTORY and ADD SCRAP AND CUT PIECE OUTFLOW ENTRIES ---
    suspend fun addScrapCutPieceOutflow(
        scrapItem: ScrapStock,
        cutPieceItem: CutPieceStock,
        entryUserId: String?,
        entryUserName: String?
    ) {

        val scrapInventoryId = fetchScrapInventoryId(scrapItem.grade,scrapItem.gauge)
        val scrapInventoryRef = db.collection(SCRAP_INVENTORY_DB).document(scrapInventoryId?:"")

        val cutPieceInventoryId = fetchCutPieceInventoryId(cutPieceItem.grade,cutPieceItem.gauge)
        val cutPieceInventoryRef = db.collection(CUT_PIECE_INVENTORY_DB).document(cutPieceInventoryId?:"")


        db.runTransaction { transaction ->
            val scrapSnapshot = transaction.get(scrapInventoryRef)
            if (scrapSnapshot.exists()) {
                val existing = scrapSnapshot.toObject(ScrapStock::class.java)
                val newWeight = (existing?.weight ?: 0.0) - scrapItem.weight

                if (newWeight < 0){
                    throw IllegalStateException("Scrap Outflow Cannot Reduce below zero")
                }
                transaction.update(scrapInventoryRef,"weight",newWeight)

                val scrapOutflowEntryRef = db.collection(SCRAP_OUTFLOW_DB).document()

                val newScrapOutflowInventoryMap = mapOf(
                    "gauge" to scrapItem.gauge,
                    "grade" to scrapItem.grade,
                    "weight" to scrapItem.weight,
                    "timestamp" to scrapItem.timestamp,
                    "inventoryId" to scrapInventoryId,
                    "entryUserId" to entryUserId,
                    "entryUserName" to entryUserName
                )
                transaction.set(scrapOutflowEntryRef, newScrapOutflowInventoryMap)



            }
        }.await()


        db.runTransaction { transaction ->
            val cutPieceSnapshot = transaction.get(cutPieceInventoryRef)
            if (cutPieceSnapshot.exists()) {
                val existing = cutPieceSnapshot.toObject(CutPieceStock::class.java)
                val newWeight = (existing?.weight ?: 0.0) - scrapItem.weight

                if (newWeight < 0){
                    throw IllegalStateException("CutPiece Outflow Cannot Reduce below zero")
                }
                transaction.update(cutPieceInventoryRef,"weight",newWeight)

                val cutPieceOutflowEntryRef = db.collection(CUT_PIECE_OUTFLOW_INVENTORY_DB).document()

                val newCutPieceOutflowInventoryMap = mapOf(
                    "gauge" to scrapItem.gauge,
                    "grade" to scrapItem.grade,
                    "weight" to scrapItem.weight,
                    "timestamp" to scrapItem.timestamp,
                    "inventoryId" to scrapInventoryId,
                    "entryUserId" to entryUserId,
                    "entryUserName" to entryUserName
                )
                transaction.set(cutPieceOutflowEntryRef, newCutPieceOutflowInventoryMap)

            }
        }.await()
    }







    //  Delete pipe entry and update inventory
//    suspend fun deletePipeEntry(itemId: String, item: PipeStock,) {
//        // Delete entry
//        db.collection(PIPE_ENTRY_DB).document(itemId).delete().await()
//
//        //  Reduce inventory
//        val query = db.collection(PIPE_DB)
//            .whereEqualTo("coilSize", item.coilSize)
//            .whereEqualTo("pipeSize", item.pipeSize)
//            .whereEqualTo("gauge", item.gauge)
//            .whereEqualTo("grade", item.grade)
//            .get()
//            .await()
//
//        if (!query.isEmpty) {
//            val doc = query.documents.first()
//            val existing = doc.toObject(PipeStock::class.java)
//            val newWeight = (existing?.approxWeight ?: 0.0) - item.approxWeight
//            val newQuantity = (existing?.quantity ?: 0) - item.quantity
//
//            db.collection(PIPE_DB)
//                .document(doc.id)
//                .update(
//                    mapOf(
//                        "weight" to newWeight.coerceAtLeast(0.0),
//                        "quantity" to newQuantity.coerceAtLeast(0)
//                    )
//                ).await()
//        }
//
//        //  Restore coil inventory
//        val totalUsed = item.approxWeight + item.approxWeight + item.cutPieceWeight
//        val queryCoil = db.collection(COIL_DB)
//            .whereEqualTo("size", item.coilSize)
//            .whereEqualTo("gauge", item.gauge)
//            .whereEqualTo("grade", item.grade)
//            .get()
//            .await()
//
//        if (!queryCoil.isEmpty) {
//            val doc = queryCoil.documents.first()
//            val existing = doc.toObject(CoilStockItem::class.java)
//            val newWeight = (existing?.weight ?: 0.0).toDouble() + totalUsed
//
//            db.collection(COIL_DB)
//                .document(doc.id)
//                .update("weight", newWeight)
//                .await()
//        }
//    }


    // ---------- LIVE FLOW FOR PIPE INVENTORY ----------
    fun getPipeInventoryFlow(): Flow<List<PipeStockWithId>> = callbackFlow {
        val listener = db.collection(PIPE_DB)
            .addSnapshotListener { snapshot, e ->
                if (e != null) { close(e); return@addSnapshotListener }
                val items = snapshot?.documents?.map { doc ->
                    PipeStockWithId(doc.id, doc.toObject(PipeStock::class.java)!!)
                } ?: emptyList()
                trySend(items)
            }
        awaitClose { listener.remove() }
    }

    // ---------- LIVE FLOW FOR PIPE ENTRY ----------
    fun getPipeEntriesFlow(): Flow<List<PipeStockWithId>> = callbackFlow {
        val listener = db.collection(PIPE_ENTRY_DB)
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, e ->
                if (e != null) { close(e); return@addSnapshotListener }
                val items = snapshot?.documents?.map { doc ->
                    PipeStockWithId(doc.id, doc.toObject(PipeStock::class.java)!!)
                } ?: emptyList()
                trySend(items)
            }
        awaitClose { listener.remove() }
    }

    // ---------- LIVE FLOW FOR SCRAP INVENTORY ----------
    fun getScrapInventoryFlow(): Flow<List<ScrapStockWithId>> = callbackFlow {
        val listener = db.collection(SCRAP_INVENTORY_DB)
            .addSnapshotListener { snapshot, e ->
                if (e != null) { close(e); return@addSnapshotListener }
                val items = snapshot?.documents?.map { doc ->
                    ScrapStockWithId(doc.id, doc.toObject(ScrapStock::class.java)!!)
                } ?: emptyList()
                trySend(items)
            }
        awaitClose { listener.remove() }
    }

    // ---------- LIVE FLOW FOR SCRAP ENTRY ----------
    fun getScrapEntriesFlow(): Flow<List<ScrapStockWithId>> = callbackFlow {
        val listener = db.collection(SCRAP_DB)
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, e ->
                if (e != null) { close(e); return@addSnapshotListener }
                val items = snapshot?.documents?.map { doc ->
                    ScrapStockWithId(doc.id, doc.toObject(ScrapStock::class.java)!!)
                } ?: emptyList()
                trySend(items)
            }
        awaitClose { listener.remove() }
    }

    // ---------- LIVE FLOW FOR CUT PIECE INVENTORY ----------
    fun getCutPieceInventoryFlow(): Flow<List<CutPieceStockWithId>> = callbackFlow {
        val listener = db.collection(CUT_PIECE_INVENTORY_DB)
            .addSnapshotListener { snapshot, e ->
                if (e != null) { close(e); return@addSnapshotListener }
                val items = snapshot?.documents?.map { doc ->
                    CutPieceStockWithId(doc.id, doc.toObject(CutPieceStock::class.java)!!)
                } ?: emptyList()
                trySend(items)
            }
        awaitClose { listener.remove() }
    }

    // ---------- LIVE FLOW FOR CUT PIECE ENTRY ----------
    fun getCutPieceEntriesFlow(): Flow<List<CutPieceStockWithId>> = callbackFlow {
        val listener = db.collection(CUT_PIECE_DB)
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, e ->
                if (e != null) { close(e); return@addSnapshotListener }
                val items = snapshot?.documents?.map { doc ->
                    CutPieceStockWithId(doc.id, doc.toObject(CutPieceStock::class.java)!!)
                } ?: emptyList()
                trySend(items)
            }
        awaitClose { listener.remove() }
    }


     //-----------------LIVE FLOW FOR PIPE OUTFLOW INVENTORY ----------
     fun getPipeOutflowEntriesFlow(): Flow<List<PipeStockWithId>> = callbackFlow {
         val listener = db.collection(PIPE_OUTFLOW_DB)
             .orderBy("timestamp")
             .addSnapshotListener { snapshot, e ->
                 if (e != null) { close(e); return@addSnapshotListener }
                 val items = snapshot?.documents?.map { doc ->
                     PipeStockWithId(doc.id, doc.toObject(PipeStock::class.java)!!)
                 } ?: emptyList()
                 trySend(items)
             }
         awaitClose { listener.remove() }
     }

    //--------------------LIVE FLOW FOR SCRAP OUTFLOW ---------------

    fun getScrapOutflowEntriesFlow(): Flow<List<ScrapStockWithId>> = callbackFlow {
        val listener = db.collection(SCRAP_OUTFLOW_DB)
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, e ->
                if (e != null) { close(e); return@addSnapshotListener }
                val items = snapshot?.documents?.map { doc ->
                    ScrapStockWithId(doc.id, doc.toObject(ScrapStock::class.java)!!)
                } ?: emptyList()
                trySend(items)
            }
        awaitClose { listener.remove() }
    }

    fun getCutPieceOutflowEntriesFlow(): Flow<List<CutPieceStockWithId>> = callbackFlow {
        val listener = db.collection(CUT_PIECE_OUTFLOW_INVENTORY_DB)
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, e->
                if (e != null) { close(e); return@addSnapshotListener}
                val items = snapshot?.documents?.map { doc ->
                    CutPieceStockWithId(doc.id,doc.toObject(CutPieceStock::class.java)!!)
                }?: emptyList()
                trySend(items)

            }
        awaitClose {listener.remove()}
    }

    // ---------- DATA CLASSES WITH FIRESTORE ID ----------
    data class PipeStockWithId(val id: String, val pipeStock: PipeStock)
    data class ScrapStockWithId(val id: String, val scrapStock: ScrapStock)
    data class CutPieceStockWithId(val id: String, val cutPieceStock: CutPieceStock)

}
