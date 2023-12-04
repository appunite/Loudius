# button_click

## When the event is called

Fired when a user clicked on a button or link.

## Parameters

- event_name (required) - Always “button_click”.
- item_name (required) - Name of the button or link that user clicked. Keep in mind, it should uniquely identify every button on every screen. So if there are two buttons with the same name, you need to add some suffix to the event. If the text on the button changes, it’s usually better to keep the “item_name” the same, so we can keep analytics dashboard simpler.

## Examples

```json
{
  "event_name": "button_click",
  "item_name": "Check-in button",
}
```

## Why this metric is tracked

- We can check if users clicks on given button/link.
- We can check what buttons are most important for our users.
- By very low conversion, we might find an opportunity for improving the button placement or UI.
