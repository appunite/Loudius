# Reviewers Screen

![Reviewers screen](analytics_imgs/reviewers_screen.png)

## Documentation

|                  When user clicks notify.                   | Specification                                                                    |
|:-----------------------------------------------------------:|----------------------------------------------------------------------------------|
| ![Click notify](analytics_imgs/when_user_clicks_notify.png) | <pre>{<br />   "name": "button_click"<br />   "item_name": "notify"<br />}<pre/> |

|     After user clicks notify, action finished with success.      | Specification                                                                                               |
|:----------------------------------------------------------------:|-------------------------------------------------------------------------------------------------------------|
| ![Success after notify](analytics_imgs/after_notify_success.png) | <pre>{<br />   "name": "action_finished"<br />   "item_name": "notify"<br />   "success": true<br />}<pre/> |

|    After user clicks notify, action finished with error.     | Specification                                                                                                |
|:------------------------------------------------------------:|--------------------------------------------------------------------------------------------------------------|
| ![Error after notify](analytics_imgs/after_notify_error.png) | <pre>{<br />   "name": "action_finished"<br />   "item_name": "notify"<br />   "success": false<br />}<pre/> |

|              When user pulls to refresh.               | Specification                                                                                    |
|:------------------------------------------------------:|--------------------------------------------------------------------------------------------------|
| ![Pull to refresh](analytics_imgs/pull_to_refresh.png) | <pre>{<br />   "name": "action_start"<br />   "item_name": "refresh_reviewers_data"<br />}<pre/> |

|             When refresh data finished with success.              | Specification                                                                                                               |
|:-----------------------------------------------------------------:|-----------------------------------------------------------------------------------------------------------------------------|
| ![Success after refresh](analytics_imgs/refresh_with_success.png) | <pre>{<br />   "name": "action_finished"<br />   "item_name": "refresh_reviewers_data"<br />   "success": true<br />}<pre/> |

|            When refresh data finished with error.             | Specification                                                                                                                |
|:-------------------------------------------------------------:|------------------------------------------------------------------------------------------------------------------------------|
| ![Error after refresh](analytics_imgs/refresh_with_error.png) | <pre>{<br />   "name": "action_finished"<br />   "item_name": "refresh_reviewers_data"<br />   "success": false<br />}<pre/> |

|            When user opens reviewers screen.             | Specification                                                                                  |
|:--------------------------------------------------------:|------------------------------------------------------------------------------------------------|
| ![Reviewers screen](analytics_imgs/reviewers_screen.png) | <pre>{<br />   "name": "action_start"<br />   "item_name": "fetch_reviewers_data"<br />}<pre/> |

|            When fetch data finished with success.             | Specification                                                                                                             |
|:-------------------------------------------------------------:|---------------------------------------------------------------------------------------------------------------------------|
| ![Success after fetch](analytics_imgs/fetch_with_success.png) | <pre>{<br />   "name": "action_finished"<br />   "item_name": "fetch_reviewers_data"<br />   "success": true<br />}<pre/> |

|           When fetch data finished with error.            | Specification                                                                                                              |
|:---------------------------------------------------------:|----------------------------------------------------------------------------------------------------------------------------|
| ![Error after fetch](analytics_imgs/fetch_with_error.png) | <pre>{<br />   "name": "action_finished"<br />   "item_name": "fetch_reviewers_data"<br />   "success": false<br />}<pre/> |
