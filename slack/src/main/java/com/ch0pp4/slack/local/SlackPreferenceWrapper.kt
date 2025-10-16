package com.ch0pp4.slack.local

import android.content.Context
import android.content.SharedPreferences

class SlackPreferenceWrapper(
    private val context: Context
) {
    // TODO DataStore migration

    private val TAG_ID = "tag_id"
    private val IS_NEW = "is_new"

    private val pref by lazy {
        context.getSharedPreferences("slack", Context.MODE_PRIVATE)
    }

    fun getExistId(): String = pref.getString(TAG_ID, "") ?: ""

    fun setId(id: String) {
        pref.edit().putString(TAG_ID, id).apply()
    }

    fun getIsNewFlag(): Boolean = pref.getBoolean(IS_NEW, false)

    fun setIsNewFlag(value: Boolean) {
        pref.edit().putBoolean(IS_NEW, value).apply()
    }
}