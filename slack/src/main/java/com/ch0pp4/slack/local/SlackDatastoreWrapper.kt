package com.ch0pp4.slack.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal val Context.slackDataStore by preferencesDataStore(name = "slack")

class SlackDatastoreWrapper(
    private val context: Context
) {
    // https://developer.android.com/topic/libraries/architecture/datastore?hl=ko#prefs-vs-proto

    private val TAG_ID = stringPreferencesKey("tag_id")
    private val IS_NEW = booleanPreferencesKey("is_new")

    val existId: Flow<String> = context.slackDataStore.data.map { pref ->
        pref[TAG_ID] ?: ""
    }

    suspend fun setId(id: String) {
        context.slackDataStore.edit { pref ->
            pref[TAG_ID] = id
        }
    }

    val isNewFlag: Flow<Boolean> = context.slackDataStore.data.map { pref ->
        pref[IS_NEW] ?: false
    }

    suspend fun setIsNewFlag(value: Boolean) {
        context.slackDataStore.edit { pref ->
            pref[IS_NEW] = value
        }
    }
}