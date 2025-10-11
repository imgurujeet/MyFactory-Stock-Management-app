package com.factory.myfactory.presentation.screens.coil.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.factory.myfactory.data.models.CoilStockItem
import com.factory.myfactory.data.repositories.CoilRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoilViewModel @Inject constructor(
    private val coilRepository: CoilRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _uploadResult = MutableStateFlow<String?>(null) // success/error
    val uploadResult: StateFlow<String?> = _uploadResult.asStateFlow()

    private val _operationResult = MutableStateFlow<String?>(null) // for update/delete results
    val operationResult: StateFlow<String?> = _operationResult.asStateFlow()


    // --- ENTRY METHODS ---
    fun addStock(item: CoilStockItem) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // 🔹 Log the item details before sending to repository
                Log.d("CoilViewModel", "Adding stock item: $item")

                // Just pass the item as-is, it already has entryUserId and entryUsername
                coilRepository.addStock(
                    item,
                    item.entryUserId,
                    item.entryUsername
                )

                _uploadResult.value = "success"

                // 🔹 Log success
                Log.d("CoilViewModel", "Stock item added successfully: ${item.size}, ${item.gauge}, ${item.grade}")
            } catch (e: Exception) {
                _uploadResult.value = e.message

                // 🔹 Log the exception
                Log.e("CoilViewModel", "Error adding stock item", e)
            } finally {
                _isLoading.value = false
            }
        }
    }




    fun updateStock(itemId: String, updatedItem: CoilStockItem) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                coilRepository.updateStock(itemId, updatedItem)
                _operationResult.value = "update_success"
            } catch (e: Exception) {
                _operationResult.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteStock(docId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                coilRepository.deleteStock(docId)
                _uploadResult.value = "Deleted"
            } catch (e: Exception) {
                _uploadResult.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearOperationResult() {
        _operationResult.value = null
    }

    // --- FLOWS ---
    // Entry-level flow (all entries, transaction log)
    val entryStockList: StateFlow<List<CoilRepository.CoilStockItemWithId>> =
        coilRepository.getAllEntriesFlow()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // Summarized inventory flow (merged weights)
    val inventoryStockList: StateFlow<List<CoilRepository.CoilStockItemWithId>> =
        coilRepository.getInventoryFlow()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
}





