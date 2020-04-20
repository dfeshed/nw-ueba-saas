import { get } from '@ember/object';
import arrayToHashKeys from 'component-lib/utils/array/to-hash-keys';

/**
 * Searches for the first object in an array whose property value matches one of a given list of values.
 *
 * Similar to Ember's `array.findBy(key, value)` but takes an array of values instead of a single value.
 *
 * @param {object[]} arr
 * @param {string} attrName The name of the property to search by.
 * @param {[]} values An array of property values to match.
 * @returns The first matching array item found, if any; null otherwise.
 * @public
 */
export default function arrayFindByList(arr, attrName, values = []) {
  const hash = arrayToHashKeys(values);
  return (arr || []).find((item) => item && (get(item, attrName) in hash));
}
