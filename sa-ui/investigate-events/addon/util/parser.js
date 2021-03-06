import { lookup } from 'ember-dependency-lookup';
import * as LEXEMES from 'investigate-events/constants/lexemes';
import * as GRAMMAR from 'investigate-events/constants/grammar';
import { relevantOperators } from 'investigate-events/util/possible-operators';

const VALUE_TYPE_MAP = {
  UInt8: LEXEMES.INTEGER,
  UInt16: LEXEMES.INTEGER,
  UInt32: LEXEMES.INTEGER,
  UInt64: LEXEMES.INTEGER,
  UInt128: LEXEMES.INTEGER,
  Int8: LEXEMES.INTEGER,
  Int16: LEXEMES.INTEGER,
  Int32: LEXEMES.INTEGER,
  Int64: LEXEMES.INTEGER,
  Float32: LEXEMES.FLOAT,
  Float64: LEXEMES.FLOAT,
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
  constructor(tokens, availableMeta, aliases) {
    // The array of tokens we parse into a structure
    this.tokens = tokens;
    // The list of available meta
    this.availableMeta = availableMeta;
    // The list of aliases which would otherwise break type enforcement
    this.aliases = aliases;
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
   * Returns the next *next* token without advancing `current`.
   * @private
   */
  _peekNext() {
    return this.tokens[this.current + 1];
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
   * Used to pass text that should become complex pills from the parser
   * to outside where the data is turned to pills.
   * @param {string} text The complex filter text
   * @private
   */
  _createComplexString(text) {
    return {
      type: GRAMMAR.COMPLEX_FILTER,
      text
    };
  }

  /**
   * Used like _createComplexString, but will attempt the same non-terminal again.
   * Used to error but still try again to find a criteria.
   * @param {string} text The complex filter text
   */
  _complexAndTryAgain(text) {
    return {
      type: GRAMMAR.COMPLEX_FILTER,
      text,
      tryAgain: true
    };
  }

  /**
   * Returns an object that contains the meta and the operator, if they pass
   * validation. Otherwise, return null.
   * @param {Object} meta The meta config object
   * @param {Object} operator The operator object
   * @private
   */
  _validateMetaAndOperator(meta, operator) {
    if (this.availableMeta && this.availableMeta.length > 0) {
      const metaString = Parser.transformToString(meta);
      const metaConfig = this.availableMeta.find((m) => {
        return m.metaName === metaString;
      });
      if (!metaConfig) {
        // Meta not recognized
        return null;
      // sessionid is a special meta key that should be used even though it is indexed by none
      } else if (metaConfig.isIndexedByNone && metaConfig.metaName !== 'sessionid') {
        // Meta not indexed
        return null;
      } else {
        // Check that the operator applies to the meta
        const possibleOperators = relevantOperators(metaConfig);
        const operatorString = Parser.transformToString(operator);
        const operatorConfig = possibleOperators.find((o) => o.displayName === operatorString);
        if (!operatorConfig) {
          // Operator does not apply to meta
          return null;
        }
        return { metaConfig, operatorConfig };
      }
    }
    // If no availableMeta, return empty object
    return {};
  }

  /**
   * Returns true if the meta value(s) is/are valid, false otherwise. Can also
   * return a validationError object. Does a check of the aliases dictionary to
   * see if the cause of an invalid type is use of an alias.
   * @param {GRAMMAR.META_VALUE_RANGE} range The value range object
   * @param {String} expectedType The expected type of the value range
   * @private
   */
  _isValueTypeInvalid(range, expectedType, meta) {
    const i18n = lookup('service:i18n');
    const { aliases } = this;
    const valuesToCheck = range.value ? [ range.value ] : [ range.from, range.to ];
    let result = null;
    const isInvalid = valuesToCheck.some((value, index) => {
      if (value.type !== expectedType) {
        if (aliases[meta.text] && Object.values(aliases[meta.text]).find((alias) => {
          return alias.toLowerCase() === value.text.toLowerCase();
        })) {
          return false;
        } else if (expectedType === LEXEMES.FLOAT && value.type === LEXEMES.INTEGER) {
          // Numbers without decimal points are still valid floats
          return false;
        } else if (range.from && ((range.from.text.toLowerCase() === 'l' && index === 0) ||
          (range.to.text.toLowerCase() === 'u' && index === 1))) {
          // If using range shorthand for upper or lower bound, that's okay
          return false;
        } else {
          // Type mismatch but no aliases, invalid
          return true;
        }
      }
      // No type mismatch
      if (expectedType === LEXEMES.IPV4_ADDRESS || expectedType === LEXEMES.IPV6_ADDRESS) {
        if (value.cidr === 'empty') {
          result = i18n.t('queryBuilder.validationMessages.cidrBad');
          return true;
        } else if (isNaN(value.cidr)) {
          result = i18n.t('queryBuilder.validationMessages.cidrBad');
          return true;
        } else if (expectedType === LEXEMES.IPV4_ADDRESS && (value.cidr < 0 || value.cidr > 32)) {
          result = i18n.t('queryBuilder.validationMessages.cidrIpv4OutOfRange');
          return true;
        } else if (expectedType === LEXEMES.IPV6_ADDRESS && (value.cidr < 0 || value.cidr > 128)) {
          result = i18n.t('queryBuilder.validationMessages.cidrIpv6OutOfRange');
          return true;
        }
      } else if (expectedType === LEXEMES.INTEGER || expectedType === LEXEMES.FLOAT) {
        if (valuesToCheck.length === 2 && index === 1) {
          // This is a range, as opposed to a single value. spaces around
          // characters are handled by the Scanner, negative numbers are
          // handled by _metaValue, this checks that the first number is less
          // than the second.
          if (parseFloat(valuesToCheck[0].text, 10) >= parseFloat(value.text, 10)) {
            result = i18n.t('queryBuilder.validationMessages.badRange');
            return true;
          }
        }
      }
      return false;
    });
    return result || isInvalid;
  }

  /**
   * Returns true if the singular value or both ends of a range are >= 1
   * @param {GRAMMAR.META_VALUE_RANGE} range The value range object
   */
  _isValuePositive(range) {
    if (range.value) {
      return range.value.text[0] !== '-' && parseInt(range.value.text, 10) > 0;
    } else {
      return range.from.text[0] !== '-' || range.to.text[0] !== '-' &&
        parseInt(range.from.text, 10) > 0 && parseInt(range.to.text, 10) > 0;
    }
  }

  /**
   * Removes operator tokens from the beginning and end of the token string.
   * e.g. AND medium = 1 OR becomes medium = 1
   * Also removes more than one consecutive operator
   * medium = 1 AND OR medium = 2 becomes medium = 1 AND medium = 2
   * @private
   */
  _preprocess() {
    let firstNonOperatorSeen = false;
    let lastItemWasOperator = false;
    this.tokens = this.tokens.filter((item) => {
      if (item.type === LEXEMES.AND || item.type === LEXEMES.OR) {
        if (!firstNonOperatorSeen) {
          // If we haven't seen any non-operator yet, remove them from the beginning
          // of the token string
          return false;
        } else if (lastItemWasOperator) {
          // If the last thing we saw was an operator, remove any operators directly following it
          return false;
        } else {
          // Otherwise, set the flag and this operator is good
          lastItemWasOperator = true;
          return true;
        }
      } else {
        firstNonOperatorSeen = true;
        lastItemWasOperator = false;
        return true;
      }
    });
    if (this.tokens.lastItem?.type === LEXEMES.AND || this.tokens.lastItem?.type === LEXEMES.OR) {
      this.tokens.splice(this.tokens.lastIndex, 1);
    }
  }

  /**
   * Returns true if the provided GROUP (or NOT) is empty (a set of empty parens).
   * @param {*} tree
   */
  _isEmptyGroup(tree) {
    return (tree?.type === GRAMMAR.GROUP) && tree.group.children.length === 0;
  }

  /**
   * Takes an item produced by the parser and returns a text filter if one
   * exists inside parentheses. Otherwise, returns null. MUTATES the
   * structure passed in to remove the text filter from where it originally was
   * @param {*} tree
   */
  _moveTextFilterOutsideGroup(tree, isInsideGroup = false) {
    let result = null;
    switch (tree.type) {
      // These two cases are parentheses, return whatever might be inside them
      // while setting the flag to true
      case GRAMMAR.GROUP:
        return this._moveTextFilterOutsideGroup(tree.group, true);
      // Iterate over each item, return the first text filter, but remove ALL
      // text filters (if there are more than one) if isInsideGroup is true.
      case GRAMMAR.WHERE_CRITERIA:
        for (let i = 0; i < tree.children.length; i++) {
          const child = tree.children[i];
          const prev = tree.children[i - 1];
          const next = tree.children[i + 1];
          if (child.type === LEXEMES.TEXT_FILTER && isInsideGroup) {
            // Remove the text filter and its operator (if one exists)
            // Either remove the operator at position (i - 1) or (i + 1), and
            // also decrement i if we remove the one in front so that i still
            // points to our text filter
            if (prev?.type === LEXEMES.AND || prev?.type === LEXEMES.OR) {
              tree.children.splice(--i, 1);
            } else if (next?.type === LEXEMES.AND || next?.type === LEXEMES.OR) {
              tree.children.splice(i + 1, 1);
            }
            const [ splice ] = tree.children.splice(i--, 1);
            result = result || splice;
          } else {
            const inside = this._moveTextFilterOutsideGroup(child, isInsideGroup);
            if (this._isEmptyGroup(child)) {
              // Remove the empty group and its operator from the list of children.
              if (prev?.type === LEXEMES.AND || prev?.type === LEXEMES.OR) {
                tree.children.splice(--i, 1);
              } else if (next?.type === LEXEMES.AND || next?.type === LEXEMES.OR) {
                tree.children.splice(i + 1, 1);
              }
              tree.children.splice(i--, 1);
            }
            result = result || inside;
          }
        }
        return result;
      default:
        return null;
    }
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
    // Remove operator tokens at beginning and end of token string
    this._preprocess();
    const result = this._whereCriteria();
    if (!this._isAtEnd()) {
      // If we come out of whereCriteria and more tokens are still left, they
      // should not be there.
      const unexpectedTokensString = this.tokens
        .slice(this.current)
        .map((t) => t.text)
        .join(' ');
      // Remaining tokens become one complex pill
      result.children.push({ type: LEXEMES.AND, text: 'AND' }, this._createComplexString(unexpectedTokensString));
    }
    const textFilter = this._moveTextFilterOutsideGroup(result);
    if (textFilter) {
      // A text filter was used inside parens. This is not allowed, pull it out
      // to the first pill and add an AND.
      result.children = [ textFilter, { type: LEXEMES.AND, text: 'AND' }, ...result.children ];
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
      children: []
    };
    let child = this._criteriaOrGroupOrTextFilterOrNot();
    while ((child.type === GRAMMAR.COMPLEX_FILTER && child.tryAgain) || child.type === GRAMMAR.NOT) {
      // If we encounter a NOT, don't look for an AND/OR just yet, find another pill-like object first
      if (child.type === GRAMMAR.NOT) {
        result.children.push(child);
      } else if (child.text !== '') {
        result.children.push(child, { type: LEXEMES.AND, text: 'AND' });
      }
      child = this._criteriaOrGroupOrTextFilterOrNot();
    }
    result.children.push(child);
    while (this._nextTokenIsOfType([ LEXEMES.AND, LEXEMES.OR ])) {
      const operator = this._advance();
      let next = this._criteriaOrGroupOrTextFilterOrNot();
      result.children.push(operator);
      while ((next.type === GRAMMAR.COMPLEX_FILTER && next.tryAgain) || next.type === GRAMMAR.NOT) {
        // If we encounter a NOT, don't look for an AND/OR just yet, find another pill-like object first
        if (next.type === GRAMMAR.NOT) {
          result.children.push(next);
        } else if (next.text !== '') {
          result.children.push(next, { type: LEXEMES.AND, text: 'AND' });
        }
        next = this._criteriaOrGroupOrTextFilterOrNot();
      }
      if (next.type === GRAMMAR.COMPLEX_FILTER && next.text === '') {
        // The empty complex pill signifies that we reached the end of the input while
        // still expecting a meta. In this particular case, we read an AND or OR and then didn't
        // see anything after that. The operator has already been pushed, do nothing.
      } else if (next.type === LEXEMES.TEXT_FILTER) {
        // Operators before a text filter must be an AND. Modify pushed operator.
        result.children[result.children.lastIndex] = { type: LEXEMES.AND, text: 'AND' };
        result.children.push(next);
      } else {
        result.children.push(next);
      }
    }

    // Change any OR to AND before or after a text filter
    result.children = result.children.map((item, idx, arr) => {
      const prev = arr[idx - 1];
      const next = arr[idx + 1];
      if (item.type === LEXEMES.OR && (prev?.type === LEXEMES.TEXT_FILTER || next?.type === LEXEMES.TEXT_FILTER)) {
        return { type: LEXEMES.AND, text: 'AND' };
      }
      return item;
    });

    return result;
  }

  /**
   * Returns a GRAMMAR.GROUP if it sees a left paren, otherwise returns
   * a GRAMMAR.CRITERIA.
   * @private
   */
  _criteriaOrGroupOrTextFilterOrNot() {
    if (this._nextTokenIsOfType([ LEXEMES.LEFT_PAREN ])) {
      return this._group();
    } else if (this._nextTokenIsOfType([ LEXEMES.NOT ])) {
      return this._not();
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
    if (inside.type === GRAMMAR.COMPLEX_FILTER && inside.text === '') {
      // This is an end-of-input error. Make the left paren a complex pill.
      return this._createComplexString('(');
    }
    if (!this._nextTokenIsOfType([ LEXEMES.RIGHT_PAREN ])) {
      return this._createComplexString(`(${Parser.transformToString(inside)}`);
    } else {
      this._consume([ LEXEMES.RIGHT_PAREN ]);
    }
    return {
      type: GRAMMAR.GROUP,
      group: inside
    };
  }

  /**
   * Used to negate things. Similar to a pill in that it's used by itself.
   * Does not contain anything, just the word NOT.
   * @private
   */
  _not() {
    this._consume([ LEXEMES.NOT ]);
    return {
      type: GRAMMAR.NOT
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
    let returnComplex = false;
    if (!this._nextTokenIsOfType([ LEXEMES.META ])) {
      /**
       * Error, we are looking at a token that is not a meta but we are expecting a meta.
       * Could be:
       * - At the very beginning of a query
       * - After && or ||
       * - After (
       */
      if (this._nextTokenIsOfType(LEXEMES.OPERATOR_TYPES) && LEXEMES.VALUE_TYPES.includes(this._peekNext()?.type)) {
        // If we are expecting a meta, but we see an operator and a value, they probably forgot to include the meta.
        // Group these two tokens together in one complex pill.
        return this._createComplexString(`${this._advance().text} ${this._advance().text}`);
      } else if (!this._isAtEnd()) {
        const badToken = this._advance();
        if (this._nextTokenIsOfType([ LEXEMES.META ])) {
          return this._complexAndTryAgain(badToken.text);
        } else {
          return this._createComplexString(badToken.text);
        }
      } else {
        // If we're at the end, return an empty complex pill. `_whereCriteria` will see this
        // and do something with it depending on its context.
        return this._createComplexString('');
      }
    }
    const meta = this._consume([ LEXEMES.META ]);
    if (!this._nextTokenIsOfType(LEXEMES.OPERATOR_TYPES)) {
      // We saw a meta, but no operator followed it.
      if (this._nextTokenIsOfType([ LEXEMES.META ])) {
        if (LEXEMES.VALUE_TYPES.includes(this._peekNext()?.type)) {
          // meta meta value
          return this._createComplexString(`${meta.text} ${this._advance().text} ${this._advance().text}`);
        } else if (LEXEMES.OPERATOR_TYPES.includes(this._peekNext()?.type)) {
          // meta meta operator
          // Cut this out and use the next meta
          return this._complexAndTryAgain(meta.text);
        } else {
          return this._createComplexString(`${meta.text} ${this._advance().text}`);
        }
      } else if (this._nextTokenIsOfType([ LEXEMES.RIGHT_PAREN, LEXEMES.AND, LEXEMES.OR ])) {
        return this._createComplexString(meta.text);
      } else if (!this._isAtEnd()) {
        const badToken = this._advance();
        if (this._nextTokenIsOfType([ LEXEMES.META ])) {
          return this._complexAndTryAgain(`${meta.text} ${badToken.text}`);
        } else {
          return this._createComplexString(`${meta.text} ${badToken.text}`);
        }
      } else {
        return this._createComplexString(meta.text);
      }
    }
    const operator = this._consume(LEXEMES.OPERATOR_TYPES);
    // Check that this is a valid meta key & operator
    const configs = this._validateMetaAndOperator(meta, operator);
    if (configs === null) {
      // Error somewhere in validation, make complex, but wait until we know
      // what kind of value we'll be returning.
      returnComplex = true;
    }
    const { metaConfig, operatorConfig } = configs || {};

    // Unary operators (exists & !exists) do not have values, so push them
    // without a `valueRanges` property
    if (operator.text === 'exists' || operator.text === '!exists') {
      if (this._nextTokenIsOfType(LEXEMES.VALUE_TYPES)) {
        // Can't have values after unary operators. Pull them out of the queue
        // and put them in a complex pill
        const metaValueRanges = this._metaValueRanges();
        const { valueRanges } = metaValueRanges;
        const criteria = {
          type: GRAMMAR.CRITERIA,
          meta,
          operator,
          valueRanges
        };
        return this._createComplexString(Parser.transformToString(criteria));
      }
      const criteria = { type: GRAMMAR.CRITERIA, meta, operator };
      // If we need to return a complex pill, take what we read in and transform it back to a string.
      return returnComplex ? this._createComplexString(Parser.transformToString(criteria)) : criteria;
    } else {
      let expectedType;
      let hasInvalidValue = false;
      // Pull in the meta value ranges, which can be anything from a single value to multiple ranges
      // of values separated by commas.
      const metaValueRanges = this._metaValueRanges();
      if (metaValueRanges.type && metaValueRanges.type === GRAMMAR.COMPLEX_FILTER) {
        // If this is a complex filter, something went wrong, pass it up.
        // First, append the text of the meta and operator.
        metaValueRanges.text = `${Parser.transformToString(meta)} ${Parser.transformToString(operator)} ${metaValueRanges.text}`;
        return metaValueRanges;
      }
      // Pull out the actual values and any possible validation error. If error,
      // it will get passed along when we return but set hasInvalidValue to make
      // sure we return with the validation error.
      let { validationError, valueRanges } = metaValueRanges;
      if (validationError) {
        hasInvalidValue = true;
      }
      // Make sure all aliases have the correct capitalization
      const { aliases } = this;
      if (aliases?.[meta.text]) {
        const list = aliases[meta.text];
        valueRanges = valueRanges.map((range) => {
          if (range === null) {
            // Happens when multiple commas in a row are entered
            return range;
          }
          if (range.value) {
            const alias = Object.values(list).find((short) => {
              return short.toLowerCase() === range.value.text.toLowerCase();
            });
            return {
              ...range,
              value: alias ? { ...range.value, text: alias } : range.value
            };
          } else {
            const aliasTo = Object.values(list).find((short) => {
              return short.toLowerCase() === range.to.text.toLowerCase();
            });
            const aliasFrom = Object.values(list).find((short) => {
              return short.toLowerCase() === range.from.text.toLowerCase();
            });
            return {
              ...range,
              to: aliasTo ? { ...range.to, text: aliasTo } : range.to,
              from: aliasFrom ? { ...range.from, text: aliasFrom } : range.from
            };
          }
        });
      }
      // Map the type of the meta (e.g. UInt8) to the Scanner's types (e.g. LEXEMES.INTEGER)
      if (metaConfig) {
        expectedType = VALUE_TYPE_MAP[metaConfig.format];
      }
      // The length operator is the only operator that requires a value of a type
      // different than what is associated with the meta key
      let isLengthOperator = false;
      if (operatorConfig && operatorConfig.displayName === 'length') {
        isLengthOperator = true;
        expectedType = LEXEMES.INTEGER;
      }
      // Find any value that is the incorrect type
      hasInvalidValue = hasInvalidValue || valueRanges.some((range) => {
        if (!expectedType) {
          return false;
        }
        // This function checks many things about the value including a lot of
        // exceptions and other special rules.
        let isInvalid = this._isValueTypeInvalid(range, expectedType, meta);
        if (typeof isInvalid !== 'boolean') {
          // isValueTypeInvalid will return a validation error if the value is
          // invalid, save it and set isInvalid to true.
          validationError = validationError || isInvalid;
          isInvalid = true;
        }
        // If using length & types were valid, check for negative or zero
        if (isLengthOperator && !isInvalid) {
          isInvalid = !this._isValuePositive(range);
        }
        return isInvalid;
      });
      const i18n = lookup('service:i18n');
      if (isLengthOperator && hasInvalidValue) {
        // If the operator is `length` and we saw an invalid value, it was because
        // we did not see an integer, but the normal validation message would say it
        // was expecting a string, so change that.
        validationError = i18n.t('queryBuilder.validationMessages.length');
      }
      if (hasInvalidValue) {
        // If the hasInvalidValue flag is set, use either the validation error that was returned earlier from something
        // else, or in its absence use the validation error for that value type.
        validationError = validationError || i18n.t(`queryBuilder.validationMessages.${metaConfig.format.toLowerCase()}`);
        const criteria = {
          type: GRAMMAR.CRITERIA,
          meta,
          operator,
          valueRanges,
          isInvalid: true,
          validationError
        };
        return returnComplex ? this._createComplexString(Parser.transformToString(criteria)) : criteria;
      } else {
        const criteria = {
          type: GRAMMAR.CRITERIA,
          meta,
          operator,
          valueRanges
        };
        return returnComplex ? this._createComplexString(Parser.transformToString(criteria)) : criteria;
      }
    }
  }

  /**
   * Parses everything following the operator in a criteria. Makes one call to
   * `_metaValueRange` for everything separated by a comma (`LEXEMES.VALUE_SEPARATOR`).
   * @private
   */
  _metaValueRanges() {
    let valueRanges = [];
    const metaValueRange = this._metaValueRange();
    if (metaValueRange.type && metaValueRange.type === GRAMMAR.COMPLEX_FILTER) {
      // If this is a complex filter, something went wrong, pass it up.
      return metaValueRange;
    }
    const { range } = metaValueRange;
    let { validationError } = metaValueRange;
    valueRanges.push(range);
    while (this._nextTokenIsOfType([ LEXEMES.VALUE_SEPARATOR ])) {
      // Consume the range separator
      this._advance();
      // Check to make sure a value is next
      if (!this._nextTokenIsOfType(LEXEMES.VALUE_TYPES)) {
        // If it's another comma, set the validation error but continue
        if (this._nextTokenIsOfType([ LEXEMES.VALUE_SEPARATOR ])) {
          const i18n = lookup('service:i18n');
          validationError = i18n.t('queryBuilder.validationMessages.extraComma');
          // Push a null entry to get the extra comma when this is turned back to a string
          valueRanges.push(null);
          // Skip back to the beginning of the loop
          continue;
        } else {
          // If it's not another comma, abort. If the token after is && or ||, include
          // the next token in the complex pill we're about to create.
          const valueString = valueRanges.map(Parser.transformToString).join(',');
          if (this._peekNext() && this._peekNext().type === LEXEMES.AND || this._peekNext().type === LEXEMES.OR) {
            const badToken = this._advance();
            return this._createComplexString(`${valueString},${badToken.text}`);
          } else {
            return this._createComplexString(`${valueString},`);
          }
        }
      }
      // Add the new range to the array
      const { range: nextRange, validationError: nextValidationError } = this._metaValueRange();
      validationError = validationError || nextValidationError;
      valueRanges.push(nextRange);
    }
    // Filter out empty strings
    valueRanges = valueRanges.filter((range) => {
      if (range?.value?.text === '') {
        return false;
      }
      return true;
    });
    return {
      valueRanges,
      validationError
    };
  }

  /**
   * Parses a single value or a single range. If a `-` is present (`LEXEMES.HYPHEN`),
   * return a GRAMMAR.META_VALUE_RANGE with a `from` and `to` field. Otherwise, return
   * a GRAMMAR.META_VALUE with a `value` field.
   * @private
   */
  _metaValueRange() {
    const metaValue = this._metaValue();
    const { value, validationError } = metaValue;
    if (this._nextTokenIsOfType([ LEXEMES.HYPHEN ])) {
      // Consume the range token first
      this._consume([ LEXEMES.HYPHEN ]);
      if (!this._nextTokenIsOfType(LEXEMES.VALUE_TYPES) && (this._peek() && this._peek().text.toLowerCase()) !== 'u') {
        // If there is not a value token next, abort
        if (this._peekNext() && (this._peekNext().type === LEXEMES.AND || this._peekNext().type === LEXEMES.OR)) {
          // If the bad token has an operator after it, include the bad token
          // in the complex pill. Otherwise, leave it out.
          const badToken = this._advance();
          return this._createComplexString(`${Parser.transformToString(value)}-${badToken.text}`);
        } else {
          return this._createComplexString(`${Parser.transformToString(value)}-`);
        }
      }
      const { value: to, validationError: validationError2 } = this._metaValue();
      return {
        range: {
          type: GRAMMAR.META_VALUE_RANGE,
          from: value,
          to
        },
        validationError: validationError || validationError2
      };
    } else {
      return {
        range: {
          type: GRAMMAR.META_VALUE,
          value
        },
        validationError
      };
    }
  }

  /**
   * Parses a single value. Can combine a HYPHEN with an INTEGER or FLOAT to
   * produce a negative number.
   * @private
   */
  _metaValue() {
    let validationError = null;
    let value;
    if (this._nextTokenIsOfType([ LEXEMES.HYPHEN ])) {
      // Hyphen before value is a negative number. Consume hyphen.
      this._advance();
      // Negative numbers not allowed
      const i18n = lookup('service:i18n');
      validationError = i18n.t('queryBuilder.validationMessages.negative');
      // Capture value
      value = this._advance();
      // Make value negative
      value.text = `-${value.text}`;
    } else if (this._nextTokenIsOfType([ LEXEMES.IPV4_ADDRESS, LEXEMES.IPV6_ADDRESS ])) {
      value = this._advance();
      // Negative number, amend the token
      if (this._nextTokenIsOfType([ LEXEMES.HYPHEN ]) && this._peekNext() && this._peekNext().type === LEXEMES.INTEGER) {
        this._advance(); // Consume hyphen
        // Negative numbers not allowed
        const i18n = lookup('service:i18n');
        validationError = i18n.t('queryBuilder.validationMessages.negative');
        const num = this._advance();
        value.cidr = parseInt(`-${num.text}`, 10);
        value.text += `${value.cidr}`;
      }
    } else {
      value = this._advance();
    }
    return { value, validationError };
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
    if (tree === null) {
      // Sometimes, valueRanges contains a null entry in order to display more
      // than one comma in a row. Don't trip on this, just return empty string.
      return '';
    }
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
      case GRAMMAR.NOT:
        return 'NOT';
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
