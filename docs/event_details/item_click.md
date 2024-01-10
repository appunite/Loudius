# item_click

## When the event is called

Fired when the user clicked on an item.

## Parameters

- event_name (required) - Always “item_click”.
- item_name (required) - Name of the item that user clicked. Keep in mind, it should uniquely identify every item on every screen.
- screen_name (required) - Unique name of the screen that is opened.

## Examples

```json
{
  "event_name": "item_click",
  "item_name": "pull_request",
  "screen_name": "reviewers_screen"
}
```

## Why this metric is tracked

- We can check if users clicks on given item.
- We can check what items are most important for our users.
- By very low conversion, we might find an opportunity for improving the item placement or UI.
