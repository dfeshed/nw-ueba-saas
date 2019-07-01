import * as LEXEMES from 'investigate-events/constants/lexemes';
import { SEARCH_TERM_MARKER } from 'investigate-events/constants/pill';
import { isDigit, isAlphaNumeric, isIPv4Address, isIPv6Address, isMACAddress } from 'investigate-events/util/scanning-helpers';

/**
 * The Scanner class takes an source string and transforms it into a set of tokens
 * to be consumed by the parser.
 * @throws Error - Will throw errors if they occur during scanning, so catch
 * them if handling user input or scanning unvalidated input.
 */
class Scanner {
  /**
   * Returns a new `Scanner` with the specified source string.
   * @param {String} source - A source string to be consumed and turned into tokens.
   * @param {Object} metaDefinitions - An array that lists valid meta and their properties
   * @public
   */
  constructor(source) {
    // Remove whitespace from ends of source string and store
    this.source = source.trim();
    // The array of tokens that will be populated
    this.tokens = [];
    // The position of the character that is at the beginning of the token
    // that we are currently scanning in
    this.start = 0;
    // The position of the character that we are currently looking at for the
    // token being scanned in
    this.current = 0;
  }

  /**
   * Converts the entire source string into a series of tokens by repeatedly
   * calling `_scanToken` until the source string is consumed.
   * @returns {Array} A list of tokens
   * @public
   */
  scanTokens() {
    // As long as we are not at the end of the source string...
    while (!this._isAtEnd()) {
      // Set start to the current position and scan in a new token
      this.start = this.current;
      this._scanToken();
    }
    return this.tokens;
  }

  /**
   * Utility method to determine whether or not we have consumed the whole
   * source string.
   * @private
   */
  _isAtEnd() {
    return this.current >= this.source.length;
  }

  // ---------- TOKENIZING ----------

  /**
   * Takes the given token object and adds it to the token list
   * @param {Object} type - The type of the token
   * @param {String} [value] - Optionally override the default value
   * @private
   */
  _addToken(type, value) {
    this.tokens.push({
      type,
      text: value || this.source.substring(this.start, this.current)
    });
  }

  /**
   * Return the character at the position marked by `current` then increment
   * `current`.
   * @private
   */
  _advance() {
    return this.source[this.current++];
  }

  /**
   * Advances the scanner forward until it reaches a non-alphanumeric character
   */
  _advanceWhileAlphaNumeric() {
    while (!this._isAtEnd() && isAlphaNumeric(this._peek())) {
      this._advance();
    }
  }

  /**
   * Moves `this.current` back the provided number of positions (default 1).
   * @param {Number} num - The number of positions to backtrack
   */
  _backtrack(num = 1) {
    this.current -= num;
  }

  /**
   * Return the character at the position marked by `current` but do not
   * increment `current`.
   * @private
   */
  _peek() {
    if (this._isAtEnd()) {
      return null;
    } else {
      return this.source[this.current];
    }
  }

  /**
   * Return the character one position after `current` and do not change
   * `current`.
   * @private
   */
  _peekNext() {
    if (this.current + 1 >= this.source.length) {
      return null;
    } else {
      return this.source[this.current + 1];
    }
  }

  /**
   * Scan in a single token.
   * @private
   */
  _scanToken() {
    const char = this._advance();
    switch (char) {
      case '(':
        this._addToken(LEXEMES.LEFT_PAREN);
        break;
      case ')':
        this._addToken(LEXEMES.RIGHT_PAREN);
        break;
      case '"':
      case "'":
        // String needs to know what the opening delimiter is
        // Backtrack one and allow it to consume it again
        this._backtrack();
        this._string();
        break;
      case '-':
        this._addToken(LEXEMES.RANGE);
        break;
      case ',':
        this._addToken(LEXEMES.VALUE_SEPARATOR);
        break;
      case '&':
        if (this._peek() === '&') {
          this._advance();
          this._addToken(LEXEMES.AND);
        } else {
          throw new Error('Unexpected "&"');
        }
        break;
      case '|':
        if (this._peek() === '|') {
          this._advance();
          this._addToken(LEXEMES.OR);
        } else {
          throw new Error('Unexpected "|"');
        }
        break;
      case '=':
        this._addToken(LEXEMES.OPERATOR_EQ);
        break;
      case '!':
        if (this._peek() === '=') {
          this._advance();
          this._addToken(LEXEMES.OPERATOR_NOT_EQ);
        } else {
          // Advance until the next breakpoint and examine the string
          this._advanceWhileAlphaNumeric();
          const segment = this.source.substring(this.start, this.current);
          if (segment === '!exists') {
            this._addToken(LEXEMES.OPERATOR_NOT_EXISTS);
          } else {
            throw new Error('Unexpected "!"');
          }
        }
        break;
      case '<':
        if (this._peek() === '=') {
          this._advance();
          this._addToken(LEXEMES.OPERATOR_LTE);
        } else {
          this._addToken(LEXEMES.OPERATOR_LT);
        }
        break;
      case '>':
        if (this._peek() === '=') {
          this._advance();
          this._addToken(LEXEMES.OPERATOR_GTE);
        } else {
          this._addToken(LEXEMES.OPERATOR_GT);
        }
        break;
      case SEARCH_TERM_MARKER:
        this._textFilter();
        break;
      case '\t':
      case '\r':
      case '\n':
      case ' ':
        // Ignore whitespace
        break;
      default:
        if (isAlphaNumeric(char)) {
          // Default to reading in the whole token and then checking
          // to see what type it is
          this._alphaNumericToken();
        } else {
          // Unknown character
          throw new Error(`Unknown character encountered while scanning: "${char}"`);
        }
    }
  }

