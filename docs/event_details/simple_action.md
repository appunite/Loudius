# simple_action

## When the event is called

If you’d like to track action start and action finish. You should use "action_start" events.

## Parameters

- event_name (required) - Always “simple_action”.
- item_name (required) - Unique name of the action that is happening.
- screen_name (optional) - Unique name of the screen that is opened.

## Examples

```json
{
  "event_name": "simple_action",
  "item_name": "User is checked-in",
  "screen_name": "reviewers_screen"
}
```

## Why this metric is tracked

- We can track important interactions within the app and measure its occurrences.
- Can be used for measuring important conversions.
