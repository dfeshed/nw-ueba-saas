// Makes use of RegExp's negative lookahead
const QUOTE_NOT_FOLLOWED_BY_SLASH = /'(?!\\)/g;

/**
 * RegExp does not natively support negative lookbehinds. Negative lookbehinds
 * provide the ability to say, I'm looking for any `x` not preceded by `y`.
 * This function simulates that ability by reversing the search string, uses a
 * negative lookahead RegExp to do the replacement, then reverses the string
 * again.
 * @param {string} value The string to search within
 * @param {RegExp} search A negative lookahead RegExp. The pattern needs to
 * be in the opposite order of what you really want to find.
 * @param {string} replacement The string to replace the found text
 * @private
 */
const _lookbehindReplace = (value, search, replacement) => {
  const reversedString = value.split('').reverse().join('');
  const replacedString = reversedString.replace(search, replacement);
  return replacedString.split('').reverse().join('');
};

/**
 * Leading and trailing characters are single or double quotes
 * @type {RegExp}
 * @private
 */
const _quoted = /^('|")(.*)('|")$/;

/**
 * Single quotes are the first and last characters
 * @type {RegExp}
 * @public
 */
const properlyQuoted = /^'(.*)'$/;

/**
 * Escapes a backslash (\) character following these rules:
 * 1. \ is \\
 * 2. \\ is \\
 * 3. \' is ignored
 * 4. \\' is \\\' (even number of slashes followed by a single quote are
 *                 converted to an odd number of slashes and a single quote)
 * @param {string} value String to be escaped
 * @private
 */
const escapeBackslash = (value) => {
  return value.replace(/\\\\?(?!')/g, '\\\\');
};

/**
 * Escapes all single quotes within the provided string.
 * @param {string} value The string to search within
 * @public
 */
const escapeSingleQuotes = (value) => {
  return _lookbehindReplace(value, QUOTE_NOT_FOLLOWED_BY_SLASH, "'\\");
};

/**
 * Removes wrapping single quotes from the provided string.
 * @param {string} value The string to search within
 * @public
 */
const stripOuterSingleQuotes = (value) => {
  return value.replace(/^'(.*)'$/g, '$1');
};

/**
 * Will add single quotes to a string if they do not already exist. Will
 * convert double quotes to single quotes. Will wrap mixed quotes in single
 * quotes.
 * @param {string} value String to quote
 * @public
 */
const quote = (value) => {
  let ret;

  // If surrounded by the desired quotes, return
  if (properlyQuoted.test(value)) {
    return value;
  }

  // Test if we have a quoted string
  const match = value.match(_quoted);
  if (match) {
    const [ fullVal, beginningQuote, val, endingQuote ] = match;
    if (beginningQuote === endingQuote && endingQuote === '"') {
      // Change double quotes to single quotes
      ret = `'${val}'`;
    } else {
      // Mixed quotes
      ret = `'${fullVal}'`;
    }
  } else {
    // no match, must be bare string so quote it
    ret = `'${value}'`;
  }

  return ret;
};

export {
  escapeBackslash,
  escapeSingleQuotes,
  properlyQuoted,
  stripOuterSingleQuotes
};
export default quote;