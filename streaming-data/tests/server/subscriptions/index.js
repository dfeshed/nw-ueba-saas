/* eslint-disable no-console */
/* global process */

// ADDING A NEW ENDPOINT?
// 1) Create properly namespaced registration file that exports:
//  subscriptionDestination: String
//  requestDestination: String
//  prepareSendMessage: Function(frame)
//  delay: number, optional, time to wait to send message back
// 2) import it below
// 3) add it to INPUTS array


import testpromise1 from './test/promise/_1';
import testpromise2 from './test/promise/_2';
import testpromise3 from './test/promise/_3';
import testpromise4 from './test/promise/_4';

const INPUTS = [testpromise1, testpromise2, testpromise3, testpromise4];

const testProperty = function(obj, prop) {
  if (!obj[prop]) {
    console.error(`Property: ${prop} is missing on test configuration, exiting...`);
    process.exit(1);
  }
};

// Build subscription index
const SUBSCRIPTIONS = {};
INPUTS.forEach((test) => {
  testProperty(test, 'subscriptionDestination');
  testProperty(test, 'requestDestination');
  testProperty(test, 'prepareSendMessage');
  SUBSCRIPTIONS[test.subscriptionDestination] = test;
});

export default SUBSCRIPTIONS;