  // ---------- TOKEN TYPES ----------

  /**
   * Scan in a meta name.
   * @private
   */
  _alphaNumericToken() {
    // Start by advancing until the next logical stopping point for a token
    // and extracting it
    this._advanceWhileAlphaNumeric();
    const alphaString = this.source.substring(this.start, this.current);

    // Next, check to see if it matches any of these data types, with the more
    // restrictive types coming first

    if (isIPv4Address(alphaString)) {
      this._addToken(LEXEMES.IPV4_ADDRESS);
      return;
    }

    if (isIPv6Address(alphaString)) {
      this._addToken(LEXEMES.IPV6_ADDRESS);
      return;
    }

    if (isMACAddress(alphaString)) {
      this._addToken(LEXEMES.MAC_ADDRESS);
      return;
    }

    const isNumber = !isNaN(parseFloat(alphaString, 10));
    if (isNumber) {
      this._addToken(LEXEMES.NUMBER);
      return;
    }

    // If it's not any of those types, check to see if it matches any "keywords",
    // namely the operators that are "words" instead of special characters

    switch (alphaString) {
      case 'exists':
        this._addToken(LEXEMES.OPERATOR_EXISTS);
        break;
      case 'begins':
        this._addToken(LEXEMES.OPERATOR_BEGINS);
        break;
      case 'ends':
        this._addToken(LEXEMES.OPERATOR_ENDS);
        break;
      case 'contains':
        this._addToken(LEXEMES.OPERATOR_CONTAINS);
        break;
      case 'regex':
        this._addToken(LEXEMES.OPERATOR_REGEX);
        break;
      case 'length':
        this._addToken(LEXEMES.OPERATOR_LENGTH);
        break;
      // If it doesn't match any reserved words, add it as a meta key
      default:
        this._addToken(LEXEMES.META);
    }
  }

  /**
   * Parses a string literal.
   * @private
   */
  _string() {
    const delimiter = this._advance();

    // While we haven't reached the ending delimiter (or the end of the source)...
    while (this._peek() !== delimiter && !this._isAtEnd()) {
      // Advance past the next character and check if it's an escape character
      const char = this._advance();
      if (char === '\\') {
        // If it is an escape character, and it has something to escape (not isAtEnd), skip that next character.
        // This prevents an escaped delimiter from triggering the end of the string.
        if (!this._isAtEnd()) {
          this._advance();
        }
      }
    }

    if (this._isAtEnd()) {
      // We reached the end without seeing the closing delimiter
      throw new Error(`Unterminated string: ${this.source.substring(this.start)}`);
    }

    // Consume the ending delimiter
    this._advance();

    let string = this.source.substring(this.start + 1, this.current - 1);
    // Replace escaped delimiters with the non-escaped version
    // 3 layers of escapes here...JS string escapes, regex escapes, and now our escapes
    string = string
      .replace(new RegExp('\\\\\'', 'g'), '\'')
      .replace(new RegExp('\\\\"', 'g'), '"')
      .replace(new RegExp('\\\\\\\\', 'g'), '\\');

    this._addToken(LEXEMES.STRING, string);
  }

  /**
   * Scans in a number token.
   * @private
   */
  _number() {
    // Advance `current` until we reach a non-number character
    while (isDigit(this._peek())) {
      this._advance();
    }

    // Check for a decimal point and following number
    if (this._peek() === '.' && isDigit(this._peekNext())) {
      // Consume the decimal point
      this._advance();
      // Advance until the end of the fractional part
      while (isDigit(this._peek())) {
        this._advance();
      }
    }

    this._addToken(LEXEMES.NUMBER);
  }

  /**
   * Scans in a text filter
   * @private
   */
  _textFilter() {
    // First delimiter is already consumed
    while (this._peek() !== SEARCH_TERM_MARKER && !this._isAtEnd()) {
      this._advance();
    }

    if (this._isAtEnd()) {
      throw new Error('Reached end of input without seeing closing text filter delimiter');
    }

    // Consume ending delimiter
    this._advance();

    const filter = this.source.substring(this.start + 1, this.current - 1);
    this._addToken(LEXEMES.TEXT_FILTER, filter);
  }
}

export default Scanner;
