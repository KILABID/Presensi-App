package com.murad.presensi.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.murad.presensi.data.repository.AttendanceRepository
import com.murad.presensi.data.repository.UserRepository
import com.murad.presensi.data.repository.PreferencesRepository
import com.murad.presensi.di.Injection
import com.murad.presensi.view.admin.home.HomeAdminViewModel
import com.murad.presensi.view.admin.manage_user.ManagerUserViewModel
import com.murad.presensi.view.login.LoginViewModel
import com.murad.presensi.view.user.history.HistoryViewModel
import com.murad.presensi.view.user.home.HomeUserViewModel

class ViewModelFactory(
    private val repository: PreferencesRepository,
    private val userRepository: UserRepository,
    private val attendanceRepository: AttendanceRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeUserViewModel::class.java) -> {
                HomeUserViewModel(repository, attendanceRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userRepository, repository) as T
            }
            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> {
                HistoryViewModel(repository) as T
            }
            modelClass.isAssignableFrom(HomeAdminViewModel::class.java) ->{
                HomeAdminViewModel(attendanceRepository, userRepository, repository) as T
            }
            modelClass.isAssignableFrom(ManagerUserViewModel::class.java) ->{
                ManagerUserViewModel(userRepository, attendanceRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun clearInstance() {
            PreferencesRepository.clearInstance()
            INSTANCE = null
        }

        fun getInstance(context: Context): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ViewModelFactory(
                    Injection.providePreferencesRepository(context),
                    Injection.provideLoginRepository(context),
                    Injection.provideAttendanceRepository(context)
                )
            }.also { INSTANCE = it }
        }
    }
}