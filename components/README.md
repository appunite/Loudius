# Dialogs

Dialogs are a standard way to inform the user about some result, giving the user option to react to it.

<details>
    <summary>Code snippets</summary>
  <br>
  ### Standard
https://github.com/appunite/Loudius/blob/develop/components/src/main/java/com/appunite/loudius/components/components/LoudiusDialog.kt#L50-L63
</details>



https://github.com/appunite/Loudius/blob/8e616a9a05e668d11425a40c9f452eb3091dcc2a/components/src/main/java/com/appunite/loudius/components/components/LoudiusDialog.kt#L50-L62

| Variant                                                      | Snapshot                                                     |
| :----------------------------------------------------------- | ------------------------------------------------------------ |
| **Standard** <br />This component gives the user option to confirm the information. | <img src="src/test/snapshots/images/com.appunite.loudius_PaparazziShowkaseTests_preview_tests[Default Group-LoudiusDialogSimplePreview,1,light].png" alt="alt text" style="zoom: 80%;" /> |
| **Advanced**<br />This component gives the user the choice out of two options, usually dismiss or confirmation. | <img src="src/test/snapshots/images/com.appunite.loudius_PaparazziShowkaseTests_preview_tests[Default Group-LoudiusDialogAdvancedPreview,1,light].png" alt="alt text" style="zoom: 80%;" /> |

## Variants

#### Error

<details>
    <summary>Code snippet</summary>
    <br>
https://github.com/appunite/Loudius/blob/8e616a9a05e668d11425a40c9f452eb3091dcc2a/components/src/main/java/com/appunite/loudius/components/components/LoudiusErrorDialog.kt#L29-L47
</details>

<img src="src/test/snapshots/images/com.appunite.loudius_PaparazziShowkaseTests_preview_tests[Default Group-LoudiusErrorDialogPreview,1,light].png" style="zoom:80%;" />

# Screens

Components replacing the whole screen, for example when indicating a error.

#### Error

Component replacing the whole screen when indicating the error, with a custom action button and title.

<details>
    <summary>Code snippet</summary>
    <br>
https://github.com/appunite/Loudius/main/components/src/main/java/com/appunite/loudius/components/components/LoudiusFullScreenError.kt#L39-L84
</details>



<img src="src/test/snapshots/images/com.appunite.loudius_PaparazziShowkaseTests_preview_tests[Default Group-LoudiusErrorScreenCustomTextsPreview,1,light].png" />

# List item

Component being displayed inside a list.

<details>
    <summary>Code snippet</summary>
    <br>
https://github.com/appunite/Loudius/blob/8e616a9a05e668d11425a40c9f452eb3091dcc2a/components/src/main/java/com/appunite/loudius/components/components/LoudiusOutlinedButton.kt#L37-L61
</details>

## Variants

#### Content with action

Component allows to display the "action" on the right of the component.



| Variant                                                      | Snapshot                                                     |
| ------------------------------------------------------------ | :----------------------------------------------------------- |
| **Content with action**<br />Component allows to display the "action" on the right of the component. | <img src="src\test\snapshots\images\com.appunite.loudius_PaparazziShowkaseTests_preview_tests[Default Group-LoudiusListItemContentAndActionPreview,1,light].png" alt="com.appunite.loudius_PaparazziShowkaseTests_preview_tests[Default Group-LoudiusListItemContentAndActionPreview,1,light]" /> |
| **Content with icon**<br />Component also takes a icon which is displayed on the left of the "content". | <img src="src/test/snapshots/images/com.appunite.loudius_PaparazziShowkaseTests_preview_tests[Default Group-LoudiusListItemContentAndIconPreview,1,light].png" /> |
| **Content only**<br /><br />Component taking only the "content". | <img src="src/test/snapshots/images/com.appunite.loudius_PaparazziShowkaseTests_preview_tests[Default Group-LoudiusListItemJustContentPreview,1,light].png" /> |



# Buttons

| Variant                       | Snapshot                                                     |
| ----------------------------- | ------------------------------------------------------------ |
| **Outlined button with icon** | <img src="src/test/snapshots/images/com.appunite.loudius_PaparazziShowkaseTests_preview_tests[Default Group-LoudiusOutlinedButtonWithIconPreview,1,light].png" /> |
| **Outlined button no icon**   | <img src="../components/src/test/snapshots/images/com.appunite.loudius_PaparazziShowkaseTests_preview_tests[Default Group-LoudiusOutlinedButtonPreview,1,light].png" /> |



# Topbar

Topbar is a component on the top of the screen.

| Variant                         | Snapshot                                                     |
| ------------------------------- | ------------------------------------------------------------ |
| **Topbar with back navigation** | <img src="src/test/snapshots/images/com.appunite.loudius_PaparazziShowkaseTests_preview_tests[Default Group-LoudiusTopAppBar,1,light].png" /> |
| **Topbar with no navigation**   | <img src="src/test/snapshots/images/com.appunite.loudius_PaparazziShowkaseTests_preview_tests[Default Group-LoudiusTopAppBarWithoutBackButton,1,light].png" /> |

</details>