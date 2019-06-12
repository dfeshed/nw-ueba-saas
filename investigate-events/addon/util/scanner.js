import * as LEXEMES from 'investigate-events/constants/lexemes';
import { SEARCH_TERM_MARKER } from 'investigate-events/constants/pill';
import { relevantOperators } from 'investigate-events/util/possible-operators';
import { isDigit, isAlpha, isHex, isAlphaNumeric, isOperatorChar, isBetween } from 'investigate-events/util/scanning-helpers';

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
  constructor(source, metaDefinitions) {
    // Remove whitespace from ends of source string and store
    this.source = source.trim();
    // Store the definition of what meta is what
    this.metaDefinitions = metaDefinitions;
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

  // ---------- HELPERS ----------

  _previousMeta(operatorName) {
    const previousMeta = this.tokens[this.tokens.length - 1];
    if (!previousMeta || previousMeta.type !== LEXEMES.META) {
      throw new Error(`Expected a valid meta to preceed ${operatorName}, but did not find one.`);
    }
    return this.metaDefinitions.find((definition) => {
      return definition.metaName === previousMeta.text;
    });
  }

  /**
   * For some token types, such as the values (string, number, etc.) they
   * are called directly from other token types (such as operator). If this is
   * the case, check to make sure we haven't run out of characters and reset `this.start`.
   */
  _checkCanDoNextToken(previousName) {
    if (this._isAtEnd()) {
      throw new Error(`Expected to see ${previousName}, but hit end of input`);
    }
    this.start = this.current;
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
   * Advances the scanner forward until it reaches a character that is a solid
   * point to continue scanning from. (`(`, `)`, `&`, or `|`).
   */
  _advanceUntilNextGoodToken() {
    let advancing = true;
    while (!this._isAtEnd() && advancing) {
      const next = this._peek();
      // Move forward until we see one of our "breakpoints"
      if (next === '(' || next === ')' || next === '&' || next === '|' || next === SEARCH_TERM_MARKER) {
        advancing = false;
        // && and || have spaces around them, so backtrack one if this was the character we saw
        if (next === '&' || next === '|') {
          this._backtrack();
          if (this._peek() !== ' ') {
            // Validate that they had a space before them
            throw new Error('Logical operators must have spaces around them');
          }
        }
      } else {
        // Otherwise, advance until we see one of those characters or the end of the source.
        this._advance();
      }
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
      // Operators are the only lexeme that start (and end) with space.
      // When we see a space, it must be an operator.
      case ' ':
        this._operator();
        break;
      case SEARCH_TERM_MARKER:
        this._textFilter();
        break;
      case '\t':
      case '\r':
      case '\n':
        // Ignore other whitespace
        break;
      default:
        if (isAlpha(char)) {
          this._meta();
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
  _meta() {
    while (!this._isAtEnd() && isAlphaNumeric(this._peek())) {
      this._advance();
    }

    const metaName = this.source.substring(this.start, this.current);

    if (this._peek() !== ' ') {
      throw new Error(`Expected space after meta ${metaName}`);
    }

    const metaExists = this.metaDefinitions.some((definition) => {
      return definition.metaName === metaName;
    });

    if (!metaExists) {
      throw new Error(`"${metaName}" is not a valid meta`);
    }

    this._addToken(LEXEMES.META);
  }

  /**
   * Scans in an operator. Scans in both logical (`&&`, `||`) and query (`=`, `<`, `!=`, etc.) operators.
   * Expects and consumes a space character on both sides.
   * (Does not consume a space for `exists` or `!exists`).
   * @private
   */
  _operator() {
    // Preceding space alredy consumed
    // Now consume all the characters that make up the operator, then pull
    // it out and validate it.
    while (!this._isAtEnd() && (isAlpha(this._peek()) || isOperatorChar(this._peek()))) {
      this._advance();
    }
    const op = this.source.substring(this.start + 1, this.current);

    if (LEXEMES.OPERATORS.hasOwnProperty(op)) {
      // This is a QUERY operator, as referring to an operator inside a criteria
      // (meta operator value).
      const previousMeta = this._previousMeta(op);
      const allowedOperators = relevantOperators(previousMeta);
      const opObject = allowedOperators.find((operator) => {
        return operator.displayName === op;
      });
      if (!opObject) {
        // Valid operator, but not valid for this particular meta.
        const validOps = allowedOperators.map((o) => o.displayName).join(',');
        throw new Error(`Invalid operator for ${previousMeta.metaName}. Valid operator(s): ${validOps}`);
      }
      this._addToken(LEXEMES.OPERATOR, op);
      if (op !== 'exists' && op !== '!exists') {
        // Consume proceeding space only if operator is binary
        this._advance();
        // Scan in the next token based off of the type of the meta we have scanned in earlier
        const metaValueType = previousMeta.format;
        /**
         * Token types not covered and that need to be added:
         * - TimeT
         */
        this._checkCanDoNextToken(metaValueType);
        switch (metaValueType) {
          case 'Text':
            this._string();
            break;
          case 'UInt8':
          case 'UInt16':
          case 'UInt32':
          case 'UInt64':
          case 'UInt128':
          case 'Int8':
          case 'Int16':
          case 'Int32':
          case 'Int64':
          case 'Float32':
          case 'Float64':
            this._number();
            break;
          case 'IPv4':
            this._ipv4Address();
            break;
          case 'IPv6':
            this._ipv6Address();
            break;
          case 'MAC':
            this._macAddress();
            break;
          default:
            this._string();
            break;
        }
      }
    // These are LOGICAL operators, which separate criteria.
    } else if (op === '&&') {
      this._addToken(LEXEMES.AND, op);
      this._advance();
    } else if (op === '||') {
      this._addToken(LEXEMES.OR, op);
      this._advance();
    } else {
      // Unknown operator
      throw new Error(`Expected operator to follow space but got "${op}" instead`);
    }
  }

  /**
   * Parses a string literal.
   * @private
   */
  _string() {
    const delimiter = this._advance();
    if (delimiter !== "'" && delimiter !== '"') {
      throw new Error(`String following meta ${this.tokens[this.tokens.length - 2].text} must start with quote`);
    }

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
    string = string
      .replace(new RegExp('\\\'', 'g'), '\'')
      .replace(new RegExp('\\"', 'g'), '"')
      .replace(new RegExp('\\\\', 'g'), '\\');

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
   * Scans in an IPv4 address. Checks for validity and throws an error if
   * the value is not a valid IPv4 address.
   * @private
   */
  _ipv4Address() {
    while (isDigit(this._peek()) || this._peek() === '.' || this._peek() === '/') {
      this._advance();
    }

    const potentialAddress = this.source.substring(this.start, this.current);
    const octets = potentialAddress.split('.');
    const valid =
      octets.length === 4 &&
      octets.every((octet) => {
        const num = parseInt(octet, 10);
        return !isNaN(num) && isBetween(num, 0, 255);
      });

    if (!valid) {
      this._advanceUntilNextGoodToken();
      throw new Error(`Malformed IPv4 address: ${this.source.substring(this.start, this.current)}`);
    }
    this._addToken(LEXEMES.IPV4_ADDRESS);
  }

  /**
   * Scans in an IPv6 address. Does not check for validity.
   * @private
   */
  _ipv6Address() {
    while (!this._isAtEnd() && isHex(this._peek()) || this._peek() === ':' || this._peek() === '/') {
      this._advance();
    }

    // IPv6 validation is complicated and best left to a library
    // TODO: Validate IPv6 addresses
    this._addToken(LEXEMES.IPV6_ADDRESS);
  }

  /**
   * Scans in a MAC address.
   * @private
   */
  _macAddress() {
    while (!this._isAtEnd() && isHex(this._peek()) || this._peek() === ':') {
      this._advance();
    }

    const potentialAddress = this.source.substring(this.start, this.current);
    const bytes = potentialAddress.split(':');
    const valid = bytes.length === 6 &&
      bytes.every((byte) => {
        const num = parseInt(byte, 16);
        return isBetween(num, 0, 255);
      });
    if (!valid) {
      this._advanceUntilNextGoodToken();
      throw new Error(`Malformed MAC address: ${this.source.substring(this.start, this.current)}`);
    }
    this._addToken(LEXEMES.MAC_ADDRESS);
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
