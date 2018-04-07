import { Promise } from 'rsvp';
import { next } from '@ember/runloop';
import { registerWaiter } from '@ember/test';

const getDescendantProp = function(obj, desc) {
  const arr = desc.split('.');
  while (arr.length) {
    obj = obj[arr.shift()];
  }
  return obj;
};

export async function waitForRedux(context, key, value) {
  return new Promise(async (resolve) => {
    let counter = 1;
    registerWaiter(() => counter === 0);
    const redux = context.owner.lookup('service:redux');
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
