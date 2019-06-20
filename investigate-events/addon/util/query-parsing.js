import { isBlank } from '@ember/utils';
import { relevantOperators } from 'investigate-events/util/possible-operators';
import {
  COMPLEX_FILTER,
  COMPLEX_OPERATORS,
  OPERATORS,
  QUERY_FILTER,
  SEARCH_TERM_MARKER,
  TEXT_FILTER
} from 'investigate-events/constants/pill';
import {
  ComplexFilter,
  QueryFilter,
  TextFilter
} from './filter-types';

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
    .map((queryString) => {
      const decodedQuery = decodeURIComponent(queryString);
      return transformTextToPillData(decodedQuery, availableMeta);
    });
};

/**
 * Attempts to convert a string to a pill object. If unsuccesful for any reason,
 * returns a complex pill.
 * @param {string} queryText Fragment to convert to an object
 * @param {object[]} availableMeta The language for the selected service.
 * @param {boolean} shouldForceComplex Should we force the creation of a complex
 * pill.
 * @return {object} A pill object.
 */
export const transformTextToPillData = (queryText, availableMeta, shouldForceComplex = false) => {

  // Nuke any surrounding white space
  queryText = queryText.trim();

  // 1. Check if the text contains characters that mark it as a Text filter, and
  // remove them if it does.
  const hasSearchTerm = isSearchTerm(queryText);
  if (hasSearchTerm) {
    const term = queryText.slice(1, -1);
    return _createTextQueryFilter(term);
  }

  // 2. Check if the text contains characters make the query complex
  if (hasComplexText(queryText) || shouldForceComplex) {
    if (!queryText.startsWith('(') && !queryText.endsWith(')')) {
      queryText = `(${queryText})`;
    }
    return _createComplexQueryFilter(queryText);
  }

  // 3. Then check to see if there IS an operator. No operator = complex
  const operator = OPERATORS.find((option) => {
    // This regex looks for the patterns <space><operator><space> or
    // <space><operator><end of string>. If it finds that, it assumes that's
    // the operator you're looking for.
    const regex = new RegExp(`(\\s${option}(\\s|$))`);
    return !!queryText.match(regex);
  });

  if (!operator) {
    return _createComplexQueryFilter(queryText);
  }

  // eliminate empty chunks
  const chunks = queryText.split(operator).filter((s) => s !== '');

  let [ meta ] = chunks;
  meta = meta.trim();

  if (availableMeta && availableMeta.length > 0) {
    // 4. Check that the meta is a real meta.
    // If we do not recognize the meta, complex.
    const metaConfig = availableMeta.find((m) => m.metaName === meta);
    if (!metaConfig) {
      return _createComplexQueryFilter(queryText);
    }

    // 5. Check that the operator applies to the meta.
    // If the operator isn't valid for the meta, complex.
    const possibleOperators = relevantOperators(metaConfig);
    const operatorConfig = possibleOperators.find((o) => o.displayName === operator);
    if (!operatorConfig) {
      return _createComplexQueryFilter(queryText);
    }

    // 6. If the operator requires value and doesn't have one, then complex
    // chunks are split by operator. So, "medium = 1" would be two chunks.
    if (chunks.length < 2 && operatorConfig.hasValue) {
      return _createComplexQueryFilter(queryText);
    }

    // 7. if the operator does not have a value, but a value is include,
    // then complex.
    if (chunks.length >= 2 && !operatorConfig.hasValue) {
      return _createComplexQueryFilter(queryText);
    }
  }

  // NOT COMPLEX!
  let value;
  if (chunks.length > 2) {
    [ , ...value ] = chunks;
    value = value.join(operator).trim();
  } else {
    [ , value ] = chunks;
    // empty means it isn't there
    value = (!value || value.trim() === '') ? undefined : value.trim();
  }

  return _createQueryFilter(meta, operator, value);
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

  const metaConfig = availableMeta.find((m) => m.metaName === textChunk);
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