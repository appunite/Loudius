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

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(val route: String) {
    open val arguments: List<NamedNavArgument> = emptyList()

    object Login : Screen("login_screen")

    object Repos : Screen("repos_screen")

    object PullRequests : Screen("pull_requests_screen")

    object Reviewers :
        Screen("reviewers_screen/{pull_request_number}/{owner}/{repo}/{submission_date}") {
        const val pullRequestNumberArg = "pull_request_number"
        const val ownerArg = "owner"
        const val repoArg = "repo"
        const val submissionDateArg = "submission_date"

        override val arguments: List<NamedNavArgument>
            get() {
                return listOf(
                    navArgument(pullRequestNumberArg) { type = NavType.StringType },
                    navArgument(ownerArg) { type = NavType.StringType },
                    navArgument(repoArg) { type = NavType.StringType },
                    navArgument(submissionDateArg) { type = NavType.StringType },
                )
            }

        fun constructRoute(
            owner: String,
            repo: String,
            pullRequestNumber: String,
            submissionDate: String,
        ): String = "reviewers_screen/$pullRequestNumber/$owner/$repo/$submissionDate"
    }
}
