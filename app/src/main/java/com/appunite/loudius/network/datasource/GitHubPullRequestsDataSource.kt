package com.appunite.loudius.network.datasource

import com.appunite.loudius.domain.UserRepository
import com.appunite.loudius.network.model.PullRequestsResponse
import com.appunite.loudius.network.services.GithubPullRequestsService
import com.appunite.loudius.network.utils.safeApiCall
import javax.inject.Inject

class GitHubPullRequestsDataSource @Inject constructor(private val service: GithubPullRequestsService, private val userRepository: UserRepository) {
    suspend fun getPullRequestsForUser(author: String): Result<PullRequestsResponse> = safeApiCall {
        service.getPullRequestsForUser(
            userRepository.getAccessToken(),
            "author:$author type:pr state:open",
        )
    }
}
