package fr.myticket.moov.checker.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import fr.myticket.moov.checker.Enums.EnumTags


class Preferences(val context: Context) {
    private val preferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)


    fun saveToken(value: String) {
        preferences.edit().putString(EnumTags.ACCESS_TOKEN.value, value).apply()
    }

    fun getToken(): String? {
        return if (preferences.getString(EnumTags.ACCESS_TOKEN.value, "")?.isEmpty() == true) {
            null
        } else {
            preferences.getString(EnumTags.ACCESS_TOKEN.value, "")
        }
    }

    fun saveApiKey(value: String) {
        preferences.edit().putString(EnumTags.API_KEY.value, value).apply()
    }

    fun getApiKey(): String? {
        return if (preferences.getString(EnumTags.API_KEY.value, "")?.isEmpty() == true) {
            null
        } else {
            preferences.getString(EnumTags.API_KEY.value, "")
        }
    }


    fun removeStore() {
        preferences.edit().clear().apply()
    }


}


