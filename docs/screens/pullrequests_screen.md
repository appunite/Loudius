# Pull Requests Screen

<img src="../analytics_imgs/pull_requests/prs_screen_opened.png" width=20% height=20%>

## Documentation

|                           When pull requests screen opened.                            | Specification                                                                                   |
|:--------------------------------------------------------------------------------------:|-------------------------------------------------------------------------------------------------|
| <img src="../analytics_imgs/pull_requests/prs_screen_opened.png" width=50% height=50%> | <pre>{<br />   "name": "screen_opened"<br />   "item_name": "pull_requests_screen"<br />}<pre/> |

|                           When user pulls to refresh.                            | Specification                                                                                        |
|:--------------------------------------------------------------------------------:|------------------------------------------------------------------------------------------------------|
| <img src="../analytics_imgs/pull_requests/refresh_prs.png" width=50% height=50%> | <pre>{<br />   "name": "action_start"<br />   "item_name": "refresh_pull_requests_data"<br />}<pre/> |

|                         When refresh data finished with success.                         | Specification                                                                                                                   |
|:----------------------------------------------------------------------------------------:|---------------------------------------------------------------------------------------------------------------------------------|
| <img src="../analytics_imgs/pull_requests/refresh_prs_success.png" width=50% height=50%> | <pre>{<br />   "name": "action_finished"<br />   "item_name": "refresh_pull_requests_data"<br />   "success": true<br />}<pre/> |

|                         When refresh data finished with failure.                         | Specification                                                                                                                    |
|:----------------------------------------------------------------------------------------:|----------------------------------------------------------------------------------------------------------------------------------|
| <img src="../analytics_imgs/pull_requests/refresh_prs_failure.png" width=50% height=50%> | <pre>{<br />   "name": "action_finished"<br />   "item_name": "refresh_pull_requests_data"<br />   "success": false<br />}<pre/> |

|                         When fetching data is started.                         | Specification                                                                                      |
|:------------------------------------------------------------------------------:|----------------------------------------------------------------------------------------------------|
| <img src="../analytics_imgs/pull_requests/fetch_prs.png" width=50% height=50%> | <pre>{<br />   "name": "action_start"<br />   "item_name": "fetch_pull_requests_data"<br />}<pre/> |

|                         When fetch data finished with success.                         | Specification                                                                                                                 |
|:--------------------------------------------------------------------------------------:|-------------------------------------------------------------------------------------------------------------------------------|
| <img src="../analytics_imgs/pull_requests/fetch_prs_success.png" width=50% height=50%> | <pre>{<br />   "name": "action_finished"<br />   "item_name": "fetch_pull_requests_data"<br />   "success": true<br />}<pre/> |

|                         When fetch data finished with failure.                         | Specification                                                                                                                  |
|:--------------------------------------------------------------------------------------:|--------------------------------------------------------------------------------------------------------------------------------|
| <img src="../analytics_imgs/pull_requests/fetch_prs_failure.png" width=50% height=50%> | <pre>{<br />   "name": "action_finished"<br />   "item_name": "fetch_pull_requests_data"<br />   "success": false<br />}<pre/> |

|                           When user clicks on pull request item.                           | Specification                                                                        |
|:------------------------------------------------------------------------------------------:|--------------------------------------------------------------------------------------|
| <img src="../analytics_imgs/pull_requests/navigate_to_reviewers.png" width=50% height=50%> | <pre>{<br />   "name": "item_click"<br />   "item_name": "pull_request"<br />}<pre/> |
