# Dialogs

Dialogs are a standard way to inform the user about some result, giving the user option to react to it.

## Variants

| Variant                                                                                                                                                                                                                                         | Snapshot                                                                                                                                                                                    |
|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Standard** <br />[Source Code](../components/src/main/java/com/appunite/loudius/components/components/LoudiusDialog.kt#L50-L62)<br /><br />This component gives the user option to confirm the information.<br />                             | <img src="src/test/snapshots/images/com.appunite.loudius_PaparazziShowkaseTests_preview_tests[Default Group-LoudiusDialogSimplePreview,1,light].png" alt="alt text" style="zoom: 80%;" />   |
| **Advanced**<br />[Source Code](../components/src/main/java/com/appunite/loudius/components/components/LoudiusDialog.kt#L64-L85)<br /><br />This component gives the user the choice out of two options, usually dismiss or confirmation.<br /> | <img src="src/test/snapshots/images/com.appunite.loudius_PaparazziShowkaseTests_preview_tests[Default Group-LoudiusDialogAdvancedPreview,1,light].png" alt="alt text" style="zoom: 80%;" /> |
| **Error**<br />[Source Code](../components/src/main/java/com/appunite/loudius/components/components/LoudiusErrorDialog.kt#L49-L55)<br /><br />Component indicating error.<br />                                                                 | <img src="src/test/snapshots/images/com.appunite.loudius_PaparazziShowkaseTests_preview_tests[Default Group-LoudiusErrorDialogPreview,1,light].png" style="zoom:80%;" />                    |





# Screens

Components replacing the whole screen, for example when indicating an error.

# Variants

| Variant                                                                                                                                                                                                                                                          | Snapshot                                                                                                                                                          |
|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Error**<br />[Source Code](../components/src/main/java/com/appunite/loudius/components/components/LoudiusFullScreenError.kt#L106-L112)<br /><br />Component replacing the whole screen when indicating the error, with a custom action button and title.<br /> | <img src="src/test/snapshots/images/com.appunite.loudius_PaparazziShowkaseTests_preview_tests[Default Group-LoudiusErrorScreenCustomTextsPreview,1,light].png" /> |

# List item

Component being displayed inside a list.

## Variants

#### Content with action

Component allows to display the "action" on the right of the component.



| Variant                                                                                                                                                                                                                                         | Snapshot                                                                                                                                                                                                                                                                                          |
|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Content with action**<br />[Source Code](../components/src/main/java/com/appunite/loudius/components/components/LoudiusListItem.kt#L143-L159)<br /><br />Component allows to display the "action" on the right of the component.<br /><br />  | <img src="src\test\snapshots\images\com.appunite.loudius_PaparazziShowkaseTests_preview_tests[Default Group-LoudiusListItemContentAndActionPreview,1,light].png" alt="com.appunite.loudius_PaparazziShowkaseTests_preview_tests[Default Group-LoudiusListItemContentAndActionPreview,1,light]" /> |
| **Content with icon**<br />[Source Code](../components/src/main/java/com/appunite/loudius/components/components/LoudiusDialog.kt#L161-L180)<br /><br />Component also takes a icon which is displayed on the left of the "content".<br /><br /> | <img src="src/test/snapshots/images/com.appunite.loudius_PaparazziShowkaseTests_preview_tests[Default Group-LoudiusListItemContentAndIconPreview,1,light].png" />                                                                                                                                 |
| **Content only**<br />[Source Code](../components/src/main/java/com/appunite/loudius/components/components/LoudiusDialog.kt#L89-L101)<br /><br />Component taking only the "content".<br /><br />                                               | <img src="src/test/snapshots/images/com.appunite.loudius_PaparazziShowkaseTests_preview_tests[Default Group-LoudiusListItemJustContentPreview,1,light].png" />                                                                                                                                    |



# Buttons

| Variant                                                                                                                                                   | Snapshot                                                                                                                                                                |
|-----------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Outlined button with icon**<br />[Source Code](../components/src/main/java/com/appunite/loudius/components/components/LoudiusOutlinedButton.kt#L37-L61) | <img src="src/test/snapshots/images/com.appunite.loudius_PaparazziShowkaseTests_preview_tests[Default Group-LoudiusOutlinedButtonWithIconPreview,1,light].png" />       |
| **Outlined button no icon**<br />[Source Code](../components/src/main/java/com/appunite/loudius/components/components/LoudiusOutlinedButton.kt#L102-L117) | <img src="../components/src/test/snapshots/images/com.appunite.loudius_PaparazziShowkaseTests_preview_tests[Default Group-LoudiusOutlinedButtonPreview,1,light].png" /> |



# Topbar

Topbar is a component on the top of the screen.

| Variant                                                                                                                                                | Snapshot                                                                                                                                                       |
|--------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Topbar with back navigation**<br />[Source Code](../components/src/main/java/com/appunite/loudius/components/components/LoudiusTopAppBar.kt#L61-L70) | <img src="src/test/snapshots/images/com.appunite.loudius_PaparazziShowkaseTests_preview_tests[Default Group-LoudiusTopAppBar,1,light].png" />                  |
| **Topbar with no navigation**<br /> [Source Code](../components/src/main/java/com/appunite/loudius/components/components/LoudiusTopAppBar.kt#L72-L80)  | <img src="src/test/snapshots/images/com.appunite.loudius_PaparazziShowkaseTests_preview_tests[Default Group-LoudiusTopAppBarWithoutBackButton,1,light].png" /> |