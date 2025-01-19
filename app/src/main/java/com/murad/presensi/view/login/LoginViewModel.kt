package com.murad.presensi.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.murad.presensi.data.local.model.UserModel
import com.murad.presensi.data.local.preferences.PrefModel
import com.murad.presensi.data.repository.PreferencesRepository
import com.murad.presensi.data.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: UserRepository,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {
    var loginResult = MutableLiveData<Result<UserModel>>()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true // Set loading to true at the start
            try {
                val result = repository.login(email, password)
                loginResult.postValue(result)
            } catch (e: Exception) {
                loginResult.postValue(Result.failure(e))
            } finally {
                _isLoading.value = false // Set loading to false at the end
            }
        }
    }


    fun getSession(): LiveData<PrefModel> {
        return preferencesRepository.getSession().asLiveData()
    }

}

