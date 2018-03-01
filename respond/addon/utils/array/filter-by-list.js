import { get } from '@ember/object';
import arrayToHashKeys from 'component-lib/utils/array/to-hash-keys';

/**
 * Searches for all objects in an array whose property value matches one of a given list of values.
 *
 * Similar to Ember's `array.filterBy(key, value)` but takes an array of values instead of a single value.
 *
 * @param {object[]} arr
 * @param {string} attrName The name of the property to search by.
 * @param {[]} values An array of property values to match.
 * @returns {object[]} The subset of `arr` which match any of the given `values`, if any; possibly empty.
 * @public
 */
export default function arrayFilterByList(arr, attrName, values = []) {
  const hash = arrayToHashKeys(values);
  return (arr || []).filter((item) => item && (get(item, attrName) in hash));
}
