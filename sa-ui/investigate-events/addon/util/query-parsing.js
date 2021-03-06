import { isBlank } from '@ember/utils';
import _ from 'lodash';

import {
  mergeFilterStrings,
  removeEmptyFilters
} from 'investigate-shared/actions/api/events/utils';
import { relevantOperators } from 'investigate-events/util/possible-operators';
import * as LEXEMES from 'investigate-events/constants/lexemes';
import * as GRAMMAR from 'investigate-events/constants/grammar';
import Scanner from 'investigate-events/util/scanner';
import Parser from 'investigate-events/util/parser';
import {
  CLOSE_PAREN,
  COMPLEX_FILTER,
  ALL_OPERATORS,
  OPEN_PAREN,
  OPERATOR_AND,
  OPERATOR_OR,
  QUERY_FILTER,
  SEARCH_TERM_MARKER,
  TEXT_FILTER
} from 'investigate-events/constants/pill';
import {
  ComplexFilter,
  QueryFilter,
  TextFilter
} from './filter-types';
import { filterValidMeta } from 'investigate-events/util/meta';
import {
  CloseParen,
  OpenParen,
  OperatorAnd,
  OperatorOr
} from './grammar-types';

const { log } = console; // eslint-disable-line

const TWIN_PREFIX = 'twinPill_';

/**
 * String representation of filter with spaces trimmed.
 * @param {object} filter The filter to convert to a string.
 * @return {string}
 */
const _asString = (filter) => {
  let ret = '';
  if (filter.type === QUERY_FILTER) {
    const m = filter.meta ? filter.meta.trim() : '';
    const o = filter.operator ? filter.operator.trim() : '';
    const v = filter.value ? filter.value.trim() : '';
    ret = `${m} ${o} ${v}`.trim();
  } else if (filter.type === COMPLEX_FILTER) {
    ret = filter.complexFilterText ? filter.complexFilterText.trim() : '';
  } else if (filter.type === TEXT_FILTER) {
    const text = filter.searchTerm ? filter.searchTerm.trim() : null;
    ret = text ? `${SEARCH_TERM_MARKER}${text}${SEARCH_TERM_MARKER}` : '';
  } else if (filter.type === OPEN_PAREN) {
    ret = '(';
  } else if (filter.type === CLOSE_PAREN) {
    ret = ')';
  } else if (filter.type === OPERATOR_AND) {
    ret = 'AND';
  } else if (filter.type === OPERATOR_OR) {
    ret = 'OR';
  }
  return ret;
};

/**
 * Creates a Complex filter.
 * @param {string} complexFilterText A complex string
 */
const _createComplexQueryFilter = (complexFilterText) => ComplexFilter.create({ complexFilterText });

/**
 * Creates a normal Query filter.
 * @param {string} meta A meta key
 * @param {string} operator An operator
 * @param {string} [value] The value
 */
const _createQueryFilter = (meta, operator, value) => QueryFilter.create({ meta, operator, value });

/**
 * Creates a Text filter.
 * @param {string} searchTerm The text to search within indexed meta values.
 */
const _createTextQueryFilter = (searchTerm) => TextFilter.create({ searchTerm });

/**
 * Creates an open parenthesis grammar structure.
 */
const _createOpenParen = () => OpenParen.create();

/**
 * Creates a close parenthesis grammar structure.
 */
const _createCloseParen = () => CloseParen.create();

/**
 * Creates an AND logical operator grammar structure.
 */
const _createOperatorAND = () => OperatorAnd.create();

/**
 * Creates an OR logical operator grammar structure.
 */
const _createOperatorOR = () => OperatorOr.create();

/** There arrays indicate which pill types might need logical operator injected */
const injectTypes = [QUERY_FILTER, COMPLEX_FILTER, TEXT_FILTER];

/**
 * Creates a filter for a given type.
 * @param {string} type The type of filter to create
 * @param  {...any} args Arguments for filter creation
 * @see _createComplexQueryFilter()
 * @see _createQueryFilter()
 * @see _createTextQueryFilter()
 * @return A filter.
 */
export const createFilter = (type, ...args) => {
  let filter;
  if (type === COMPLEX_FILTER) {
    filter = _createComplexQueryFilter(...args);
  } else if (type === TEXT_FILTER) {
    filter = _createTextQueryFilter(...args);
  } else if (type === QUERY_FILTER) {
    filter = _createQueryFilter(...args);
  } else {
    throw new Error(`Unknown filter type: "${type}"`);
  }
  return filter;
};

/**
 * Creates a logical operator for a given type.
 * @param {string} type The type of operator to create
 * @see _createOperatorAND()
 * @see _createOperatorOR
 * @return An operator
 */
