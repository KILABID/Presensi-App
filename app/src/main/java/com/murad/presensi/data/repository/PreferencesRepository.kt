package com.murad.presensi.data.repository

import com.murad.presensi.data.local.preferences.PrefModel
import com.murad.presensi.data.local.preferences.UserPreferences
import kotlinx.coroutines.flow.Flow

class PreferencesRepository private constructor(
    private val preferences: UserPreferences,
) {

    suspend fun saveSession(pref: PrefModel) {
        preferences.saveSession(pref)
    }

    fun getSession(): Flow<PrefModel> {
        return preferences.getSession()
    }

    suspend fun logout() {
        preferences.logout()
    }

    companion object {
        @Volatile
        private var INSTANCE: PreferencesRepository? = null

        fun clearInstance() {
            INSTANCE = null
        }

        fun getInstance(
            userPreferences: UserPreferences,
        ): PreferencesRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: PreferencesRepository(userPreferences)
            }.also { INSTANCE = it }
    }

}