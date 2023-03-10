@file:OptIn(ExperimentalCoroutinesApi::class)

package com.appunite.loudius.domain

import com.appunite.loudius.fakes.FakePullRequestDataSource
import com.appunite.loudius.network.datasource.UserDataSource
import com.appunite.loudius.network.model.PullRequest
import com.appunite.loudius.network.model.PullRequestsResponse
import com.appunite.loudius.network.model.User
import com.appunite.loudius.network.utils.WebException
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


class PullRequestsRepositoryTest {

    private val pullRequestsNetworkDataSource = FakePullRequestDataSource()
    private val userDataSource: UserDataSource = mockk {
        coEvery { getUser() } returns Result.success(User(1, "exampleUser"))
    }
    private val repository = PullRequestsRepository(
        pullRequestsNetworkDataSource = pullRequestsNetworkDataSource,
        userDataSource = userDataSource
    )

    @Test
    fun `GIVEN logged in user WHEN getting pull requests THEN return list of pull requests`() =
        runTest {
            val actualResponse = repository.getPullRequests()

            val expectResponse = Result.success(
                PullRequestsResponse(
                    incompleteResults = false,
                    totalCount = 3,
                    items = (1..3).map { id ->
                        PullRequest(
                            id = id,
                            draft = false,
                            number = id,
                            repositoryUrl = "https://api.github.com/repos/exampleOwner/exampleRepo${id}",
                            title = "example title $id",
                            LocalDateTime.parse("2023-03-07T09:24:24"),
                        )
                    }
                ))
            Assertions.assertEquals(expectResponse, actualResponse)
        }

    @Test
    fun `GIVEN error during fetching user WHEN getting pull requests THEN return error`() =
        runTest {
            coEvery { userDataSource.getUser() } returns
                    Result.failure(WebException.NetworkError())

            val response = repository.getPullRequests()

            Assertions.assertEquals(
                Result.failure<PullRequestsResponse>(WebException.NetworkError()),
                response
            )
        }

    @Test
    fun `GIVEN error during fetching pull requests WHEN getting pull requests THEN return list of pull requests`() =
        runTest {
            coEvery { userDataSource.getUser() } returns Result.success(User(1, "wrongUser"))

            val response = repository.getPullRequests()

            Assertions.assertEquals(
                Result.failure<PullRequestsResponse>(WebException.NetworkError()),
                response
            )
        }
}
