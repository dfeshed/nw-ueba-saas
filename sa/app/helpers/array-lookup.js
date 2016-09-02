/**
 * @file Array Lookup helper
 * Looks up a member of an array at a given index.
 * Isn't this supported natively by Ember??
 * @public
 */
import Ember from 'ember';

const { Helper } = Ember;

export function arrayLookup([ arr, index ]/* , hash */) {
  return arr && arr[index];
}

export default Helper.helper(arrayLookup);
