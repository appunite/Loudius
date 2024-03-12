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

import com.appunite.mockwebserverrule.MockWebServerRule
import com.appunite.mockwebserverrule.jsonResponse
import com.appunite.mockwebserverrule.path
import com.appunite.mockwebserverrule.queryParameter
import com.appunite.mockwebserverrule.url
import strikt.api.expectThat
import strikt.assertions.isEqualTo

object Register {

    fun user(mockWebServer: MockWebServerRule) {
        mockWebServer.register {
            expectThat(it).url.path.isEqualTo("/user")
            jsonResponse("""{"id": 1, "login": "user"}""")
        }
    }

    fun accessToken(mockWebServer: MockWebServerRule) {
        mockWebServer.register {
            expectThat(it).url.path.isEqualTo("/login/oauth/access_token")
            jsonResponse(
                """
                {
                  "access_token": "example_access_token",
                  "token_type": "bearer",
                  "scope": "repo"
                }
                """.trimIndent()
            )
        }
    }

    fun reviews(mockWebServer: MockWebServerRule) {
        mockWebServer.register {
            expectThat(it).url.and {
                get("host") { host }.isEqualTo("api.github.com")
                path.isEqualTo("/repos/exampleOwner/exampleRepo/pulls/1/reviews")
            }
            jsonResponse("[]")
        }
    }

    fun comment(mockWebServer: MockWebServerRule) {
        mockWebServer.register {
            expectThat(it).url.and {
                get("host") { host }.isEqualTo("api.github.com")
                path.isEqualTo("/repos/exampleOwner/exampleRepo/issues/1/comments")
            }
            jsonResponse(
                """
                    {
                      "id": 1,
                      "node_id": "1",
                      "url": "https://api.github.com/repos/owner/repo/issues/comments/1",
                      "html_url": "https://github.com/owner/repo/issues/1347#issuecomment-1",
                      "body": "example body",
                      "user": {
                        "login": "userLogin",
                        "id": 1,
                        "node_id": "1",
                        "avatar_url": "https://github.com/images/error/octocat_happy.gif",
                        "gravatar_id": "",
                        "url": "https://api.github.com/users/owner",
                        "html_url": "https://github.com/owner",
                        "followers_url": "https://api.github.com/users/owner/followers",
                        "following_url": "https://api.github.com/users/owner/following{/other_user}",
                        "gists_url": "https://api.github.com/users/owner/gists{/gist_id}",
                        "starred_url": "https://api.github.com/users/owner/starred{/owner}{/repo}",
                        "subscriptions_url": "https://api.github.com/users/owner/subscriptions",
                        "organizations_url": "https://api.github.com/users/owner/orgs",
                        "repos_url": "https://api.github.com/users/owner/repos",
                        "events_url": "https://api.github.com/users/owner/events{/privacy}",
                        "received_events_url": "https://api.github.com/users/owner/received_events",
                        "type": "User",
                        "site_admin": false
                      },
                      "created_at": "2011-04-14T16:00:49Z",
                      "updated_at": "2011-04-14T16:00:49Z",
                      "issue_url": "https://api.github.com/repos/owner/repo/issues/1",
                      "author_association": "COLLABORATOR"
                    }
                """.trimIndent()
            )
        }
    }

    fun requestedReviewers(mockWebServer: MockWebServerRule) {
        mockWebServer.register {
            expectThat(it).url.and {
                get("host") { host }.isEqualTo("api.github.com")
                path.isEqualTo("/repos/exampleOwner/exampleRepo/pulls/1/requested_reviewers")
            }
            jsonResponse(
                """
                        {
                            "users": [
                                {
                                    "login": "userLogin",
                                    "id": 1,
                                    "node_id": "1",
                                    "avatar_url": "https://avatars.githubusercontent.com/u/18102775?v=4",
                                    "gravatar_id": "",
                                    "url": "https://api.github.com/users/user",
                                    "html_url": "https://github.com/user",
                                    "followers_url": "https://api.github.com/users/user/followers",
                                    "following_url": "https://api.github.com/users/user/following{/other_user}",
                                    "gists_url": "https://api.github.com/users/user/gists{/gist_id}",
                                    "starred_url": "https://api.github.com/users/user/starred{/owner}{/repo}",
                                    "subscriptions_url": "https://api.github.com/users/user/subscriptions",
                                    "organizations_url": "https://api.github.com/users/user/orgs",
                                    "repos_url": "https://api.github.com/users/user/repos",
                                    "events_url": "https://api.github.com/users/user/events{/privacy}",
                                    "received_events_url": "https://api.github.com/users/user/received_events",
                                    "type": "User",
                                    "site_admin": false
                                }
                            ],
                            "teams": []
                        }
                  """
            )
        }
    }

