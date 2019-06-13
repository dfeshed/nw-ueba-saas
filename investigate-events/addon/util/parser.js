import * as LEXEMES from 'investigate-events/constants/lexemes';
import * as GRAMMAR from 'investigate-events/constants/grammar';

const VALUE_TYPES = [
  LEXEMES.NUMBER,
  LEXEMES.STRING,
  LEXEMES.IPV4_ADDRESS,
  LEXEMES.IPV6_ADDRESS,
  LEXEMES.MAC_ADDRESS
];

/**
 * The Parser takes a list of tokens produced by the scanner and uses that
 * to produce meaningful data.
 */
class Parser {
  /**
   * Returns a new `Parser` based off the provided token list.
   * @param {Array} tokens - The array of tokens generated by the Scanner.
   * @public
   */
  constructor(tokens) {
    // The array of tokens we parse into a structure
    this.tokens = tokens;
    // Indicates the next token to examine
    this.current = 0;
  }

  /**
   * Use when you expect a certain token or one of a few token typess to be next.
   * If successful, returns that token. Otherwise, throws an error.
   * @param {Array} types - An array containing one or more typess expected next
   * @private
   */
  _consume(types) {
    if (this._isAtEnd()) {
      throw new Error(`Expected token of type ${types.join(',')} but reached the end of the input`);
    }
    const nextTypeIsOk = types.some((type) => {
      return type === this._peek().type;
    });
    if (!nextTypeIsOk) {
      throw new Error(`Expected token of type ${types.join(',')} but got type ${this._peek().type}`);
    }
    return this._advance();
  }

  /**
   * Returns true if any one of the passed in types matches the next token
   * @param {Array} types - An array of lexeme types to check for.
   * @private
   */
  _nextTokenIsOfType(types) {
    if (this._isAtEnd()) {
      return false;
    }
    return types.some((type) => this._peek().type === type);
  }

  /**
   * Returns the next token without advancing `current`.
   * @private
   */
  _peek() {
    return this.tokens[this.current];
  }

  /**
   * Returns the next token and advances `current`.
   * @private
   */
  _advance() {
    return this.tokens[this.current++];
  }

  /**
   * Returns `true` if all tokens have been consumed.
   * @private
   */
  _isAtEnd() {
    return this.current >= this.tokens.length;
  }

  /**
   * Parses the list of tokens into a syntax tree.
   * @public
   */
  parse() {
    return this._whereClause();
  }

  // ---------- Nonterminals & Terminals ----------

  /**
   * The top level nonterminal.
   * @private
   */
  _whereClause() {
    const result = this._whereCriteria();
    if (!this._isAtEnd()) {
      // If we come out of whereCriteria and more tokens are still left, they
      // should not be there.
      const hasMultipleUnexpectedTokens = this.current < this.tokens.length - 1;
      const unexpectedTokensString = this.tokens
        .slice(this.current)
        .map((t) => `${t.type}(${t.text})`)
        .join(' ');
      throw new Error(`Unexpected token${hasMultipleUnexpectedTokens ? 's' : ''}: ${unexpectedTokensString}`);
    }
    return result;
  }

  /**
   * Returns a GRAMMAR.WHERE_CRITERIA, which has a property `children` that
   * contains one guaranteed criteria or group, followed optionally by 0 or more
   * groups of logical operators with additional groups or criteria.
   * e.g. (criteriaOrGroup, [logicalOperator, criteriaOrGroup]*) where the group
   * of logicalOperator followed by criteriaOrGroup is repeated 0 or more times.
   * The criteria or group will haves types of GRAMMAR.CRITERIA or GRAMMAR.GROUP
   * respectively, while the logical operators will be of type LEXEMES.OR or LEXEMES.AND.
   * @private
   */
  _whereCriteria() {
    const result = {
      type: GRAMMAR.WHERE_CRITERIA,
      children: [ this._criteriaOrGroup() ]
    };
    while (this._nextTokenIsOfType([ LEXEMES.AND, LEXEMES.OR ])) {
      const operator = this._advance();
      const nextCriteriaOrGroup = this._criteriaOrGroup();
      result.children.push(operator, nextCriteriaOrGroup);
    }
    return result;
  }

