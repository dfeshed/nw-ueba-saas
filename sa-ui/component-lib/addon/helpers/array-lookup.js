/**
 * @file Array Lookup helper
 * Looks up a member of an array at a given index.
 * Isn't this supported natively by Ember??
 * @public
 */
import Helper from '@ember/component/helper';

export function arrayLookup([ arr, index ]/* , hash */) {
  return arr && arr[index];
}

export default Helper.helper(arrayLookup);
