package com.factory.myfactory.presentation.screens.admin.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.factory.myfactory.data.models.RegisteredUser
import com.factory.myfactory.data.repositories.RegisteredUserRepository
import com.google.firebase.auth.FirebaseAuth
import com.imgurujeet.stockease.data.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisteredUserViewmodel @Inject constructor(
    private val registeredUserRepository: RegisteredUserRepository

) : ViewModel(){

    private val _registeredUser = MutableStateFlow<List<RegisteredUser>>(emptyList())

    val registeredUser: StateFlow<List<RegisteredUser>> = _registeredUser

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _uploadResult = MutableStateFlow<String?>(null) // success/error
    val uploadResult = _uploadResult.asStateFlow()


    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var registeredUserListenerJob: Job?=null

    init {
        observeRegisteredUsersRealtime()
    }


    private fun observeRegisteredUsersRealtime() {
        registeredUserListenerJob?.cancel()
        registeredUserListenerJob = viewModelScope.launch {
            registeredUserRepository.observeRegisteredUsers()
                .catch { _error.value = it.localizedMessage }
                .collect { userList ->
                    _registeredUser.value = userList
                }
        }
    }

    fun deleteUser(docId: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                registeredUserRepository.deleteRegisteredUser(docId) // call repo method
                _loading.value = false
                _uploadResult.value = "Deleted"
            } catch (e: Exception) {
                _loading.value = false
                _uploadResult.value = e.message
            }
        }
    }

    fun addUser(user: RegisteredUser) {
        viewModelScope.launch {
            try {
                _loading.value = true
                registeredUserRepository.registerUser(user) // your Firebase upload call
                _loading.value = false
                _uploadResult.value = "success"
            } catch (e: Exception) {
                _loading.value = false
                _uploadResult.value = e.message
            }
        }
    }

//    fun updateUser(user: RegisteredUser, onComplete: (Boolean, String?) -> Unit = { _, _ -> }) {
//        viewModelScope.launch {
//            _loading.value = true
//            _error.value = null
//            try {
//                // 1 Update in Firestore
//                registeredUserRepository.updateRegisteredUser(user)
//
//                // 2 Update the local StateFlow immediately for instant UI feedback
//                val currentList = _registeredUser.value.toMutableList()
//                val index = currentList.indexOfFirst { it.uid == user.uid }
//                if (index != -1) {
//                    currentList[index] = user
//                    _registeredUser.value = currentList
//                }
//
//                onComplete(true, null)
//            } catch (e: Exception) {
//                _error.value = e.localizedMessage
//                onComplete(false, e.localizedMessage)
//            } finally {
//                _loading.value = false
//            }
//        }
//    }

    fun updateUser(user: RegisteredUser, onComplete: (Boolean, String?) -> Unit = { _, _ -> }) {
        val currentPhone = FirebaseAuth.getInstance().currentUser?.phoneNumber
        // If trying to deactivate yourself
        if (user.phone == currentPhone) {
            onComplete(false, "ooh ho! You are deactivating yourself")
            return
        }

        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                // Toggle the active state
                val newActive = !(user.active ?: true)


                // Update Firestore (only active field)
                registeredUserRepository.updateRegisteredUserStatus(user.uid, newActive)

                // Update local list for instant UI feedback
                val currentList = _registeredUser.value.toMutableList()
                val index = currentList.indexOfFirst { it.uid == user.uid }
                if (index != -1) {
                    currentList[index] = user.copy(active = newActive)
                    _registeredUser.value = currentList
                }

                onComplete(true, null)
            } catch (e: Exception) {
                _error.value = e.localizedMessage
                onComplete(false, e.localizedMessage)
            } finally {
                _loading.value = false
            }
        }
    }




}