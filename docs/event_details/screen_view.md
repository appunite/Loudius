# screen_view

## When the event is called

If you’d like to track when the user sees some component.

## Parameters

- event_name (required) - Always “screen_view”.
- item_name (required) - Unique name of the component that is opened.

## Examples

```json
{
  "event_name": "screen_view",
  "item_name": "xiaomi_permission_dialog"
}
```

## Why this metric is tracked

- We can track when user see application component like dialog.
