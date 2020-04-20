import { Promise } from 'rsvp';
import { next } from '@ember/runloop';
import wait from 'ember-test-helpers/wait';
import { waitUntil } from '@ember/test-helpers';

export default function(trigger) {
  return () => {
    trigger();
    return wait();
  };
}

export function localStorageClear() {
  localStorage.clear();
  return new Promise((resolve) => {
    waitUntil(() => localStorage.getItem('reduxPersist:global') === null).then(() => {
      next(null, resolve);
    });
  });
}
