import * as LEXEMES from 'investigate-events/constants/lexemes';

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
 * Alpha is defined as `a-z`, `A-Z`, `.`, `:`, and `/`.
 * @param {String} char - A single character
 * @private
 */
const isAlpha = (char) => {
  const codePoint = char.codePointAt(0);
  return (codePoint >= 65 && codePoint <= 90) ||
    (codePoint >= 97 && codePoint <= 122) ||
    char === '.' || char === ':' || char === '/';
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
  [ candidate ] = candidate.split('/');
  candidate = candidate.split('.');
  if (candidate.length !== 4) {
    return false;
  }
  return candidate.every((octet) => {
    const num = parseInt(octet, 10);
    return !isNaN(num) && isBetween(num, 0, 255);
  });
};

const ipv4AddressToken = (text) => {
  let [, cidr ] = text.split('/');
  if (cidr === undefined) {
    cidr = null;
  } else if (cidr === '') {
    cidr = 'empty';
  } else {
    cidr = parseInt(cidr, 10);
  }
  return {
    type: LEXEMES.IPV4_ADDRESS,
    text,
    cidr
  };
};

// This monolithic regex checks if a string is a valid IPv6 address
// Taken from https://github.rsa.lab.emc.com/asoc/sa-ui/pull/5392
// and modified to accept invalid CIDR masks.
const IPv6Regex = new RegExp(/^\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:)))(\/\w*)?(%.+)?\s*$/);
const isIPv6Address = (candidate) => {
  const result = IPv6Regex.exec(candidate);
  return result && result[0] === candidate;
};

const ipv6AddressToken = (text) => {
  let [, cidr ] = text.split('/');
  if (cidr === undefined) {
    cidr = null;
  } else if (cidr === '') {
    cidr = 'empty';
  } else {
    cidr = parseInt(cidr, 10);
  }
  return {
    type: LEXEMES.IPV6_ADDRESS,
    text,
    cidr
  };
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
  ipv4AddressToken,
  isIPv6Address,
  ipv6AddressToken,
  isMACAddress
};
