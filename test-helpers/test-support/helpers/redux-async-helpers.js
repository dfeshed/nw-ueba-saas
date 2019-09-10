import RSVP from 'rsvp';
import { visit } from '@ember/test-helpers';
import { schedule, run } from '@ember/runloop';
import { registerAsyncHelper } from '@ember/test';
import Ember from 'ember';

const {
  Test: {
    promise: TestPromise
  },

  Test
} = Ember;

const getDescendantProp = function(obj, desc) {
  const arr = desc.split('.');
  while (arr.length) {
    obj = obj[arr.shift()];
  }
  return obj;
};

const visitAndWaitForReduxStateChange = function(app, url, stateString) {
  return RSVP.allSettled([visit(url), waitForReduxStateChangeAppTest(app, stateString)]);
};

const waitForReduxStateChangeAppTest = function(app, stateString) {
  const redux = app.__container__.lookup('service:redux');

  return waitForReduxStateChange(redux, stateString);
};

const waitForReduxStateChange = function(redux, stateString) {

  return new TestPromise(function(resolve) {
    // inform the test framework that there is an async operation in progress,
    // so it shouldn't consider the test complete
    Test.adapter.asyncStart();

    // Establish the starting state for the property of interest
    let currentValue = getDescendantProp(redux.store.getState(), stateString);
    const unsubscribe = redux.store.subscribe(function() {
      const previousValue = currentValue;
      currentValue = getDescendantProp(redux.store.getState(), stateString);

      if (currentValue && previousValue !== currentValue) {
        unsubscribe();

        // inform the test framework that this async operation is complete
        Test.adapter.asyncEnd();

        // wait until the afterRender queue to resolve this promise,
        // to give any side effects of the promise resolving a chance to
        // occur and settle
        schedule('afterRender', null, () => {
          resolve(currentValue);
        });
      }
    });
  });
};

export const waitForReduxStateToEqual = function(app, stateString, stateValue) {
  const redux = app.__container__.lookup('service:redux');

  return new TestPromise(function(resolve) {

    // inform the test framework that there is an async operation in progress,
    // so it shouldn't consider the test complete
    Test.adapter.asyncStart();

    const unsubscribe = redux.store.subscribe(function() {
      const currentValue = getDescendantProp(redux.store.getState(), stateString);

      if (currentValue === stateValue) {
        unsubscribe();

        Test.adapter.asyncEnd();

        // wait until the afterRender queue to resolve this promise,
        // to give any side effects of the promise resolving a chance to
        // occur and settle
        schedule('afterRender', null, resolve);
      }
    });
  });
};

const dispatchReduxAction = function(app, action) {
  const redux = app.__container__.lookup('service:redux');
  run(function() {
    redux.dispatch(action);
  });
  return app.testHelpers.wait();
};

registerAsyncHelper('waitForReduxStateChange', waitForReduxStateChange);
registerAsyncHelper('waitForReduxStateToEqual', waitForReduxStateToEqual);
registerAsyncHelper('dispatchReduxAction', dispatchReduxAction);
registerAsyncHelper('visitAndWaitForReduxStateChange', visitAndWaitForReduxStateChange);

export default waitForReduxStateChange;