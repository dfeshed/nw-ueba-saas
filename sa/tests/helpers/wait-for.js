import { Promise } from 'rsvp';
import { next } from '@ember/runloop';
import wait from 'ember-test-helpers/wait';

export default function(trigger) {
  return () => {
    trigger();
    return wait();
  };
}

export function localStorageClear() {
  return new Promise((resolve) => {
    localStorage.clear();
    next(resolve);
  });
}
