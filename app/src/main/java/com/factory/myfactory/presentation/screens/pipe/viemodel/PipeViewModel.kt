package com.factory.myfactory.presentation.screens.pipe.viemodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.factory.myfactory.data.models.CutPieceStock
import com.factory.myfactory.data.models.PipeStock
import com.factory.myfactory.data.models.ScrapStock
import com.factory.myfactory.data.repositories.CoilRepository
import com.factory.myfactory.data.repositories.PipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PipeViewModel @Inject constructor(
    private val repository: PipeRepository
) : ViewModel() {

    // -------------------- UI STATES --------------------
    private val _pipeEntries = MutableStateFlow<List<PipeRepository.PipeStockWithId>>(emptyList())
    val pipeEntries: StateFlow<List<PipeRepository.PipeStockWithId>> = _pipeEntries.asStateFlow()

    private val _pipeInventory = MutableStateFlow<List<PipeRepository.PipeStockWithId>>(emptyList())
    val pipeInventory: StateFlow<List<PipeRepository.PipeStockWithId>> = _pipeInventory.asStateFlow()

    private val _scrapEntries = MutableStateFlow<List<PipeRepository.ScrapStockWithId>>(emptyList())
    val scrapEntries: StateFlow<List<PipeRepository.ScrapStockWithId>> = _scrapEntries.asStateFlow()

    private val _scrapInventory = MutableStateFlow<List<PipeRepository.ScrapStockWithId>>(emptyList())
    val scrapInventory: StateFlow<List<PipeRepository.ScrapStockWithId>> = _scrapInventory.asStateFlow()

    private val _cutPieceEntries = MutableStateFlow<List<PipeRepository.CutPieceStockWithId>>(emptyList())
    val cutPieceEntries: StateFlow<List<PipeRepository.CutPieceStockWithId>> = _cutPieceEntries.asStateFlow()

    private val _cutPieceInventory = MutableStateFlow<List<PipeRepository.CutPieceStockWithId>>(emptyList())
    val cutPieceInventory: StateFlow<List<PipeRepository.CutPieceStockWithId>> = _cutPieceInventory.asStateFlow()

    private val _pipeOutflowEntry = MutableStateFlow<List<PipeRepository.PipeStockWithId>>(emptyList())
    val pipeOutflowEntry: StateFlow<List<PipeRepository.PipeStockWithId>> = _pipeOutflowEntry.asStateFlow()

    private val _scrapOutflowEntry = MutableStateFlow<List<PipeRepository.ScrapStockWithId>>(emptyList())
    val scrapOutflowEntry: StateFlow<List<PipeRepository.ScrapStockWithId>> = _scrapOutflowEntry.asStateFlow()

    private val _cutPieceOutflowEntry = MutableStateFlow<List<PipeRepository.CutPieceStockWithId>>(emptyList())
    val cutPieceOutflowEntry: StateFlow<List<PipeRepository.CutPieceStockWithId>> = _cutPieceOutflowEntry.asStateFlow()



    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    // 🔹 Add pipe entry (handles scrap, cut piece, and coil automatically)
    fun addPipeScrapCutPieceStockWithEntry(pipe: PipeStock,scrap: ScrapStock,cutPiece: CutPieceStock) {
        viewModelScope.launch {
            try {
                _loading.value = true
                repository.addPipeStockWithScrapAndCutPiece(pipe, scrap, cutPiece, pipe.entryUserId, pipe.entryUserName)
                _successMessage.value = "Wohoo! Item added to Stock"
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to add pipe entry"
            } finally {
                _loading.value = false
            }
        }
    }
    fun addPipeOutflow(pipe: PipeStock) {
        viewModelScope.launch {

            try {
                _loading.value = true

                repository.addPipeOutflow(pipe, pipe.entryUserId, pipe.entryUserName)

                _successMessage.value = "Wohoo! Pipe outflow recorded"

            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to record pipe outflow"
            } finally {
                _loading.value = false
            }
        }
    }



    fun addScrapCutPieceOutflow(scrap: ScrapStock,cutPiece: CutPieceStock) {
        viewModelScope.launch {
            try {
                _loading.value = true
                repository.addScrapCutPieceOutflow(scrap,cutPiece,scrap.entryUserId,scrap.entryUserName)
                _successMessage.value = "Wohoo! outflow recorded"
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to record  outflow"
            } finally {
                _loading.value = false
            }
        }
    }

    // 🔹 Delete pipe entry (also restores coil stock)
//    fun deletePipeEntry(itemId: String, pipe: PipeStock) {
//        viewModelScope.launch {
//            try {
//                _loading.value = true
//                repository.deletePipeEntry(itemId, pipe)
//                _successMessage.value = "Pipe entry deleted successfully!"
//            } catch (e: Exception) {
//                _error.value = e.message ?: "Failed to delete pipe entry"
//            } finally {
//                _loading.value = false
//            }
//        }
//    }

    // -------------------- LOAD LIVE DATA --------------------

    fun loadPipeEntries() {
        viewModelScope.launch {
            repository.getPipeEntriesFlow().collectLatest { _pipeEntries.value = it }
        }
    }

    fun loadPipeInventory() {
        viewModelScope.launch {
            repository.getPipeInventoryFlow().collectLatest { _pipeInventory.value = it }
        }
    }

    fun loadScrapEntries() {
        viewModelScope.launch {
            repository.getScrapEntriesFlow().collectLatest { _scrapEntries.value = it }
        }
    }

    fun loadScrapInventory() {
        viewModelScope.launch {
            repository.getScrapInventoryFlow().collectLatest { _scrapInventory.value = it }
        }
    }

    fun loadCutPieceEntries() {
        viewModelScope.launch {
            repository.getCutPieceEntriesFlow().collectLatest { _cutPieceEntries.value = it }
        }
    }

    fun loadCutPieceInventory() {
        viewModelScope.launch {
            repository.getCutPieceInventoryFlow().collectLatest { _cutPieceInventory.value = it }
        }
    }

    fun loadPipeOutflowEntries() {
        viewModelScope.launch {
            repository.getPipeOutflowEntriesFlow().collectLatest { _pipeOutflowEntry.value = it }
        }
    }

    fun loadScrapOutflowEntries() {
        viewModelScope.launch {
            repository.getScrapOutflowEntriesFlow().collectLatest { _scrapOutflowEntry.value = it }
        }
    }
    fun loadCutPieceOutflowEntries() {
        viewModelScope.launch {
            repository.getCutPieceOutflowEntriesFlow().collectLatest { _cutPieceOutflowEntry.value = it }
        }
    }


    // 🔹 Clear success/error messages (useful for snackbars)
    fun clearMessages() {
        _error.value = null
        _successMessage.value = null
    }

    fun addPipeEntryError(message: String = "Please fill all required fields.") {
        _error.value = message
        _successMessage.value = null
    }







}