  /**
   * Returns a GRAMMAR.GROUP if it sees a left paren, otherwise returns
   * a GRAMMAR.CRITERIA.
   * @private
   */
  _criteriaOrGroup() {
    if (this._nextTokenIsOfType([ LEXEMES.LEFT_PAREN ])) {
      return this._group();
    } else {
      return this._criteria();
    }
  }

  /**
   * A group is anything in parenthesis, which is then parsed
   * recursively as if it were its own top level element and then
   * placed inside a group.
   * @private
   */
  _group() {
    this._consume([ LEXEMES.LEFT_PAREN ]);
    const inside = this._whereCriteria();
    this._consume([ LEXEMES.RIGHT_PAREN ]);
    return {
      type: GRAMMAR.GROUP,
      group: inside
    };
  }

  /**
   * A criteria is the common "meta operator value" form. If the operator is
   * unary (exists & !exists), no value is present on the returned object.
   * Otherwise, the object contains a meta, operator, and valueRanges.
   * valueRanges is an array of values, where each value can either be
   * a range or a single value. i.e. "medium = 12,50-60,88" would be interpreted
   * as a criteria where the meta is "medium", the operator is "=", and valueRanges
   * is an array with 3 items. The first item is GRAMMAR.META_VALUE with the `value`
   * property of "12", the second is a GRAMMAR.META_VALUE_RANGE with the properties
   * `from: 50` and `to: 60`, and the third item would be another GRAMMAR.META_VALUE
   * with a `value` of 88.
   * @private
   */
  _criteria() {
    const meta = this._consume([ LEXEMES.META ]);
    const operator = this._consume([ LEXEMES.OPERATOR ]);
    // Unary operators (exists & !exists) do not have values, so push them
    // without a `valueRanges` property
    if (operator.text === 'exists' || operator.text === '!exists') {
      if (this._nextTokenIsOfType(VALUE_TYPES)) {
        throw new Error(`Invalid value ${this._advance().text} after unary operator ${operator.text}`);
      }
      return {
        type: GRAMMAR.CRITERIA,
        meta,
        operator
      };
    } else {
      const metaValueRanges = this._metaValueRanges();
      return {
        type: GRAMMAR.CRITERIA,
        meta,
        operator,
        valueRanges: metaValueRanges
      };
    }
  }

  /**
   * Parses everything following the operator in a criteria. Makes one call to
   * `_metaValueRange` for everything separated by a comma (`LEXEMES.RANGE_SEPARATOR`).
   * @private
   */
  _metaValueRanges() {
    const valueRanges = [ this._metaValueRange() ];
    while (this._nextTokenIsOfType([ LEXEMES.RANGE_SEPARATOR ])) {
      // Consume the range separator
      this._advance();
      // Add the new range to the array
      valueRanges.ranges.push(this._metaValueRange());
    }
    return valueRanges;
  }

  /**
   * Parses a single value or a single range. If a `-` is present (`LEXEMES.RANGE`),
   * return a GRAMMAR.META_VALUE_RANGE with a `from` and `to` field. Otherwise, return
   * a GRAMMAR.META_VALUE with a `value` field.
   * @private
   */
  _metaValueRange() {
    const value = this._consume(VALUE_TYPES);
    if (this._nextTokenIsOfType([ LEXEMES.RANGE ])) {
      const to = this._consume([ value.type ]);
      return {
        type: GRAMMAR.META_VALUE_RANGE,
        from: value,
        to
      };
    } else {
      return {
        type: GRAMMAR.META_VALUE,
        value
      };
    }
  }
}

export default Parser;
