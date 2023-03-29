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

package com.appunite.loudius.util

import com.appunite.loudius.network.model.PullRequest
import com.appunite.loudius.network.model.RequestedReviewer
import com.appunite.loudius.network.model.Review
import com.appunite.loudius.network.model.ReviewState
import com.appunite.loudius.network.model.User
import java.time.LocalDateTime

object Defaults {
    private val date1 = LocalDateTime.parse("2022-01-29T10:00:00")
    private val date2 = LocalDateTime.parse("2022-01-29T11:00:00")
    private val date3 = LocalDateTime.parse("2022-01-29T12:00:00")

    fun pullRequest(id: Int = 1) = PullRequest(
        id = id,
        draft = false,
        number = id,
        repositoryUrl = "https://api.github.com/repos/exampleOwner/exampleRepo",
        title = "example title",
        LocalDateTime.parse("2023-03-07T08:21:45").plusHours(id.toLong()),
    )

    fun reviews() = listOf(
        Review("1", User(1, "user1"), ReviewState.CHANGES_REQUESTED, date1),
        Review("2", User(1, "user1"), ReviewState.COMMENTED, date2),
        Review("3", User(1, "user1"), ReviewState.APPROVED, date3),
        Review("4", User(2, "user2"), ReviewState.COMMENTED, date1),
        Review("5", User(2, "user2"), ReviewState.COMMENTED, date2),
        Review("6", User(2, "user2"), ReviewState.APPROVED, date3),
    )

    fun requestedReviewers() = listOf(
        RequestedReviewer(3, "user3"),
        RequestedReviewer(4, "user4"),
    )
}
