package com.ch0pp4.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.appDataStore by preferencesDataStore(name = "AppDataStore")

class AppDataStore(
    private val context: Context
) {
    // https://developer.android.com/topic/libraries/architecture/datastore?hl=ko#prefs-vs-proto

    private val WORKER_TERM = intPreferencesKey("worker_term_minute")
    private val DAME_PRODUCT_ID = stringPreferencesKey("dame_product_id")
    private val IS_NEW_DAME = booleanPreferencesKey("is_new_dame")

    val workerTerm: Flow<Int> = context.appDataStore.data.map { pref ->
        pref[WORKER_TERM] ?: -1
    }

    suspend fun setWorkerTerm(minute: Int) {
        context.appDataStore.edit { pref ->
            pref[WORKER_TERM] = minute
        }
    }

    val existId: Flow<String> = context.appDataStore.data.map { pref ->
        pref[DAME_PRODUCT_ID] ?: ""
    }

    suspend fun setId(id: String) {
        context.appDataStore.edit { pref ->
            pref[DAME_PRODUCT_ID] = id
        }
    }

    val isNewFlag: Flow<Boolean> = context.appDataStore.data.map { pref ->
        pref[IS_NEW_DAME] ?: false
    }

    suspend fun setIsNewFlag(value: Boolean) {
        context.appDataStore.edit { pref ->
            pref[IS_NEW_DAME] = value
        }
    }
}