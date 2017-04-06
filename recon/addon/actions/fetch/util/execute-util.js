import { later } from 'ember-runloop';

const wait = 50;

// Used to delay API responses using a run.later.
// Allows rendering to keep up.
//
// Takes a cb to execute later and a selector that
// returns the data to pass into the callback
export const delayedResponse = (cb, selector, time = wait) => {
  let count = 1;
  return (response) => {
    later(cb, selector(response), time * count++);
  };
};