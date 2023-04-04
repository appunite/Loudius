package com.appunite.loudius.fakes

import com.appunite.loudius.domain.store.UserLocalDataSource
import com.appunite.loudius.network.model.AccessToken

class FakeUserLocalDataSource : UserLocalDataSource {
    private var token = ""
    override fun saveAccessToken(accessToken: AccessToken) {
        token = accessToken
    }

    override fun getAccessToken(): AccessToken = token
}
