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
import com.appunite.loudius.network.model.PullRequestsResponse
import com.appunite.loudius.network.model.RequestedReviewer
import com.appunite.loudius.network.model.Review
import com.appunite.loudius.network.model.ReviewState
import com.appunite.loudius.network.model.User
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.hours

object Defaults {
    val date1: LocalDateTime = LocalDateTime.parse("2022-01-29T10:00:00")
    val date2: LocalDateTime = LocalDateTime.parse("2022-01-29T11:00:00")
    val date3: LocalDateTime = LocalDateTime.parse("2022-01-29T12:00:00")

    fun pullRequest(id: Int = 1) = PullRequest(
        id = id,
        draft = false,
        number = id,
        repositoryUrl = "https://api.github.com/repos/exampleOwner/exampleRepo",
        title = "example title",
        LocalDateTime.parse("2023-03-07T08:21:45").toInstant(TimeZone.UTC).plus(id.toLong().hours).toLocalDateTime(TimeZone.UTC)
    )

    fun reviews() = listOf(
        Review("1", currentUser(), ReviewState.CHANGES_REQUESTED, date1),
        Review("2", currentUser(), ReviewState.COMMENTED, date2),
        Review("3", currentUser(), ReviewState.APPROVED, date3),
        Review("4", User(1, "user1"), ReviewState.COMMENTED, date1),
        Review("5", User(1, "user1"), ReviewState.COMMENTED, date2),
        Review("6", User(1, "user1"), ReviewState.APPROVED, date3),
    )

    fun requestedReviewers() = listOf(
        RequestedReviewer(2, "user2"),
        RequestedReviewer(3, "user3"),
    )

    fun pullRequestsResponse() = PullRequestsResponse(
        incompleteResults = false,
        totalCount = 1,
        items = listOf(pullRequest()),
    )

    fun currentUser() = User(0, "currentUser")
}
