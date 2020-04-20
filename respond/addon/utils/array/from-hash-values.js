/**
 * Creates an array with all the values in a given hash.
 *
 * @param {object} hash The hash whose values will populate a new array.
 * @returns {[]}
 * @public
 */
export default function(hash = {}) {
  return Object.keys(hash).map((key) => hash[key]);
}
