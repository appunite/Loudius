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

package com.appunite.loudius.network.model

import com.appunite.loudius.common.Constants
import com.appunite.loudius.network.utils.InstantSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PullRequest(
    val id: Int,
    val draft: Boolean,
    val number: Int,
    @SerialName("repository_url")
    val repositoryUrl: String,
    val title: String,
    @SerialName("created_at")
    @Serializable(with = InstantSerializer::class)
    val createdAt: Instant
) {
    val fullRepositoryName: String
        get() = repositoryUrl.removePrefix(REPOSITORY_PATH)

    val shortRepositoryName: String
        get() = fullRepositoryName.substringAfter('/')

    val owner: String
        get() = fullRepositoryName.substringBefore('/')

    companion object {
        private const val REPOSITORY_PATH = Constants.BASE_API_URL + "/repos/"
    }
}
