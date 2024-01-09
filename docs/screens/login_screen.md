# Login Screen

<img src="../analytics_imgs/login/login_screen.png" width=20% height=20%>

## Documentation

|                         When login screen opened.                         | Specification                                                                              |
|:-------------------------------------------------------------------------:|--------------------------------------------------------------------------------------------|
| <img src="../analytics_imgs/login/login_screen.png" width=50% height=50%> | <pre>{<br />   "name": "screen_opened"<br />   "screen_name": "log_in_screen"<br />}<pre/> |

|                         When user clicks log in.                          | Specification                                                                                                           |
|:-------------------------------------------------------------------------:|-------------------------------------------------------------------------------------------------------------------------|
| <img src="../analytics_imgs/login/login_screen.png" width=50% height=50%> | <pre>{<br />   "name": "button_click"<br />   "item_name": "log_in"<br />   "screen_name": "log_in_screen"<br />}<pre/> |

|                         When github auth opens.                          | Specification                                                                                                                      |
|:------------------------------------------------------------------------:|------------------------------------------------------------------------------------------------------------------------------------|
| <img src="../analytics_imgs/login/github_auth.png" width=50% height=50%> | <pre>{<br />   "name": "simple_action"<br />   "item_name": "open_github_auth"<br />   "screen_name": "log_in_screen"<br />}<pre/> |

|                         When user dismiss permission dialog.                         | Specification                                                                                                                                        |
|:------------------------------------------------------------------------------------:|------------------------------------------------------------------------------------------------------------------------------------------------------|
| <img src="../analytics_imgs/login/xiaomi_dialog_dismissed.png" width=50% height=50%> | <pre>{<br />   "name": "simple_action"<br />   "item_name": "xiaomi_permission_dialog_dismissed"<br />   "screen_name": "log_in_screen"<br />}<pre/> |

|                      When user clicks grant on permission dialog.                      | Specification                                                                                                                                                 |
|:--------------------------------------------------------------------------------------:|---------------------------------------------------------------------------------------------------------------------------------------------------------------|
| <img src="../analytics_imgs/login/xiaomi_permission_granted.png" width=50% height=50%> | <pre>{<br />   "name": "simple_action"<br />   "item_name": "xiaomi_permission_dialog_permission_granted"<br />   "screen_name": "log_in_screen"<br />}<pre/> |

|                     When user clicks already granted on permission dialog.                     | Specification                                                                                                                                                         |
|:----------------------------------------------------------------------------------------------:|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| <img src="../analytics_imgs/login/xiaomi_permission_already_granted.png" width=50% height=50%> | <pre>{<br />   "name": "simple_action"<br />   "item_name": "xiaomi_permission_dialog_permission_already_granted"<br />   "screen_name": "log_in_screen"<br />}<pre/> |
