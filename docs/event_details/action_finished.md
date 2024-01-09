# action_finished

## When the event is called

Called when the "action_start" is finished.

## Parameters

- event_name (required) - Always “action_finished”.
- item_name (required) - Unique action name. The same value as in "action_start".
- success (required) - True if action succeeded and false if not.
- screen_name (required) - Unique name of the screen that is opened.

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
  "screen_name": "reviewers_screen"
}
```

## Why this metric is tracked

- We can measure how many interactions has finished.
- We can measure how many actions were successful.
- In case of debugging errors, we can measure user interactions that are preceding the failure event.