export const createOperator = (type) => {
  let operator;
  if (type === OPERATOR_AND) {
    operator = _createOperatorAND();
  } else if (type === OPERATOR_OR) {
    operator = _createOperatorOR();
  } else {
    throw new Error(`Unknown operator type: "${type}"`);
  }
  return operator;
};

/**
 * Creates a pair of parentheses,
 * @return {object[]} A pair of parentheses
 */
export const createParens = () => {
  const open = _createOpenParen();
  const close = _createCloseParen();

  // match-up inserted parens, this is useful later
  // when needing to act on a paren set in tandem,
  // like when selecting/focusing
  const twinId = _.uniqueId(TWIN_PREFIX);
  open.twinId = twinId;
  close.twinId = twinId;

  return [open, close];
};

/**
 * Matches up the `twinId` property of parens if they get mismatched. This can
 * happen, for example, when intra-parens ")(" have been inserted.
 * @param {object[]} filters Array of query filters
 * @param {number} insertionIndex Index where parens were inserted that caused
 * a mismatch
 */
export const reassignTwinIds = (filters, insertionIndex) => {
  const cache = [];
  const insertedCloseParen = filters[insertionIndex];
  const insertedOpenParen = filters[insertionIndex + 2];
  let leftIndex = insertionIndex - 1;
  let rightIndex = insertionIndex + 3;
  // look left from insertion index to find matching open paren
  while (leftIndex >= 0) {
    const item = filters[leftIndex];
    if (item.type === CLOSE_PAREN) {
      // save twinId and advance to next filter
      cache.push(item.twinId);
      leftIndex--;
    } else if (item.type === OPEN_PAREN) {
      if (cache.includes(item.twinId)) {
        // this is not the twin you are looking for
        leftIndex--;
      } else {
        // Not in cache, assign close paren to twinId
        insertedCloseParen.twinId = item.twinId;
        break;
      }
    } else {
      leftIndex--;
    }
  }
  // looking right from insertion index to find matching close paren
  while (rightIndex < filters.length) {
    const item = filters[rightIndex];
    if (item.type === OPEN_PAREN) {
      // save twinId and advance to next filter
      cache.push(item.twinId);
      rightIndex++;
    } else if (item.type === CLOSE_PAREN) {
      if (cache.includes(item.twinId)) {
        // this is not the twin you are looking for
        rightIndex++;
      } else {
        // Not in cache, assign open paren to twinId
        item.twinId = insertedOpenParen.twinId;
        break;
      }
    } else {
      rightIndex++;
    }
  }
  return filters;
};

export const hasOperator = (str) => {
  return ALL_OPERATORS.some((d) => str.includes(d));
};

/**
 * Determines if the provided string is marked as a searchTerm.
 * @param {string} str A string
 */
export const isSearchTerm = (str) => {
  return str.charAt(0) === SEARCH_TERM_MARKER &&
    str.charAt(str.length - 1) === SEARCH_TERM_MARKER;
};

/**
 * Parses a given URI string component that represents 0, 1 or more metaFilters
 * for a Core query. Assumes the URI is of the following syntax:
 * `key1 operator1 value1/key2 operator1 value2/../keyN operatorN valueN`,
 * where each `key#` string is a meta key identifier (e.g., `ip.src`, not a
 * display name), each operator is a logical operator (e.g. =, !=, ends), and
 * each `value#` string is a meta value (raw, not alias). Assumes `key#` strings
 * do not need URI decoding (they're just alphanumerics, plus dots maybe), but
 * `value#` strings and operators will need URI decoding.
 *
 * @param {string} uri
 * @returns {object[]} Array of condition objects. Each array item is an object
 * with properties `key` & `value`, where:
 * (i) `key` is a meta key identifier (e.g., "ip.src", not a display name); and
 * (ii) value` is a meta key value (raw, not alias).
 */
export const parsePillDataFromUri = (uri, language = [], aliases = []) => {
  if (isBlank(uri)) {
    // When uri is empty, return empty array. Alas, ''.split() returns a non-empty array; it's a 1-item array with
    // an empty string in it, which is not what we want.  So we check for '' and return [] explicitly here.
    return [];
  }

  // A text filter is never encoded (see metaFiltersAsString), so we cannot decode it.
  // Instead we return the string as it is.
  const decodedQuery = uri.split(' ').map((filter) => {
    if (isSearchTerm(filter)) {
      return filter;
    } else {
      return decodeURIComponent(filter);
    }
  }).join(' ');
  return transformTextToPillData(decodedQuery, { language, aliases, returnMany: true });
};

