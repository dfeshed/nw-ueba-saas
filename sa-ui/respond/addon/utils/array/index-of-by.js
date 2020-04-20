import { get } from '@ember/object';

/**
 * Finds and returns the index of the first array member whose key matches a given value.
 * Similar to `Array.findBy` but returns the index not the array member.
 *
 * Note: we could perform this operation by using Array.mapBy() and then Array.indexOf(), but this
 * implementation is more performant. So if you want it done fast, you've come to the right place. :-)
 *
 * @param {Array} arr The array to be searched.
 * @param {String} attrName The name of the attribute whose value is to be matched.
 * @param {String|Number} value The attribute value to be matched.
 * @private
 */
export default function arrayIndexOfBy(arr = [], attrName, value) {
  const len = arr.length;
  let index = -1;
  for (let i = 0; i < len; i++) {
    if (get(arr[i], attrName) === value) {
      index = i;
      break;
    }
  }
  return index;
}