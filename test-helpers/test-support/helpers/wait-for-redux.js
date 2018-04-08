import { Promise } from 'rsvp';
import { next } from '@ember/runloop';
import { registerWaiter } from '@ember/test';
import { getContext } from '@ember/test-helpers';

const getDescendantProp = function(obj, desc) {
  const arr = desc.split('.');
  while (arr.length) {
    obj = obj[arr.shift()];
  }
  return obj;
};

export async function waitForRedux(key, value) {
  return new Promise(async (resolve) => {
    let counter = 1;
    registerWaiter(() => counter === 0);
    const { owner } = getContext();
    const redux = owner.lookup('service:redux');
    const unsubscribe = redux.store.subscribe(() => {
      const currentValue = getDescendantProp(redux.store.getState(), key);
      if (currentValue === value) {
        unsubscribe();
        counter -= 1;
        next(null, resolve);
      }
    });
  });
}