/**
 * Attempts to convert a string to a pill object. If unsuccesful for any reason,
 * returns a complex pill.
 * @param {string} queryText Fragment to convert to an object
 * @param {Object} options Options hash
 * @param {Object[]} options.availableMeta The language for the selected service.
 * @param {Object} options.aliases The alias mappings for the current language set
 * @param {boolean} options.shouldForceComplex Should we force the creation of a complex
 * @param {boolean} options.returnMany Should return an array of pills instead of just the first pill
 * pill.
 * @return {object} A pill object.
 */
export const transformTextToPillData = (queryText, { language, aliases, shouldForceComplex = false, returnMany = false }) => {
  let result;

  // Create complex pill if asked to
  if (shouldForceComplex) {
    const pill = _createComplexQueryFilter(`(${queryText})`);
    return returnMany ? [ pill ] : pill;
  }

  // Scan queryText into tokens, and pass those to the parser
  const s = new Scanner(queryText);
  try {
    const p = new Parser(s.scanTokens(), language, aliases);
    result = p.parse();
  } catch (err) {
    // Scanning or parsing error, make complex pill
    const pill = _createComplexQueryFilter(queryText);

    return returnMany ? [ pill ] : pill;
  }

  const pills = _treeToPills(result);
  return returnMany ? pills : pills[0];
};

/**
 * Turns data generated by the parser into an array of pills.
 * @param {Object} tree A structure returned by the parser, or a substructure
 * of what is returned
 * @private
 */
const _treeToPills = (tree) => {
  // pills holds criteria that have been definitively turned into pills
  const pills = [];
  // We can only add one text pill, so don't allow more than the first we see
  let textPillAdded = false;
  // tree.children holds parsed items (groups, criteria, AND, OR, NOT)
  while (tree.children.length > 0) {
    const item = tree.children.shift();
    if (item.type === LEXEMES.TEXT_FILTER) {
      if (!textPillAdded) {
        textPillAdded = true;
        pills.push(_createTextQueryFilter(item.text));
      } else if (pills.lastItem?.type === OPERATOR_AND || pills.lastItem?.type === OPERATOR_OR) {
        // If an extra text pill was included, remove the operator before it (if it exists)
        pills.splice(pills.lastIndex, 1);
      }
    } else if (item.type === GRAMMAR.CRITERIA) {
      pills.push(_criteriaToPill(item));
    } else if (item.type === LEXEMES.AND) {
      pills.push(_createOperatorAND());
    } else if (item.type === LEXEMES.OR) {
      pills.push(_createOperatorOR());
    } else if (item.type === GRAMMAR.GROUP) {
      // Push open paren, any inside pills, close paren.
      const groupWithParens = createParens();
      groupWithParens.splice(1, 0, ..._treeToPills(item.group));
      pills.push(...groupWithParens);
    } else if (item.type === GRAMMAR.NOT) {
      // TODO: Change once NOT pills are created in the UI
      const [ next ] = tree.children;
      if (next?.type === GRAMMAR.CRITERIA || next?.type === GRAMMAR.COMPLEX_FILTER) {
        tree.children.shift();
        pills.push(_createComplexQueryFilter(`NOT(${Parser.transformToString(next)})`));
      } else if (next?.type === GRAMMAR.GROUP) {
        tree.children.shift();
        pills.push(_createComplexQueryFilter(`NOT${Parser.transformToString(next)}`));
      } else {
        pills.push(_createOperatorAND(), _createComplexQueryFilter('NOT'));
      }
    } else if (item.type === GRAMMAR.COMPLEX_FILTER) {
      pills.push(_createComplexQueryFilter(`${Parser.transformToString(item)}`));
    } else {
      pills.push(_createComplexQueryFilter(`(${Parser.transformToString(item)})`));
    }
  }

  return pills;
};

/**
 * Takes a single criteria and turns it into a pill. Conditionally makes the
 * pill invalid.
 * @param {GRAMMAR.CRITERIA} criteria A valid criteria returned from the parser
 * @private
 */
const _criteriaToPill = (criteria) => {
  const pill = _createQueryFilter(
    Parser.transformToString(criteria.meta),
    Parser.transformToString(criteria.operator),
    criteria.valueRanges ? criteria.valueRanges.map(Parser.transformToString).join(',') : undefined
  );
  if (criteria.isInvalid) {
    pill.isInvalid = true;
    pill.validationError = criteria.validationError;
  }
  return pill;
};

/**
 * Encodes a given list of metaFilters into a URI string component that can be
 * used for routing. The reverse of `parseMetaFilterUri()`.
 * @param {object[]} filters The array of meta filters.
 * @param {boolean} shouldEncode Should each filter be URL encoded?
 * For structure, see return value of parseMetaFilterUri.
 * @returns {string}
 */
