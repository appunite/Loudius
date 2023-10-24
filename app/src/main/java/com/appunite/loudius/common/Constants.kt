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

package com.appunite.loudius.common

import com.appunite.loudius.BuildConfig

object Constants {

    const val AUTH_API_URL = "https://github.com"
    const val BASE_API_URL = "https://api.github.com"
    const val AUTH_PATH = "/login/oauth/authorize"
    const val NAME_PARAM_CLIENT_ID = "?client_id="
    const val SCOPE_PARAM = "&scope=repo"
    const val CLIENT_ID = BuildConfig.LOUDIUS_CLIENT_ID
    const val REDIRECT_URL = "loudius://callback"
    const val AUTHORIZATION_URL =
        AUTH_API_URL + AUTH_PATH + NAME_PARAM_CLIENT_ID + CLIENT_ID + SCOPE_PARAM
}
