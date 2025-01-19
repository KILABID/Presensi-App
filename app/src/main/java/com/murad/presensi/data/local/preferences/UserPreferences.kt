package com.murad.presensi.data.local.preferences

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "login")

class UserPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    suspend fun saveSession(pref: PrefModel) {
        dataStore.edit { preferences ->
            preferences[ID] = pref.id
            preferences[IS_LOGIN] = pref.isLogin
            preferences[IS_CHECK_IN] = pref.isCheckIn
            preferences[USERNAME] = pref.username
            preferences[ROLE] = pref.role
        }
    }

    suspend fun checkInStatus(isCheckIn: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_CHECK_IN] = isCheckIn
        }
    }

    fun getSession(): Flow<PrefModel> {
        return dataStore.data.map { preferences ->
            PrefModel(
                preferences[ID] ?: "",
                preferences[IS_LOGIN] ?: false,
                preferences[IS_CHECK_IN] ?: false,
                preferences[USERNAME] ?: "",
                preferences[ROLE] ?: "",
            ).also { userModel ->
                // Logging for debugging
                Log.d( "UserPreferences", "getSession: $userModel")
            }
        }
    }


    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {

        private val ID = stringPreferencesKey("id")
        private val IS_LOGIN = booleanPreferencesKey("isLogin")
        private val IS_CHECK_IN = booleanPreferencesKey("isCheckIn")
        private val USERNAME = stringPreferencesKey("username")
        private val ROLE = stringPreferencesKey("role")

        private var INSTANCE: UserPreferences? = null
        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}