package com.appunite.loudius.fakes

import com.appunite.loudius.network.datasource.PullRequestDataSource
import com.appunite.loudius.network.model.PullRequestsResponse
import com.appunite.loudius.network.model.RequestedReviewer
import com.appunite.loudius.network.model.RequestedReviewersResponse
import com.appunite.loudius.network.model.Review
import com.appunite.loudius.network.model.ReviewState
import com.appunite.loudius.network.model.User
import com.appunite.loudius.network.utils.WebException
import java.time.LocalDateTime

class FakePullRequestDataSource : PullRequestDataSource {

    private val date1 = LocalDateTime.parse("2022-01-29T10:00:00")

    override suspend fun getReviews(
        owner: String,
        repository: String,
        pullRequestNumber: String
    ): Result<List<Review>> = when (pullRequestNumber) {
        "correctPullRequestNumber", "onlyReviewsPullNumber" -> Result.success(
            listOf(
                Review("1", User(1, "user1"), ReviewState.CHANGES_REQUESTED, date1),
                Review("2", User(1, "user1"), ReviewState.COMMENTED, date1),
                Review("3", User(1, "user1"), ReviewState.APPROVED, date1),
                Review("4", User(2, "user2"), ReviewState.COMMENTED, date1),
                Review("5", User(2, "user2"), ReviewState.COMMENTED, date1),
                Review("6", User(2, "user2"), ReviewState.APPROVED, date1),
            ),
        )
        "notExistingPullRequestNumber" -> Result.failure(WebException.UnknownError(404, null))
        else -> Result.success(emptyList())
    }

    override suspend fun getReviewers(
        owner: String,
        repository: String,
        pullRequestNumber: String
    ): Result<RequestedReviewersResponse> = when (pullRequestNumber) {
        "correctPullRequestNumber", "onlyRequestedReviewersPullNumber" -> Result.success(
            RequestedReviewersResponse(
                listOf(
                    RequestedReviewer(3, "user3"),
                    RequestedReviewer(4, "user4"),
                ),
            ),
        )
        "notExistingPullRequestNumber" -> Result.failure(WebException.UnknownError(404, null))
        else -> Result.success(RequestedReviewersResponse(emptyList()))
    }

    override suspend fun getPullRequestsForUser(author: String): Result<PullRequestsResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun notify(
        owner: String,
        repository: String,
        pullRequestNumber: String,
        message: String
    ): Result<Unit> {
        TODO("Not yet implemented")
    }
}
