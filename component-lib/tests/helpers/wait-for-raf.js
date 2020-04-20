import { registerWaiter } from '@ember/test';
import { settled } from '@ember/test-helpers';

export function waitForRaf() {
  let RAFcompleted = false;
  let scheduledRAF = null;

  registerWaiter(() => {
    if (RAFcompleted === true) {
      return true;
    }
    if (scheduledRAF === null) {
      scheduledRAF = requestAnimationFrame(() => requestAnimationFrame(() => {
        scheduledRAF = null;
        RAFcompleted = true;
      }));
    }
    return false;
  });
  return settled();
}
