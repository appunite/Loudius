# Reviewers Screen

<img src="../analytics_imgs/reviewers/reviewers_screen.png" width=20% height=20%>

## Documentation

|                           When reviewers screen opened.                           | Specification                                                                                 |
|:---------------------------------------------------------------------------------:|-----------------------------------------------------------------------------------------------|
| <img src="../analytics_imgs/reviewers/reviewers_screen.png" width=50% height=50%> | <pre>{<br />   "name": "screen_opened"<br />   "screen_name": "reviewers_screen"<br />}<pre/> |

|                         When user clicks notify.                         | Specification                                                                                                              |
|:------------------------------------------------------------------------:|----------------------------------------------------------------------------------------------------------------------------|
| ![Click notify](../analytics_imgs/reviewers/when_user_clicks_notify.png) | <pre>{<br />   "name": "button_click"<br />   "item_name": "notify"<br />   "screen_name": "reviewers_screen"<br />}<pre/> |

|                      Once the user notification is started.                      | Specification                                                                                                              |
|:--------------------------------------------------------------------------------:|----------------------------------------------------------------------------------------------------------------------------|
| ![Success after notify](../analytics_imgs/reviewers/when_user_clicks_notify.png) | <pre>{<br />   "name": "action_start"<br />   "item_name": "notify"<br />   "screen_name": "reviewers_screen"<br />}<pre/> |

|            After user clicks notify, action finished with success.            | Specification                                                                                                                                         |
|:-----------------------------------------------------------------------------:|-------------------------------------------------------------------------------------------------------------------------------------------------------|
| ![Success after notify](../analytics_imgs/reviewers/after_notify_success.png) | <pre>{<br />   "name": "action_finished"<br />   "item_name": "notify"<br />   "success": true<br />   "screen_name": "reviewers_screen"<br />}<pre/> |

|           After user clicks notify, action finished with error.           | Specification                                                                                                                                                                                         |
|:-------------------------------------------------------------------------:|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ![Error after notify](../analytics_imgs/reviewers/after_notify_error.png) | <pre>{<br />   "name": "action_finished"<br />   "item_name": "notify"<br />   "success": false<br />   "screen_name": "reviewers_screen"<br />   "error_message": "Some error message."<br />}<pre/> |

|                           When user pulls to refresh.                            | Specification                                                                                                                              |
|:--------------------------------------------------------------------------------:|--------------------------------------------------------------------------------------------------------------------------------------------|
| <img src="../analytics_imgs/reviewers/pull_to_refresh.png" width=50% height=50%> | <pre>{<br />   "name": "action_start"<br />   "item_name": "refresh_reviewers_data"<br />   "screen_name": "reviewers_screen"<br />}<pre/> |

|                       When refresh data finished with success.                        | Specification                                                                                                                                                         |
|:-------------------------------------------------------------------------------------:|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| <img src="../analytics_imgs/reviewers/refresh_with_success.png" width=50% height=50%> | <pre>{<br />   "name": "action_finished"<br />   "item_name": "refresh_reviewers_data"<br />   "success": true<br />   "screen_name": "reviewers_screen"<br />}<pre/> |

|                       When refresh data finished with error.                        | Specification                                                                                                                                                                                                         |
|:-----------------------------------------------------------------------------------:|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| <img src="../analytics_imgs/reviewers/refresh_with_error.png" width=50% height=50%> | <pre>{<br />   "name": "action_finished"<br />   "item_name": "refresh_reviewers_data"<br />   "success": false<br />   "screen_name": "reviewers_screen"<br />   "error_message": "Some error message."<br />}<pre/> |

|                          When fetching data is started.                           | Specification                                                                                                                            |
|:---------------------------------------------------------------------------------:|------------------------------------------------------------------------------------------------------------------------------------------|
| <img src="../analytics_imgs/reviewers/reviewers_screen.png" width=50% height=50%> | <pre>{<br />   "name": "action_start"<br />   "item_name": "fetch_reviewers_data"<br />   "screen_name": "reviewers_screen"<br />}<pre/> |

|                       When fetch data finished with success.                        | Specification                                                                                                                                                       |
|:-----------------------------------------------------------------------------------:|---------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| <img src="../analytics_imgs/reviewers/fetch_with_success.png" width=50% height=50%> | <pre>{<br />   "name": "action_finished"<br />   "item_name": "fetch_reviewers_data"<br />   "success": true<br />   "screen_name": "reviewers_screen"<br />}<pre/> |

|                       When fetch data finished with error.                        | Specification                                                                                                                                                                                                       |
|:---------------------------------------------------------------------------------:|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| <img src="../analytics_imgs/reviewers/fetch_with_error.png" width=50% height=50%> | <pre>{<br />   "name": "action_finished"<br />   "item_name": "fetch_reviewers_data"<br />   "success": false<br />   "screen_name": "reviewers_screen"<br />   "error_message": "Some error message."<br />}<pre/> |
