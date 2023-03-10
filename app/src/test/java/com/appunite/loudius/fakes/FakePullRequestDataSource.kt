package com.appunite.loudius.fakes

import com.appunite.loudius.network.datasource.PullRequestDataSource
import com.appunite.loudius.network.model.PullRequest
import com.appunite.loudius.network.model.PullRequestsResponse
import com.appunite.loudius.network.model.RequestedReviewersResponse
import com.appunite.loudius.network.model.Review
import com.appunite.loudius.network.utils.WebException
import java.time.ZonedDateTime

class FakePullRequestDataSource : PullRequestDataSource {
    override suspend fun getReviewers(
        owner: String,
        repository: String,
        pullRequestNumber: String
    ): Result<RequestedReviewersResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getReviews(
        owner: String,
        repository: String,
        pullRequestNumber: String
    ): Result<List<Review>> {
        TODO("Not yet implemented")
    }

    override suspend fun getPullRequestsForUser(author: String): Result<PullRequestsResponse> =
        when (author) {
            "exampleUser" -> getPullRequestResponse()
            else -> Result.failure(WebException.NetworkError())
        }

    private fun getPullRequestResponse(): Result<PullRequestsResponse> {
        val itemsAmount = 3
        return Result.success(
            PullRequestsResponse(
                incompleteResults = false,
                totalCount = itemsAmount,
                items = (1..itemsAmount).map { getPullRequestForId(it) }
            )
        )
    }

    private fun getPullRequestForId(id: Int) = PullRequest(
        id = id,
        draft = false,
        number = id,
        repositoryUrl = "https://api.github.com/repos/exampleOwner/exampleRepo${id}",
        title = "example title $id",
        ZonedDateTime.parse("2023-03-07T09:24:24Z").toLocalDateTime().plusHours(id.toLong()),
    )
}
