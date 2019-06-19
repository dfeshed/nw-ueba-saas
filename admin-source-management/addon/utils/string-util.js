/**
 * Utility function to remove both single and double quotes on a string
  * @param {*} string
 * @public
 */
export const removeQuotes = (entry) => {
  return entry.trim().replace(/["']/g, '');
};

/**
 * Utility function to convert the passed in array to a string separated by a new line
  * @param {*} array
 * @public
 */
export const arrToString = (arr) => {
  return arr.map((d) => `"${d}"`).join('\n');
};
