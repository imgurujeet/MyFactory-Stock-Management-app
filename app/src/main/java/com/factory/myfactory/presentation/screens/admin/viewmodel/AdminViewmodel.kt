package com.factory.myfactory.presentation.screens.admin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.factory.myfactory.data.repositories.CoilRepository
import com.factory.myfactory.data.repositories.UserRepository
import com.imgurujeet.stockease.data.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _uploadResult = MutableStateFlow<String?>(null) // success/error
    val uploadResult = _uploadResult.asStateFlow()


    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var userListenerJob: Job? = null

    init {
        observeUsersRealtime()
    }

    /** Real-time updates from Firestore */



    private fun observeUsersRealtime() {
        userListenerJob?.cancel()
        userListenerJob = viewModelScope.launch {
            userRepository.observeUsers()
                .catch { _error.value = it.localizedMessage }
                .collect { userList ->
                    _users.value = userList
                }
        }
    }

    /** Fetch users once (optional) */
    fun fetchUsers() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                _users.value = userRepository.getUsers()
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _loading.value = false
            }
        }
    }

    /** Fetch single user */
    fun fetchUser(uid: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val user = userRepository.getUserById(uid)
                user?.let { _users.value = listOf(it) }
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _loading.value = false
            }
        }
    }

    /** Update user details */
    fun updateUser(user: User, onComplete: (Boolean, String?) -> Unit = { _, _ -> }) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                userRepository.updateUser(user)
                onComplete(true, null)
            } catch (e: Exception) {
                _error.value = e.localizedMessage
                onComplete(false, e.localizedMessage)
            } finally {
                _loading.value = false
            }
        }
    }


    fun deleteUser(docId: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                userRepository.deleteUser(docId) // call repo method
                _loading.value = false
               _uploadResult.value = "Deleted"
            } catch (e: Exception) {
                _loading.value = false
                _uploadResult.value = e.message
            }
        }
    }

    fun addUser(user: User) {
        viewModelScope.launch {
            try {
                _loading.value = true
                userRepository.addUser(user) // your Firebase upload call
                _loading.value = false
                _uploadResult.value = "success"
            } catch (e: Exception) {
                _loading.value = false
                _uploadResult.value = e.message
            }
        }
    }

}