    fun issues(mockWebServer: MockWebServerRule) {
        mockWebServer.register {
            expectThat(it).url.and {
                get("host") { host }.isEqualTo("api.github.com")
                path.isEqualTo("/search/issues")
                queryParameter("q").isEqualTo("author:user type:pr state:open")
                queryParameter("page").isEqualTo("0")
                queryParameter("per_page").isEqualTo("100")
            }

            jsonResponse(
                """
                        {
                            "total_count":1,
                            "incomplete_results":false,
                            "items":[
                                {
                                    "url":"https://api.github.com/repos/exampleOwner/exampleRepo/issues/1",
                                    "repository_url":"https://api.github.com/repos/exampleOwner/exampleRepo",
                                    "labels_url":"https://api.github.com/repos/exampleOwner/exampleRepo/issues/1/labels{/name}",
                                    "comments_url":"https://api.github.com/repos/exampleOwner/exampleRepo/issues/1/comments",
                                    "events_url":"https://api.github.com/repos/exampleOwner/exampleRepo/issues/1/events",
                                    "html_url":"https://github.com/exampleOwner/exampleRepo/pull/1",
                                    "id":1,
                                    "node_id":"example_node_id",
                                    "number":1,
                                    "title":"First Pull-Request title",
                                    "user":{
                                        "login":"exampleUser",
                                        "id":1,
                                        "node_id":"example_user_node_id",
                                        "avatar_url":"https://avatars.githubusercontent.com/u/1",
                                        "gravatar_id":"",
                                        "url":"https://api.github.com/users/exampleUser",
                                        "html_url":"https://github.com/exampleUser",
                                        "followers_url":"https://api.github.com/users/exampleUser/followers",
                                        "following_url":"https://api.github.com/users/exampleUser/following{/other_user}",
                                        "gists_url":"https://api.github.com/users/exampleUser/gists{/gist_id}",
                                        "starred_url":"https://api.github.com/users/exampleUser/starred{/owner}{/repo}",
                                        "subscriptions_url":"https://api.github.com/users/exampleUser/subscriptions",
                                        "organizations_url":"https://api.github.com/users/exampleUser/orgs",
                                        "repos_url":"https://api.github.com/users/exampleUser/repos",
                                        "events_url":"https://api.github.com/users/exampleUser/events{/privacy}",
                                        "received_events_url":"https://api.github.com/users/exampleUser/received_events",
                                        "type":"User",
                                        "site_admin":false
                                    },
                                    "labels":[
                                        
                                    ],
                                    "state":"open",
                                    "locked":false,
                                    "assignee":null,
                                    "assignees":[
                                        
                                    ],
                                    "milestone":null,
                                    "comments":1,
                                    "created_at":"2023-03-07T09:21:45Z",
                                    "updated_at":"2023-03-07T09:24:24Z",
                                    "closed_at":null,
                                    "author_association":"COLLABORATOR",
                                    "active_lock_reason":null,
                                    "draft":false,
                                    "pull_request":{
                                        "url":"https://api.github.com/repos/exampleOwner/exampleRepo/pulls/1",
                                        "html_url":"https://github.com/exampleOwner/exampleRepo/pull/1",
                                        "diff_url":"https://github.com/exampleOwner/exampleRepo/pull/1.diff",
                                        "patch_url":"https://github.com/exampleOwner/exampleRepo/pull/1.patch",
                                        "merged_at":null
                                    },
                                    "body":"pr only for demonstration purposes  . . . .",
                                    "reactions":{
                                        "url":"https://api.github.com/repos/exampleOwner/exampleRepo/issues/1/reactions",
                                        "total_count":0,
                                        "+1":0,
                                        "-1":0,
                                        "laugh":0,
                                        "hooray":0,
                                        "confused":0,
                                        "heart":0,
                                        "rocket":0,
                                        "eyes":0
                                    },
                                    "timeline_url":"https://api.github.com/repos/exampleOwner/exampleRepo/issues/1/timeline",
                                    "performed_via_github_app":null,
                                    "state_reason":null,
                                    "score":1.0
                                }
                            ]
                        }
                    """
            )
        }
    }
}
