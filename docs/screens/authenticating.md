# Authenticating

<img src="../analytics_imgs/authenticating/authenticating_screen.png" width=20% height=20%>

## Documentation

| After user clicks log in, authentication started. | <pre>{<br />   "name": "action_start"<br />   "item_name": "authentication"<br />}<pre/> |
|:-------------------------------------------------:|------------------------------------------------------------------------------------------|

| When application gets access token and authentication finished with success. | <pre>{<br />   "name": "action_finished"<br />   "item_name": "authentication"<br />   "success": true<br />}<pre/> |
|:----------------------------------------------------------------------------:|---------------------------------------------------------------------------------------------------------------------|

| When application doesn't get access token and  When authentication finished with failure. | <pre>{<br />   "name": "action_finished"<br />   "item_name": "authentication"<br />   "success": false<br />}<pre/> |
|:-----------------------------------------------------------------------------------------:|----------------------------------------------------------------------------------------------------------------------|

| When get access token started. | <pre>{<br />   "name": "action_start"<br />   "item_name": "get_access_token"<br />}<pre/> |
|:------------------------------:|--------------------------------------------------------------------------------------------|

| When get access token finished with success. | <pre>{<br />   "name": "action_finished"<br />   "item_name": "get_access_token"<br />   "success": true<br />}<pre/> |
|:--------------------------------------------:|-----------------------------------------------------------------------------------------------------------------------|

| When get access token finished with failure. | <pre>{<br />   "name": "action_finished"<br />   "item_name": "get_access_token"<br />   "success": false<br />}<pre/> |
|:--------------------------------------------:|------------------------------------------------------------------------------------------------------------------------|

| When login error is shown. | <pre>{<br />   "name": "screen_opened"<br />   "item_name": "login_error"<br />}<pre/> |
|:--------------------------:|----------------------------------------------------------------------------------------|

| When generic error is shown. | <pre>{<br />   "name": "screen_opened"<br />   "item_name": "generic_error"<br />}<pre/> |
|:----------------------------:|------------------------------------------------------------------------------------------|