export const metaFiltersAsString = (filters = [], shouldEncode = true) => {
  const filtersAsString = filters
    .map((d) => {
      const str = _asString(d);
      if (str.length > 0) {
        // URL encode all filters except for Text filter
        return (shouldEncode && d.type !== TEXT_FILTER) ? encodeURIComponent(str) : str;
      } else {
        return undefined;
      }
    })
    .filter(removeEmptyFilters)
    .reduce(mergeFilterStrings, '');
  return filtersAsString === '' ? undefined : filtersAsString;
};

const _possibleMeta = (textChunk, availableMeta, originalText, pillData) => {
  let hasInvalidMeta = false;

  const metaConfig = availableMeta.filter(filterValidMeta).find((m) => m.metaName === textChunk);

  if (!metaConfig) {
    pillData.meta = originalText;
    hasInvalidMeta = true;
  } else {
    pillData.meta = metaConfig;
  }

  return {
    pillData,
    hasInvalidMeta
  };
};

const _possibleOperator = (textChunk, selectedMeta, pillData, originalText, chunks) => {
  const possibleOperators = relevantOperators(selectedMeta);
  const operatorConfig = possibleOperators.find((o) => o.displayName === textChunk);
  let hasInvalidOperator = false;

  // no relevant operator, append all original text
  // to operator and bail OR
  // if exists or !exists is present and we have a value,
  // append all original text and bail
  if (!operatorConfig || ((!operatorConfig.hasValue && chunks.length > 2))) {
    pillData.operator = originalText;
    hasInvalidOperator = true;
  } else {
    pillData.operator = operatorConfig;
  }

  return {
    pillData,
    hasInvalidOperator
  };
};

/**
 *
 * @param {String} queryText
 * @param { String } dataSource
 * @param { Object[] } availableMeta: Languages
 * @param { Object } selectedMeta: if a meta is already selected in EPS
 * @returns { Object } pillData: pill object with meta, operator and value maybe
 * The intention here is to bail at the first possible discrepancy.
 * Discrepancy in terms of not being able to match with possible meta/operator.
 * Splitting into chunk is based on rules for typing in META.
 * ex: Space between meta/operator/value is a must.
 * Maintains the string that has been typed in case of a bailout.
 */
export const convertTextToPillData = ({ queryText, availableMeta }) => {
  const originalText = queryText;
  let pillData = {};

  // Nuke any surrounding white space and split them up
  queryText = queryText.trim();
  const chunks = queryText.split(' ');

  const [ meta, operator, ...values ] = chunks;

  if (meta) {
    const possibleMeta = _possibleMeta(meta, availableMeta, originalText, pillData);
    pillData = possibleMeta.pillData;
    if (possibleMeta.hasInvalidMeta) {
      return pillData;
    }
  } else {
    pillData.meta = originalText;
    return pillData;
  }

  // strip away meta and a space from the original text as it has been proccessd
  const unprocessedText = originalText.replace(`${meta}`, '').replace(/^[\s\uFEFF\xA0]/, '');
  if (operator) {
    const possibleOperator = _possibleOperator(
      operator,
      pillData.meta,
      pillData,
      unprocessedText,
      chunks
    );
    pillData = possibleOperator.pillData;
    if (possibleOperator.hasInvalidOperator) {
      return pillData;
    }
  } else {
    pillData.operator = unprocessedText;
    return pillData;
  }

  if (values.length > 0) {
    pillData.value = values.join(' ');
  }

  return pillData;
};

/**
 * Returns an array of values separated by commas.
 * @public
 */
