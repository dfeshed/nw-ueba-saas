/**
 * Converts integer to numbers comma separated by thousands.
 * @public
 */
export const thousandFormat = function(number) {
  if (number || number === 0) {
    return number.toLocaleString();
  }
  return number;
};