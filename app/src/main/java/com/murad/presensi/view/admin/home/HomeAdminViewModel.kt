package com.murad.presensi.view.admin.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.murad.presensi.data.repository.AttendanceRepository
import com.murad.presensi.data.repository.PreferencesRepository
import com.murad.presensi.data.repository.UserRepository
import kotlinx.coroutines.launch

class HomeAdminViewModel(
    private val attendanceRepository: AttendanceRepository,
    private val userRepository: UserRepository,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {

    fun logout() {
        viewModelScope.launch {
            preferencesRepository.logout()
        }
    }
}