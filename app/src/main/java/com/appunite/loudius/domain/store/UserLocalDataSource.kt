package com.appunite.loudius.domain.store

import android.content.Context
import android.content.SharedPreferences
import com.appunite.loudius.network.model.AccessToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserLocalDataSource @Inject constructor(@ApplicationContext context: Context) {

    companion object {
        private const val FILE_NAME = "com.appunite.loudius.sharedPreferences"
        private const val KEY_ACCESS_TOKEN = "access_token"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    fun saveAccessToken(accessToken: AccessToken) {
        sharedPreferences.edit().putString(KEY_ACCESS_TOKEN, accessToken).apply()
    }

    fun getAccessToken(): AccessToken = sharedPreferences.getString(KEY_ACCESS_TOKEN, null) ?: ""
}
