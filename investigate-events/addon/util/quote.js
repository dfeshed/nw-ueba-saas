/**
 * Single quotes are the first and last characters
 * @type {RegExp}
 * @public
 */
const properlyQuoted = /^'(.*)'$/;

/**
 * Leading and trailing characters are single or double quotes
 * @type {RegExp}
 * @private
 */
const _quoted = /^('|")(.*)('|")$/;

/**
 * Escapes special characters. These are:
 * 1. single quotes
 * 2. backslash
 * @param {string} value String to be escaped
 * @private
 */
const _escapeSpecialCharacters = (value) => {
  return value.replace(/\\/g, '\\\\').replace(/'/g, "\\'");
};

/**
 * Will add single quotes to a string if they do not already exist. Will
 * escape single quotes within the string.
 * @param {string} value String to quote
 * @public
 */
const quote = (value) => {
  let ret;

  // If surrounded by the desired quotes, make sure inner single quotes are
  // escaped.
  if (properlyQuoted.test(value)) {
    const [ , , val ] = value.match(_quoted);
    return `'${_escapeSpecialCharacters(val)}'`;
  }

  // Test if we have a quoted string
  const match = value.match(_quoted);
  if (match) {
    const [ fullVal, beginningQuote, val, endingQuote ] = match;
    if (beginningQuote === endingQuote && endingQuote === '"') {
      // Change double quotes to single quotes
      ret = `'${_escapeSpecialCharacters(val)}'`;
    } else {
      // Mixed quotes
      ret = `'${_escapeSpecialCharacters(fullVal)}'`;
    }
  } else {
    // no match, must be bare string so quote it
    ret = `'${_escapeSpecialCharacters(value)}'`;
  }

  return ret;
};

export {
  properlyQuoted
};
export default quote;