# action_start

## When the event is called

This event should be tracked only if is followed by [action_finished](action_finished.md) event. If you don’t want to track finish, you should use [simple_action](simple_action.md) event.

## Parameters

- event_name (required) - Always “action_start”.
- item_name (required) - Unique action name. The same item_name should be used in [action_finished](action_finished.md) event.
- screen_name (optional) - Unique name of the screen that is opened.

## Examples

```json
{
  "event_name": "action_start",
  "item_name": "Downloading User Profile",
  "screen_name": "reviewers_screen"
}
```

## Why this metric is tracked

- We can measure how many interactions has started.
- By checking [action_finished](action_finished.md), we can measure how many actions were successful.
