/*
 * Copyright 2023 AppUnite S.A.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.appunite.loudius.domain.store

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.appunite.loudius.network.model.AccessToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface EncryptedPrefs {
    fun saveAccessToken(accessToken: AccessToken)
    fun getAccessToken(): AccessToken
}

class EncryptedPrefsImpl @Inject constructor(@ApplicationContext context: Context) :
    EncryptedPrefs {

    companion object {
        private const val FILE_NAME = "com.appunite.loudius.sharedPreferences"
        private const val KEY_ACCESS_TOKEN = "access_token"
    }

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    override fun saveAccessToken(accessToken: AccessToken) {
        sharedPreferences.edit().putString(KEY_ACCESS_TOKEN, accessToken).apply()
    }

    override fun getAccessToken(): AccessToken =
        sharedPreferences.getString(KEY_ACCESS_TOKEN, null) ?: ""
}
