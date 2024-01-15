# action_finished

## When the event is called

Called when the "action_start" is finished.

## Parameters

- event_name (required) - Always “action_finished”.
- item_name (required) - Unique action name. The same value as in "action_start".
- success (required) - True if action succeeded and false if not.
- screen_name (optional) - Unique name of the screen that is opened.
- error_message (required if success is false) - Error message that will help trace the root of the issue.

## Examples

```json
{
  "event_name": "action_finished",
  "item_name": "Downloading User Profile",
  "success": true,
  "screen_name": "reviewers_screen"
}
```

or

```json
{
  "event_name": "action_finished",
  "item_name": "Downloading User Profile",
  "success": false,
  "screen_name": "reviewers_screen",
  "error_message": "Some error message."
}
```

## Why this metric is tracked

- We can measure how many interactions has finished.
- We can measure how many actions were successful.
- In case of debugging errors, we can measure user interactions that are preceding the failure event.
