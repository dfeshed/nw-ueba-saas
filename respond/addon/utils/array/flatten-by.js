/**
 * Given an array of objects, each of which has a child array under a given attribute path, creates a new
 * array which contains all the child array members concatenated together.
 *
 * @example
 * Given this array:
 * ```
 * [ { foo: [1, 2] }, { foo: [3, 4, 5] } ]
 * ```
 * calling this method with this array and the attribute path 'foo' will result in the following array:
 * ```
 * [1, 2, 3, 4, 5]
 * ```
 *
 * @param {object[]} arr The array.
 * @param {string} attrName The attribute name or path.
 * @returns {[]}
 * @public
 */
export default function arrayFlattenBy(arr, attrName) {
  const arrays = (arr || []).mapBy(attrName);
  return [].concat(...arrays);
}
