import { isBlank } from '@ember/utils';
import { relevantOperators } from 'investigate-events/util/possible-operators';
import {
  COMPLEX_OPERATORS,
  OPERATORS,
  SEARCH_TERM_MARKER
} from 'investigate-events/constants/pill';

const _createComplexFilterText = (complexFilterText) => ({
  meta: undefined,
  operator: undefined,
  value: undefined,
  complexFilterText
});

/**
 * Determines if the provided string is marked as a searchTerm.
 * @param {string} str A string
 */
export const isSearchTerm = (str) => str.charAt(0) === SEARCH_TERM_MARKER;

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

  // 1. Check if the text contains characters
  // that immediately make the query complex
  const hasComplexItem = COMPLEX_OPERATORS.some((operator) => queryText.includes(operator));
  if (hasComplexItem || shouldForceComplex) {
    if (!(queryText.startsWith('(') && queryText.endsWith(')'))) {
      queryText = `(${queryText})`;
    }

    return _createComplexFilterText(queryText);
  }

  // 2. Check if the text contains characters that
  // immediately marks it a Text filter
  const hasSearchTerm = isSearchTerm(queryText);
  if (hasSearchTerm) {
    return {
      searchTerm: queryText
    };
  }

  // 3. Then check to see if there IS an operator,
  // no operator = complex
  const operator = OPERATORS.find((option) => {
    // This regex looks for the patterns <space><operator><space> or
    // <space><operator><end of string>. If it finds that, it assumes that's
    // the operator you're looking for.
    const regex = new RegExp(`(\\s${option}(\\s|$))`);
    return !!queryText.match(regex);
  });

  if (!operator) {
    return _createComplexFilterText(queryText);
  }

  // eliminate empty chunks
  const chunks = queryText.split(operator).filter((s) => s !== '');

  let [ meta ] = chunks;
  meta = meta.trim();

  if (availableMeta && availableMeta.length > 0) {
    // 4. Check that the meta is a real meta,
    // if we do not recognize the meta, complex
    const metaConfig = availableMeta.find((m) => m.metaName === meta);
    if (!metaConfig) {
      return _createComplexFilterText(queryText);
    }

    // 5. Check that the operator applies to the meta,
    // if the operator isn't valid for the meta, complex
    const possibleOperators = relevantOperators(metaConfig);
    const operatorConfig = possibleOperators.find((o) => o.displayName === operator);
    if (!operatorConfig) {
      return _createComplexFilterText(queryText);
    }

    // 6. If the operator requires value and doesn't have one,
    // then complex
    // chunks are split by operator, so "medium = 1" would be
    // two chunks
    if (chunks.length < 2 && operatorConfig.hasValue) {
      return _createComplexFilterText(queryText);
    }

    // 7. if the operator does not have a value but a value is
    // include, then complex
    if (chunks.length >= 2 && !operatorConfig.hasValue) {
      return _createComplexFilterText(queryText);
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

  return {
    meta,
    operator: operator.trim(),
    value,
    complexFilterText: undefined
  };
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
      let ret;

      if (d.complexFilterText) {
        ret = d.complexFilterText;
      } else if (d.searchTerm) {
        ret = `${SEARCH_TERM_MARKER}${d.searchTerm}`;
      } else {
        ret = `${(d.meta) ? d.meta.trim() : ''} ${(d.operator) ? d.operator.trim() : ''} ${(d.value) ? d.value.trim() : ''}`;
      }
      return isBlank(ret) ? undefined : encodeURIComponent(ret);
    })
    .filter((d) => !!d)
    .join('/');

  return encodedFilters || undefined;
};
