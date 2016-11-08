/**
 * @file if a value is falsy, returns a default value
 * Checks if the first argument is truly, if it is it returns it, otherwise returns the default value
 * @public
 */
import Ember from 'ember';

const { Helper: { helper } } = Ember;

export function truthyOrDefault([meta, falsyValue]) {
  return meta ? meta : falsyValue;
}

export default helper(truthyOrDefault);
