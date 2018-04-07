import { Promise } from 'rsvp';
import { next } from '@ember/runloop';
import wait from 'ember-test-helpers/wait';
import { waitFor } from 'ember-wait-for-test-helper/wait-for';

export default function(trigger) {
  return () => {
    trigger();
    return wait();
  };
}

export function localStorageClear() {
  return new Promise((resolve) => {
    localStorage.clear();
    waitFor(() => localStorage.getItem('reduxPersist:global') === null).then(() => {
      next(null, resolve);
    });
  });
}
