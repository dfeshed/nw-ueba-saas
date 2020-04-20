export default function triggerNativeEvent(element, eventName) {
  if (document.createEvent) {
    const event = document.createEvent('Events');
    event.initEvent(eventName, true, false);
    element.dispatchEvent(event);
  } else {
    element.fireEvent(`on${eventName}`);
  }
}