export const valueList = (string, options) => {
  const removeEscapes = options?.removeEscapes ?? false;
  const whitespaceRegex = /\s+/;
  const result = [];
  let tempString = '';
  let activeQuote = null;
  let goodQuoted = false;
  let startingQuotePos = NaN;
  let inbetweenStrings = true;
  let pos = 0;
  // Iterate over the entire string
  while (pos < string.length) {
    const char = string[pos];
    const prevChar = string[pos - 1];
    // While we're not capturing text, we're searching for text to start capturing
    // or quotes to pay attention to
    if (inbetweenStrings) {
      // If we see a quote, store the quote type and location, and start normal
      // character scanning. Make sure the quote is not escaped.
      if ((char === '\'' && (prevChar === undefined || prevChar !== '\\')) || char === '"') {
        activeQuote = char;
        startingQuotePos = pos;
        inbetweenStrings = false;
      // If we see a non-whitespace character, capture it and start normal scanning
      } else if (!whitespaceRegex.test(char)) {

        // if value begins with `\` and it is not an escape char for a quote
        if (char === '\\') {
          const nextChar = string[pos + 1];
          if (nextChar === '\\' && string[pos + 2] !== '\'') {
            tempString += _computeBasedOnRemoveEscapes(removeEscapes, char);
            pos += 1; // skip the next slash
          } else if (nextChar !== '\'') {
            tempString += _computeBasedOnRemoveEscapes(removeEscapes, char);
          }
        } else {
          tempString += char;
        }
        inbetweenStrings = false;
      }
      // If we see whitespace, do nothing here, which trims whitespace off the front
      // of a value
    } else {
      if (pos === string.length - 1 && activeQuote && char !== activeQuote) {
        // Unterminated string, jump back in time and treat the opening quote as part of the value.
        pos = startingQuotePos;
        activeQuote = null;
        tempString = '';
        // inbetweenStrings remains false
        // Skip position increment
        continue;
      // If a quote is encountered, set activeQuote to null only if there's not
      // a non-whitespace character before the next comma or the end of the string
      } else if (char === activeQuote) {
        let commaIdx = string.indexOf(',', pos);
        if (commaIdx === -1) {
          commaIdx = string.length;
        }
        const betweenPosAndComma = string.substring(pos + 1, commaIdx);
        if (betweenPosAndComma.length === 0 || whitespaceRegex.test(betweenPosAndComma)) {
          // If all that exists between the end quote and the comma is whitespace,
          // skip ahead to the comma.
          pos = commaIdx - 1;
          activeQuote = null;
          goodQuoted = true;
        } else {
          // If there are non-whitespace characters (besides the quote) between
          // the quote and the next comma, treat this quote as part of this value.
          tempString += char;
        }
      } else if (char === ',' && !activeQuote) {
        result.push({ value: tempString.trim(), quoted: goodQuoted });
        tempString = '';
        inbetweenStrings = true;
        goodQuoted = false;
      } else if (char === '\\') {
        const nextChar = string[pos + 1];

        if (nextChar === '\\' && !_isQuotePartOfString(string, activeQuote, pos + 2, string[pos + 2])) {
          tempString += _computeBasedOnRemoveEscapes(removeEscapes, char);
          pos += 1; // skip the next slash
        } else if (nextChar !== '\'') {
          tempString += _computeBasedOnRemoveEscapes(removeEscapes, char);
        } else if (nextChar === '\'') {
          // If this is an escaped quote, make sure to skip past it
          removeEscapes ? tempString += '\'' : tempString += '\\\'';
          pos += 1;
        }
      } else {
        tempString += char;
      }
    }
    pos++;
  }
  if (tempString.length > 0) {
    result.push({ value: tempString.trim(), quoted: goodQuoted });
  }
  // Only return non-empty strings
  return result.filter((a) => a.value !== '');
};

/**
 * If removeEscapes: true, this value is for `display only`, and
 * so do not add escapes to the existing value.
 * If false, we add another `\`. This is the value MT needs to query.
 */
const _computeBasedOnRemoveEscapes = (removeEscapes, char) => {
  let str;
  removeEscapes ? str = char : str = '\\\\';
  return str;
};

/**
 * In order to differentiate wrapping quotes from quotes within the string
 */
const _isQuotePartOfString = (string, activeQuote, currPos, charToCheck) => {
  if (charToCheck === '\'') {
    if (activeQuote) {
      // If there is an active quote, then we need to further investigate if this
      // is part of the value.
      let commaIdx = string.indexOf(',', currPos);
      if (commaIdx === -1) {
        commaIdx = string.length;
      }
      // Because there can be multiple comma separated values, we break up the
      // string until the next comma
      const subStr = string.substring(currPos + 1, commaIdx);
      // If there is another `'` after this curr position, then this
      // def is part of the value.
      return subStr.includes('\'');
    } else {
      // If there is no active quote registered, this is def part of the value
      return true;
    }
  } else {
    return false;
  }
};

export const injectLogicalOperatorIfMissing = (pills, injectingOpType) => {
  const result = [];
  let isPreviousOpenParen = false;
  pills.forEach((pill, index) => {
    if (index > 0 && injectTypes.includes(pill.type) && !isPreviousOpenParen) {
      result.push(createOperator(injectingOpType));
      isPreviousOpenParen = false;
    } else if (pill.type === OPEN_PAREN) {
      if (index > 0 && !isPreviousOpenParen) {
        result.push(createOperator(injectingOpType));
      }
      isPreviousOpenParen = true;
    } else {
      isPreviousOpenParen = false;
    }
    result.push(pill);
  });
  return result;
};
