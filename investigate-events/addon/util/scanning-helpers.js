/**
 * Returns true if `char` is a digit character, false otherwise
 * @param {String} char - A single character
 * @private
 */
const isDigit = (char) => {
  if (typeof char !== 'string') {
    return false;
  }
  const codePoint = char.codePointAt(0);
  return codePoint >= 48 && codePoint <= 57;
};

/**
 * Returns true if `char` is an "alpha" character, false otherwise.
 * Alpha is defined as `a-z`, `A-Z`, and `.`.
 * @param {String} char - A single character
 * @private
 */
const isAlpha = (char) => {
  const codePoint = char.codePointAt(0);
  return (codePoint >= 65 && codePoint <= 90) ||
    (codePoint >= 97 && codePoint <= 122) ||
    char === '.';
};

/**
 * Returns true if `char` is a valid hex digit. `0-9`, `a-f`, and `A-F` are
 * considered valid.
 * @param {String} char - A single character
 * @private
 */
const isHex = (char) => {
  const codePoint = char.codePointAt(0);
  return (codePoint >= 48 && codePoint <= 57) ||
    (codePoint >= 65 && codePoint <= 70) ||
    (codePoint >= 97 && codePoint <= 102);
};

/**
 * Returns true if `char` is alphanumeric.
 * @see isDigit
 * @see isAlpha
 * @param {String} char - A single character
 * @private
 */
const isAlphaNumeric = (char) => {
  return isAlpha(char) || isDigit(char);
};

/**
 * Returns `true` if char is `<`, `>`, `=`, `!`, `|`, or `&`.
 * @param {String} char - A single character
 * @private
 */
const isOperatorChar = (char) => {
  const codePoint = char.codePointAt(0);
  return (codePoint >= 60 && codePoint <= 62) ||
    char === '!' || char === '|' || char === '&';
};

/**
 * Checks whether or not a number is in a range.
 * @param {Number} num - The number to check.
 * @param {Number} low - The lower bound, inclusive.
 * @param {Number} high - The upper bound, inclusive.
 * @private
 */
const isBetween = (num, low, high) => {
  return num >= low && num <= high;
};

export {
  isDigit,
  isAlpha,
  isHex,
  isAlphaNumeric,
  isOperatorChar,
  isBetween
};
