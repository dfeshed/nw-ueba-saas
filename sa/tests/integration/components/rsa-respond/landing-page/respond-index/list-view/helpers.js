import Ember from 'ember';

const {
  RSVP: {
    Promise
  }
} = Ember;

/**
 * @description: Evaluates a condition and returns immediatelly or waits until the condition is fulfilled.
 * @param {function} test A method used to determinate if the promise is fulfilled. It must return truthy/falsy values
 * @param {number} [msec=10] time in milliseconds to wait between each iteration
 * @param {number} [maxWait=1000] Max wait time for promise fulfillment
 * @returns {Object} Promise. Promise's result will be `true` if `test` was fulfilled within `maxWait` time, otherwise false.
 * @public
 */
export function waitFor(test, msec = 10, maxWait = 1000) {
  let promise = new Promise((resolve) => {
    let maxCount = Math.ceil(maxWait / msec);
    let count = 0;
    _testCondition(resolve, test, msec, count, maxCount);
  });

  return promise;
}

function _testCondition(resolve, test, msec, count, maxCount) {
  let testResult = test();
  if (!testResult && count < maxCount) {
    setTimeout(function() {
      count++;
      _testCondition(resolve, test, msec, count, maxCount);
    }, msec);
  } else {
    resolve(testResult);
  }
}