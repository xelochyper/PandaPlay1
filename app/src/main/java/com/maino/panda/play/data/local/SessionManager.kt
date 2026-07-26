package com.maino.panda.play.data.local

import android.content.Context
import android.content.SharedPreferences
import java.util.UUID

/**
 * SessionManager handles persistent storage of authentication tokens,
 * device unique IDs, and user login state using SharedPreferences.
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "panda_play_prefs"
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_DEVICE_ID = "device_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_FULL_NAME = "full_name"
        private const val KEY_EMAIL = "email"
        private const val KEY_IS_DARK_MODE = "is_dark_mode"
    }

    fun isDarkMode(): Boolean {
        return prefs.getBoolean(KEY_IS_DARK_MODE, false)
    }

    fun setDarkMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_IS_DARK_MODE, enabled).apply()
    }

    /**
     * Get or generate a persistent Device ID for login API requests
     */
    fun getDeviceId(): String {
        var deviceId = prefs.getString(KEY_DEVICE_ID, null)
        if (deviceId == null || deviceId.trim().isEmpty()) {
            deviceId = "PANDA_" + UUID.randomUUID().toString().take(12)
            prefs.edit().putString(KEY_DEVICE_ID, deviceId).apply()
        }
        return deviceId
    }

    fun saveAuthSession(token: String, username: String, fullName: String, email: String) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_USERNAME, username)
            .putString(KEY_FULL_NAME, fullName)
            .putString(KEY_EMAIL, email)
            .apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false) && !getToken().isNullOrEmpty()
    }

    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    fun getUsername(): String {
        return prefs.getString(KEY_USERNAME, "PandaGamer") ?: "PandaGamer"
    }

    fun getFullName(): String {
        return prefs.getString(KEY_FULL_NAME, "Panda Play User") ?: "Panda Play User"
    }

    fun getEmail(): String {
        return prefs.getString(KEY_EMAIL, "user@maino.web.id") ?: "user@maino.web.id"
    }

    fun updateProfileDetails(fullName: String) {
        prefs.edit()
            .putString(KEY_FULL_NAME, fullName)
            .apply()
    }

    fun logout() {
        prefs.edit()
            .remove(KEY_TOKEN)
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .remove(KEY_USERNAME)
            .remove(KEY_FULL_NAME)
            .remove(KEY_EMAIL)
            .apply()
    }
}
