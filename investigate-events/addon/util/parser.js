import { lookup } from 'ember-dependency-lookup';
import * as LEXEMES from 'investigate-events/constants/lexemes';
import * as GRAMMAR from 'investigate-events/constants/grammar';
import { relevantOperators } from 'investigate-events/util/possible-operators';

const VALUE_TYPES = [
  LEXEMES.NUMBER,
  LEXEMES.STRING,
  LEXEMES.IPV4_ADDRESS,
  LEXEMES.IPV6_ADDRESS,
  LEXEMES.MAC_ADDRESS
];

const VALUE_TYPE_MAP = {
  UInt8: LEXEMES.NUMBER,
  UInt16: LEXEMES.NUMBER,
  UInt32: LEXEMES.NUMBER,
  UInt64: LEXEMES.NUMBER,
  Float32: LEXEMES.NUMBER,
  Text: LEXEMES.STRING,
  IPv4: LEXEMES.IPV4_ADDRESS,
  IPv6: LEXEMES.IPV6_ADDRESS,
  MAC: LEXEMES.MAC_ADDRESS
};

/**
 * The Parser takes a list of tokens produced by the scanner and uses that
 * to produce meaningful data.
 */
class Parser {
  /**
   * Returns a new `Parser` based off the provided token list.
   * @param {Array} tokens - The array of tokens generated by the Scanner.
   * @param {Array} availableMeta - The language array defining available meta
   * @public
   */
  constructor(tokens, availableMeta) {
    // The array of tokens we parse into a structure
    this.tokens = tokens;
    // The list of available meta
    this.availableMeta = availableMeta;
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
    const typeString = types === LEXEMES.OPERATOR_TYPES ? 'OPERATOR' : types.join(',');
    if (this._isAtEnd()) {
      throw new Error(`Expected token of type ${typeString} but reached the end of the input`);
    }
    const nextTypeIsOk = types.some((type) => {
      return type === this._peek().type;
    });
    if (!nextTypeIsOk) {
      throw new Error(`Expected token of type ${typeString} but got type ${this._peek().type}`);
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
      children: [ this._criteriaOrGroupOrTextFilter() ]
    };
    while (this._nextTokenIsOfType([ LEXEMES.AND, LEXEMES.OR ])) {
      const operator = this._advance();
      const nextCriteriaOrGroup = this._criteriaOrGroupOrTextFilter();
      result.children.push(operator, nextCriteriaOrGroup);
    }
    return result;
  }

  /**
   * Returns a GRAMMAR.GROUP if it sees a left paren, otherwise returns
   * a GRAMMAR.CRITERIA.
   * @private
   */
  _criteriaOrGroupOrTextFilter() {
    if (this._nextTokenIsOfType([ LEXEMES.LEFT_PAREN ])) {
      return this._group();
    } else if (this._nextTokenIsOfType([ LEXEMES.TEXT_FILTER ])) {
      // Just return the text filter
      return this._advance();
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
    const operator = this._consume(LEXEMES.OPERATOR_TYPES);
    let metaConfig;
    // Check that this is a valid meta key
    if (this.availableMeta && this.availableMeta.length > 0) {
      metaConfig = this.availableMeta.find((m) => {
        return m.metaName === Parser.transformToString(meta);
      });
      if (!metaConfig) {
        throw new Error(`Meta "${Parser.transformToString(meta)}" not recognized`);
      } else if (metaConfig.isIndexedByNone && metaConfig.metaName !== 'sessionid') {
        // sessionid is a special meta key that should be used even though it is indexed by none
        throw new Error(`Meta "${Parser.transformToString(meta)}" not indexed`);
      } else {
        // Check that the operator applies to the meta
        const possibleOperators = relevantOperators(metaConfig);
        const operatorString = Parser.transformToString(operator);
        const operatorConfig = possibleOperators.find((o) => o.displayName === operatorString);
        if (!operatorConfig) {
          throw new Error(`Operator "${operatorString}" does not apply to meta "${Parser.transformToString(meta)}"`);
        }
      }
    }
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
      // Check to make sure all the values have the correct type
      const expectedType = VALUE_TYPE_MAP[metaConfig.format];
      const invalidRange = metaValueRanges.find((range) => {
        if (!metaConfig) {
          return false;
        }
        if (range.value) {
          return expectedType !== range.value.type;
        } else {
          return expectedType !== range.from.type || expectedType !== range.to.type;
        }
      });
      if (invalidRange) {
        const i18n = lookup('service:i18n');
        const validationError = i18n.t(`queryBuilder.validationMessages.${metaConfig.format.toLowerCase()}`);
        return {
          type: GRAMMAR.CRITERIA,
          meta,
          operator,
          valueRanges: metaValueRanges,
          isInvalid: true,
          validationError
        };
      } else {
        return {
          type: GRAMMAR.CRITERIA,
          meta,
          operator,
          valueRanges: metaValueRanges
        };
      }
    }
  }

  /**
   * Parses everything following the operator in a criteria. Makes one call to
   * `_metaValueRange` for everything separated by a comma (`LEXEMES.VALUE_SEPARATOR`).
   * @private
   */
  _metaValueRanges() {
    const valueRanges = [ this._metaValueRange() ];
    while (this._nextTokenIsOfType([ LEXEMES.VALUE_SEPARATOR ])) {
      // As long as the UI does not support shorthand, throw this error to get complex pills.
      // Once support is included, uncommend the rest of this block.
      throw new Error('Value shorthand is not yet supported');
      // // Consume the range separator
      // this._advance();
      // // Add the new range to the array
      // valueRanges.push(this._metaValueRange());
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
    const value = this._advance();
    if (this._nextTokenIsOfType([ LEXEMES.RANGE ])) {
      // As long as the UI does not support shorthand, throw this error to get complex pills.
      // Once support is included, uncommend the rest of this block.
      throw new Error('Value shorthand is not yet supported');
      // // Consume the range token first
      // this._consume([ LEXEMES.RANGE ]);
      // const to = this._consume([ value.type ]);
      // return {
      //   type: GRAMMAR.META_VALUE_RANGE,
      //   from: value,
      //   to
      // };
    } else {
      return {
        type: GRAMMAR.META_VALUE,
        value
      };
    }
  }

  /**
   * Turns any valid structure produced by the Parser back into
   * a query string.
   * @public
   * @static
   */
  static transformToString(tree) {
    // Alias this method so it doesn't create super long expressions inside
    const ts = Parser.transformToString;
    switch (tree.type) {
      case GRAMMAR.WHERE_CRITERIA:
        // Take each child, perform transformToString on them, then join together
        return tree.children.map(ts).join('');
      case GRAMMAR.CRITERIA:
        if (tree.valueRanges) {
          return `${ts(tree.meta)} ${ts(tree.operator)} ${tree.valueRanges.map(ts).join(',')}`;
        } else {
          return `${ts(tree.meta)} ${ts(tree.operator)}`;
        }
      case GRAMMAR.GROUP:
        return `(${ts(tree.group)})`;
      case GRAMMAR.META_VALUE:
        return ts(tree.value);
      case GRAMMAR.META_VALUE_RANGE:
        return `${ts(tree.from)}-${ts(tree.to)}`;
      case LEXEMES.AND:
      case LEXEMES.OR:
        return ` ${tree.text} `;
      case LEXEMES.STRING:
        return `'${tree.text}'`;
      default:
        return tree.text;
    }
  }
}

export default Parser;
