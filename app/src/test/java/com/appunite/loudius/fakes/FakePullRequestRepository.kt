package com.appunite.loudius.fakes

import com.appunite.loudius.domain.PullRequestRepository
import com.appunite.loudius.network.model.PullRequestsResponse
import com.appunite.loudius.network.model.RequestedReviewer
import com.appunite.loudius.network.model.RequestedReviewersResponse
import com.appunite.loudius.network.model.Review
import com.appunite.loudius.network.model.ReviewState.APPROVED
import com.appunite.loudius.network.model.ReviewState.CHANGES_REQUESTED
import com.appunite.loudius.network.model.ReviewState.COMMENTED
import com.appunite.loudius.network.model.User
import com.appunite.loudius.network.utils.WebException
import java.time.LocalDateTime

class FakePullRequestRepository : PullRequestRepository {
    private val date1 = LocalDateTime.parse("2022-01-29T10:00:00")
    private val date2 = LocalDateTime.parse("2022-01-29T11:00:00")
    private val date3 = LocalDateTime.parse("2022-01-29T12:00:00")

    override suspend fun getReviews(
        owner: String,
        repo: String,
        pullRequestNumber: String,
    ): Result<List<Review>> = when (pullRequestNumber) {
        "correctPullRequestNumber", "onlyReviewsPullNumber" -> Result.success(
            listOf(
                Review("1", User(1, "user1"), CHANGES_REQUESTED, date1),
                Review("2", User(1, "user1"), COMMENTED, date2),
                Review("3", User(1, "user1"), APPROVED, date3),
                Review("4", User(2, "user2"), COMMENTED, date1),
                Review("5", User(2, "user2"), COMMENTED, date2),
                Review("6", User(2, "user2"), APPROVED, date3),
            ),
        )
        "notExistingPullRequestNumber" -> Result.failure(WebException.UnknownError(404, null))
        else -> Result.success(emptyList())
    }

    override suspend fun getRequestedReviewers(
        owner: String,
        repo: String,
        pullRequestNumber: String,
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

    override suspend fun getCurrentUserPullRequests(): Result<PullRequestsResponse> {
        TODO("Not yet implemented")
    }
}
