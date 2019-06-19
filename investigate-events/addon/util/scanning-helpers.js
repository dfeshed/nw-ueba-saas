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
 * Alpha is defined as `a-z`, `A-Z`, `.`, and `:`.
 * @param {String} char - A single character
 * @private
 */
const isAlpha = (char) => {
  const codePoint = char.codePointAt(0);
  return (codePoint >= 65 && codePoint <= 90) ||
    (codePoint >= 97 && codePoint <= 122) ||
    char === '.' || char === ':';
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
 * Checks whether or not a number is in a range.
 * @param {Number} num - The number to check.
 * @param {Number} low - The lower bound, inclusive.
 * @param {Number} high - The upper bound, inclusive.
 * @private
 */
const isBetween = (num, low, high) => {
  return num >= low && num <= high;
};

/**
 * Checks if a string is a valid IPv4 address
 * @param {String} candidate - A string that is possibly an IPv4 address
 */
const isIPv4Address = (candidate) => {
  candidate = candidate.split('.');
  if (candidate.length !== 4) {
    return false;
  }
  return candidate.every((octet) => {
    const num = parseInt(octet, 10);
    return !isNaN(num) && isBetween(num, 0, 255);
  });
};

// This monolithic regex checks if a string is a valid IPv6 address
const IPv6Regex = new RegExp(/^((?:[a-fA-F\d]{1,4}:){7}(?:[a-fA-F\d]{1,4}|:)|(?:[a-fA-F\d]{1,4}:){6}(?:(?:25[0-5]|2[0-4]\d|1\d\d|[1-9]\d|\d)(?:\.(?:25[0-5]|2[0-4]\d|1\d\d|[1-9]\d|\d)){3}|:[a-fA-F\d]{1,4}|:)|(?:[a-fA-F\d]{1,4}:){5}(?::(?:25[0-5]|2[0-4]\d|1\d\d|[1-9]\d|\d)(?:\.(?:25[0-5]|2[0-4]\d|1\d\d|[1-9]\d|\d)){3}|(:[a-fA-F\d]{1,4}){1,2}|:)|(?:[a-fA-F\d]{1,4}:){4}(?:(:[a-fA-F\d]{1,4}){0,1}:(?:25[0-5]|2[0-4]\d|1\d\d|[1-9]\d|\d)(?:\.(?:25[0-5]|2[0-4]\d|1\d\d|[1-9]\d|\d)){3}|(:[a-fA-F\d]{1,4}){1,3}|:)|(?:[a-fA-F\d]{1,4}:){3}(?:(:[a-fA-F\d]{1,4}){0,2}:(?:25[0-5]|2[0-4]\d|1\d\d|[1-9]\d|\d)(?:\.(?:25[0-5]|2[0-4]\d|1\d\d|[1-9]\d|\d)){3}|(:[a-fA-F\d]{1,4}){1,4}|:)|(?:[a-fA-F\d]{1,4}:){2}(?:(:[a-fA-F\d]{1,4}){0,3}:(?:25[0-5]|2[0-4]\d|1\d\d|[1-9]\d|\d)(?:\.(?:25[0-5]|2[0-4]\d|1\d\d|[1-9]\d|\d)){3}|(:[a-fA-F\d]{1,4}){1,5}|:)|(?:[a-fA-F\d]{1,4}:){1}(?:(:[a-fA-F\d]{1,4}){0,4}:(?:25[0-5]|2[0-4]\d|1\d\d|[1-9]\d|\d)(?:\.(?:25[0-5]|2[0-4]\d|1\d\d|[1-9]\d|\d)){3}|(:[a-fA-F\d]{1,4}){1,6}|:)|(?::((?::[a-fA-F\d]{1,4}){0,5}:(?:25[0-5]|2[0-4]\d|1\d\d|[1-9]\d|\d)(?:\.(?:25[0-5]|2[0-4]\d|1\d\d|[1-9]\d|\d)){3}|(?::[a-fA-F\d]{1,4}){1,7}|:)))(%[0-9a-zA-Z]{1,})?$/);
const isIPv6Address = (candidate) => {
  const result = IPv6Regex.exec(candidate);
  return result && result[0] === candidate;
};

const isMACAddress = (candidate) => {
  candidate = candidate.split(':');
  if (candidate.length !== 6) {
    return false;
  }
  return candidate.every((byte) => {
    const num = parseInt(byte, 16);
    return !isNaN(num) && isBetween(num, 0, 255);
  });
};

export {
  isDigit,
  isAlpha,
  isAlphaNumeric,
  isBetween,
  isIPv4Address,
  isIPv6Address,
  isMACAddress
};
