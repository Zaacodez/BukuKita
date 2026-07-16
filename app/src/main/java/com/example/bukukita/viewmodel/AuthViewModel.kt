package com.example.bukukita.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bukukita.data.repository.AuthRepository
import com.example.bukukita.data.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(repository.getCurrentUser() != null)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _isUpdating = MutableStateFlow(false)
    val isUpdating: StateFlow<Boolean> = _isUpdating.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    fun getCurrentEmail(): String? {
        return repository.getCurrentUser()?.email
    }

    fun fetchProfile() {
        val user = repository.getCurrentUser()
        if (user != null) {
            viewModelScope.launch {
                _isLoading.value = true
                val profile = repository.getUserProfile(user.id)
                _userProfile.value = profile
                _isLoading.value = false
            }
        }
    }

    fun register(email: String, password: String, username: String, fullName: String, avatarBytes: ByteArray?) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                repository.signUp(email, password, username, fullName, avatarBytes)
                _isLoggedIn.value = true // Automatically log in on successful registration
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Terjadi kesalahan saat mendaftar"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun login(emailOrUsername: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                repository.signIn(emailOrUsername, password)
                _isLoggedIn.value = true
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Terjadi kesalahan saat masuk"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                repository.signOut()
                _isLoggedIn.value = false
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Terjadi kesalahan saat keluar"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfile(username: String, fullName: String, avatarBytes: ByteArray?, newEmail: String?, onSuccess: () -> Unit) {
        val user = repository.getCurrentUser()
        if (user != null) {
            viewModelScope.launch {
                _isUpdating.value = true
                _errorMessage.value = null
                try {
                    val success = repository.updateUserProfile(user.id, username, fullName, avatarBytes, newEmail)
                    if (success) {
                        fetchProfile()
                        onSuccess()
                    } else {
                        _errorMessage.value = "Gagal memperbarui profil"
                    }
                } catch (e: Exception) {
                    _errorMessage.value = e.message ?: "Terjadi kesalahan"
                } finally {
                    _isUpdating.value = false
                }
            }
        }
    }
}

class AuthViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}