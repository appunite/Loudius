package com.appunite.loudius.util

import com.appunite.loudius.network.model.PullRequest
import java.time.LocalDateTime

object Defaults {
    fun pullRequest(id: Int = 1) = PullRequest(
        id = id,
        draft = false,
        number = id,
        repositoryUrl = "https://api.github.com/repos/exampleOwner/exampleRepo",
        title = "example title",
        LocalDateTime.parse("2023-03-07T08:21:45").plusHours(id.toLong()),
    )
}
