# screen_opened

## When the event is called

If you’d like to track when screen is opened and when user navigates back to this screen from previous screen.

## Parameters

- event_name (required) - Always “screen_opened”.
- item_name (required) - Unique name of the screen that is opened.

## Examples

```json
{
  "event_name": "screen_opened",
  "item_name": "reviewers_screen"
}
```

## Why this metric is tracked

- We can track when user open screen.
- We can track when user navigates back to this screen from previous screen.
