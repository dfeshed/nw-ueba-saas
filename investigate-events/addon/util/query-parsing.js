import { isBlank } from '@ember/utils';
import { relevantOperators } from 'investigate-events/util/possible-operators';
import * as LEXEMES from 'investigate-events/constants/lexemes';
import * as GRAMMAR from 'investigate-events/constants/grammar';
import Scanner from 'investigate-events/util/scanner';
import Parser from 'investigate-events/util/parser';
import {
  COMPLEX_FILTER,
  COMPLEX_OPERATORS,
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
  OpenParen
} from './grammar-types';

const { log } = console; // eslint-disable-line

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
 * Creates a pair of parentheses,
 * @return {object[]} A pair of parentheses
 */
export const createParens = () => {
  const open = _createOpenParen();
  const close = _createCloseParen();
  return [open, close];
};

export const hasComplexText = (str) => {
  return COMPLEX_OPERATORS.some((d) => str.includes(d));
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
export const parsePillDataFromUri = (uri, availableMeta) => {
  if (isBlank(uri)) {
    // When uri is empty, return empty array. Alas, ''.split() returns a non-empty array; it's a 1-item array with
    // an empty string in it, which is not what we want.  So we check for '' and return [] explicitly here.
    return [];
  }
  return uri.split('/')
    .filter((segment) => !!segment)
    .flatMap((queryString) => {
      const decodedQuery = decodeURIComponent(queryString);
      return transformTextToPillData(decodedQuery, availableMeta, false, true);
    });
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
  // itemList holds parsed items that may or may not be included in a complex
  // pill, depending on whether or not they are next to an OR (`||`)
  let itemList = [];
  // We can only add one text pill, so don't allow more than the first we see
  let textPillAdded = false;
  // tree.children holds parsed items (groups, criteria, AND, OR)
  while (tree.children.length > 0) {
    const item = tree.children.shift();
    if (item.type === LEXEMES.TEXT_FILTER) {
      if (!textPillAdded) {
        textPillAdded = true;
        pills.push(_createTextQueryFilter(item.text));
      }
    } else {
      itemList.push(item);
    }

    // If the next item is a logical AND, add the pill(s) we just saw and consume the AND
    if ((tree.children.length > 0 && tree.children[0].type === LEXEMES.AND) || tree.children.length === 0) {
      // Consume the AND
      tree.children.shift();
      // If there is only one item waiting to be made into a pill, do that
      // This happens when the item before does not have an OR in front of it
      if (itemList.length === 1) {
        const item = itemList.shift();
        // If that one item is a normal criteria, turn it into a pill
        if (item.type === GRAMMAR.CRITERIA) {
          pills.push(_criteriaToPill(item));
        // If that one item is a group, add it as a pill IF it only has a single
        // child which is a criteria. Otherwise, add it as a complex pill.
        } else if (item.type === GRAMMAR.GROUP) {
          // Push open paren, any inside pills, close paren.
          const groupWithParens = createParens();
          groupWithParens.splice(1, 0, ..._treeToPills(item.group));
          pills.push(...groupWithParens);
        } else if (item.type === GRAMMAR.COMPLEX_FILTER) {
          pills.push(_createComplexQueryFilter(`${Parser.transformToString(item)}`));
        } else {
          pills.push(_createComplexQueryFilter(`(${Parser.transformToString(item)})`));
        }
      // If there is more than one criteria waiting, they are a complex pill. Transform them all back to strings
      // and join them together, then add as one complex pill. This happens when the criteria before has at least
      // one OR and other criteria in front of it. The UI cannot handle this yet, so make it complex.
      } else if (itemList.length > 1) {
        pills.push(_createComplexQueryFilter(`(${itemList.map(Parser.transformToString).join('')})`));
        itemList = [];
      }
    } else if (tree.children.length > 0 && tree.children[0].type === LEXEMES.OR) {
      // Otherwise if it's an OR, add the OR to itemList to be added together in a bigger complex pill
      // This will tree in any criteria on the right or left of this OR and any before/after it all getting
      // combined into a larger complex pill.
      itemList.push(tree.children.shift());
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
 * For structure, see return value of parseMetaFilterUri.
 * @returns {string}
 */
export const uriEncodeMetaFilters = (filters = []) => {
  const encodedFilters = filters
    .map((d) => {
      const str = _asString(d);
      if (str.length > 0) {
        // URL encode all filters except for Text filter
        return (d.type !== TEXT_FILTER) ? encodeURIComponent(str) : str;
      } else {
        return undefined;
      }
    })
    .filter((d) => !!d)
    .join('/');

  return encodedFilters || undefined;
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
