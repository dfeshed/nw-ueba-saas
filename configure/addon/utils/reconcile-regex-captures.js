/**
 * Returns true if the provided regex's capture groups is fewer than the captures defined in the captures arguments.
 * The captures argument must be an array of objects, each of which must have an "index" property indicating the capture
 * group: 0 = Full Capture, 1 = First Capture, 2 = Second Capture, etc.
 *
 * The regex can have more captures groups than than defined in the captures argument, but it cannot have fewer.
 * @param regex String
 * @param captures Array
 * @returns {boolean}
 * @public
 */
export const hasInvalidCaptures = (regex, captures) => {
  // find the number of captures in the regex beyond full capture
  const captureCount = (new RegExp(`${regex}|`)).exec('').length - 1; // minus 1 so that zero represents a full capture
  // find the last capture entry in the rule
  const lastCapture = captures.length && captures[captures.length - 1];
  // if the number of found captures is less than the index of the last capture, there are missing captures
  return captureCount < parseInt(lastCapture.index, 10);
};