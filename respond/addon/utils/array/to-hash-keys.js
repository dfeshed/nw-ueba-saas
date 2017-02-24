/**
 * Creates a hash (possibly empty) of the values found in a given array (if any).
 * The hash keys will correspond to the (stringified) values in the array.  Every hash key is mapped to `true`.
 *
 * Note: If the array is empty or null, a hash with no keys is returned.
 *
 * @param {[]} [arr] The array whose values are to be hash mapped.
 * @returns {object}
 * @public
 */
export default function arrayToHashKeys(arr) {
  return (arr || []).reduce((p, v) => {
    p[v] = true;
    return p;
  }, {});
}