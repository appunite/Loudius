# Authenticating

<img src="../analytics_imgs/authenticating/authenticating_screen.png" width=20% height=20%>

## Documentation

|                             When authenticating screen opened.                              | Specification                                                                                      |
|:-------------------------------------------------------------------------------------------:|----------------------------------------------------------------------------------------------------|
| <img src="../analytics_imgs/authenticating/authenticating_screen.png" width=50% height=50%> | <pre>{<br />   "name": "screen_opened"<br />   "screen_name": "authenticating_screen"<br />}<pre/> |

| After user clicks log in, authentication started. | <pre>{<br />   "name": "action_start"<br />   "item_name": "authentication"<br />   "screen_name": "authenticating_screen"<br />}<pre/> |
|:-------------------------------------------------:|-----------------------------------------------------------------------------------------------------------------------------------------|

| When application gets access token and authentication finished with success. | <pre>{<br />   "name": "action_finished"<br />   "item_name": "authentication"<br />   "success": true<br />   "screen_name": "authenticating_screen"<br />}<pre/> |
|:----------------------------------------------------------------------------:|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|

| When application doesn't get access token and  When authentication finished with failure. | <pre>{<br />   "name": "action_finished"<br />   "item_name": "authentication"<br />   "success": false<br />   "screen_name": "authenticating_screen"<br />}<pre/> |
|:-----------------------------------------------------------------------------------------:|---------------------------------------------------------------------------------------------------------------------------------------------------------------------|

| When get access token started. | <pre>{<br />   "name": "action_start"<br />   "item_name": "get_access_token"<br />   "screen_name": "authenticating_screen"<br />}<pre/> |
|:------------------------------:|-------------------------------------------------------------------------------------------------------------------------------------------|

| When get access token finished with success. | <pre>{<br />   "name": "action_finished"<br />   "item_name": "get_access_token"<br />   "success": true<br />   "screen_name": "authenticating_screen"<br />}<pre/> |
|:--------------------------------------------:|----------------------------------------------------------------------------------------------------------------------------------------------------------------------|

| When get access token finished with failure. | <pre>{<br />   "name": "action_finished"<br />   "item_name": "get_access_token"<br />   "success": false<br />   "screen_name": "authenticating_screen"<br />}<pre/> |
|:--------------------------------------------:|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------|
