import Ember from 'ember';

const {
  RSVP: {
    Promise
  },
  merge,
  run,
  Object: EmberObject
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
  const promise = new Promise((resolve) => {
    const maxCount = Math.ceil(maxWait / msec);
    const count = 0;
    _testCondition(resolve, test, msec, count, maxCount);
  });

  return promise;
}

function _testCondition(resolve, test, msec, count, maxCount) {
  const testResult = test();
  if (!testResult && count < maxCount) {
    run.later(function() {
      count++;
      _testCondition(resolve, test, msec, count, maxCount);
    }, msec);
  } else {
    resolve(testResult);
  }
}

/**
 * @name createIncident
 * @description Helper function for generation of incidents
 * @param params Attributes to be merged or to override default properties of Incident object.
 * @returns EmberObject representing an Incident with default values
 * @private
 */
export function createIncident(params) {

  const attr = {
    id: 'INC-1',
    riskScore: 10,
    name: 'Suspected command and control communication with www.mozilla.com',
    createdBy: 'User X',
    created: new Date().getTime(), // Current time in milliseconds
    lastUpdated: '2015-10-10',
    statusSort: 0, // Status: New
    prioritySort: 0, // Priority: Low
    alertCount: 10,
    eventCount: 2,
    sources: ['Event Stream Analysis'],
    assignee: {
      id: '1'
    },
    categories: []
  };

  return EmberObject.create(merge(attr, params));
}


