package com.murad.presensi.di

import android.content.Context
import com.murad.presensi.data.local.preferences.UserPreferences
import com.murad.presensi.data.local.preferences.dataStore
import com.murad.presensi.data.repository.AttendanceRepository
import com.murad.presensi.data.repository.UserRepository
import com.murad.presensi.data.repository.PreferencesRepository

object Injection {

    fun providePreferencesRepository(context: Context): PreferencesRepository {
        val pref = UserPreferences.getInstance(context.dataStore)
        return PreferencesRepository.getInstance(pref)
    }

    fun provideLoginRepository(context: Context): UserRepository {
        val prefRepo = providePreferencesRepository(context)
        return UserRepository.getInstance(prefRepo)
    }

    fun provideAttendanceRepository(context: Context): AttendanceRepository {
        val pref = UserPreferences.getInstance(context.dataStore)
        return AttendanceRepository.getInstance(pref)
    }
}
