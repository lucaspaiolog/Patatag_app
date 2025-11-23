package br.edu.fatecpg.patatagapp.utils

import android.content.Context
import br.edu.fatecpg.patatagapp.api.UserDto

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("patatag_session", Context.MODE_PRIVATE)

    companion object {
        const val KEY_USER_NAME = "user_name"
        const val KEY_USER_EMAIL = "user_email"
        const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    fun saveUser(user: UserDto) {
        val editor = prefs.edit()
        editor.putString(KEY_USER_NAME, user.name)
        editor.putString(KEY_USER_EMAIL, user.email)
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.apply()
    }

    fun getUserName(): String? {
        return prefs.getString(KEY_USER_NAME, "Usuário")
    }

    fun getUserEmail(): String? {
        return prefs.getString(KEY_USER_EMAIL, "email@exemplo.com")
    }

    fun clearSession() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }
}