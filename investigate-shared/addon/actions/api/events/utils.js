/**
 * If you've come here from a deprecation warning, look at
 * `findAllPromiseRequest()`/`streamingRequest()` for the proper way to
 * initiate `promiseRequest`/`streamRequest`.
 * @public
 */
import { lookup } from 'ember-dependency-lookup';

const SEARCH_TERM_MARKER = '\u02F8'; // RAISED COLON "˸"
const OPEN_PAREN = 'open-paren';
const CLOSE_PAREN = 'close-paren';

export const removeEmptyFilters = (d) => !!d;

/**
 * This function looks for empty parentheses, `( )`, and removed them from the
 * array of filters. This is a recursive function that can remove nested empty
 * parens, `((( )))`. This assumes that the `filters` have a `type` property.
 * @param {object[]} filters An array of query pills
 */
export const removeEmptyParens = (filters) => {
  const _filters = filters.filter((filter, i, arr) => {
    if (filter.type === OPEN_PAREN && arr[i + 1] && arr[i + 1].type === CLOSE_PAREN ||
      filter.type === CLOSE_PAREN && arr[i - 1] && arr[i - 1].type === OPEN_PAREN) {
      return false;
    } else {
      return true;
    }
  });
  // If the filtered `_filters` does not match the original `filters`, then
  // we've removed some parens. Run through this again to see if there were
  // nested empty parens.
  if (_filters.length !== filters.length) {
    return removeEmptyParens(_filters);
  } else {
    return _filters;
  }
};

/**
 * Reduce function to convert an array of strings into one long string
 * @private
 */
export const mergeFilterStrings = (() => {
  let _hideSeparator = false;
  return (acc, cur, idx, src) => {
    if (cur === '(') {
      _hideSeparator = true;
      // Make sure to include an && between a close and open paren and a pill
      // and open paren, but not multiple open parens. For example:
      // - ) && (
      // - pill && (
      // - ((
      if (idx > 0 && src[idx - 1] !== '(') {
        return `${acc} && ${cur}`;
      } else {
        return `${acc}${cur}`;
      }
    } else if (cur === ')') {
      return `${acc}${cur}`;
    } else {
      let separator = ' && ';
      // If we're intentionally hiding the separator, or if this is the first
      // item, then we don't need the separator.
      if (_hideSeparator || idx === 0) {
        separator = '';
        _hideSeparator = false;
      }
      return `${acc}${separator}${cur}`;
    }
  };
})();

/**
 * Creates a metaFilter conditions filter
 * @param {string} value - A query string
 * @return {object} A field/value object
 * @public
 */
export const conditionsFilter = (value) => ({ field: 'query', value });

/**
 * Creates a metaFilter conditions filter
 * @param {string} value - A query string
 * @return {object} A field/value object
 * @public
 */
export const selectFilter = (value) => ({ field: 'select', value });

/**
 * Creates a serviceId filter
 * @param {string|number} value - The Id of the service
 * @return {object} A field/value object
 * @public
 */
export const serviceIdFilter = (value) => ({ field: 'endpointId', value });

/**
 * Creates a sessionId filter
 * @param {number} value - The Id of the session
 * @return {object} A field/value object
 * @public
 */
export const sessionIdFilter = (value) => ({ field: 'sessionId', value });

/**
 * Creates a sessionIds filter
 * @param {object[]} values - List of session Ids
 * @return {object} A field/values object
 * @public
 */
export const sessionIdsFilter = (values) => ({ field: 'sessionIds', values });

/**
 * Creates a threshold filter
 * @param {number} value - The threshold value
 * @return {object} A field/value object
 * @public
 */
export const thresholdFilter = (value) => ({ field: 'threshold', value });

/**
 * Creates a time range filter
 * @param {number} startTime - The beginning date
 * @param {number} endTime - The ending date
 * @return {object} A field/range object
 * @public
 */
export const timeRangeFilter = (startTime, endTime) => ({
  field: 'timeRange',
  range: {
    from: startTime,
    to: endTime
  }
});

/**
 * Creates a search term filter
 * @param {string} value - The string to search for
 * @return {object} A field/value object
 * @public
 */
export const searchTermFilter = (value) => ({ field: 'searchTerm', value });

/**
 * Extracts a searchTerm filter from an array of other filters (guided/complex).
 * Returns an object that has `metaFilter`s and `searchTerm` if found.
 * @param {object[]} filters aka metaFilters
 */
export const extractSearchTermFromFilters = (filters) => {
  let searchTerm;
  const metaFilters = filters.reduce((acc, cur) => {
    if (cur.searchTerm) {
      searchTerm = cur.searchTerm;
      return acc;
    } else {
      acc.push(cur);
      return acc;
    }
  }, []);
  return { metaFilters, searchTerm };
};

/**
 * Creates a Promise request with its "method" set to `findAll`,
 * @param {string} modelName - Name of model
 * @param {object} query - (Optional) Query params for request
 * @param {object} streamOptions - (Optional) Stream params
 * @return {object} An RSVP Promise
 * @public
 */
export const findAllPromiseRequest = (modelName, query = {}, streamOptions = {}) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'findAll',
    modelName,
    query,
    streamOptions
  });
};

/**
 * Creates a Promise request with its "method" set to `query`,
 * @param {string} modelName - Name of model
 * @param {object} query - (Optional) Query params for request
 * @param {object} streamOptions - (Optional) Stream params
 * @return {object} An RSVP Promise
 * @public
 */
export const queryPromiseRequest = (modelName, query = {}, streamOptions = {}) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'query',
    modelName,
    query,
    streamOptions
  });
};

/**
 * Creates a Promise request with its "method" set to `stream`,
 * @param {string} modelName - Name of model
 * @param {object} query - (Optional) Query params for request
 * @param {object} streamOptions - (Optional) Stream params
 * @return {object} An RSVP Promise
 * @public
 */
export const streamPromiseRequest = (modelName, query = {}, streamOptions = {}) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'stream',
    modelName,
    query,
    streamOptions
  });
};

/**
 * Creates a Stream request with its "method" set to `stream`,
 * @param {string} modelName - Name of model
 * @param {object} handlers - (Optional) Callbacks to handle stream lifecycle
 * events
 * @param {object} query - (Optional) Query params for request
 * @param {object} streamOptions - (Optional) Stream params
 * @return {null}
 * @public
 */
export const streamingRequest = (modelName, query = {}, handlers = {}, streamOptions = {}) => {
  const request = lookup('service:request');
  request.streamRequest({
    method: 'stream',
    modelName,
    query,
    streamOptions,
    ...handlers
  });
};

/**
 * Encodes a given list of meta conditions into a "where clause" string that can
 * be used by NetWitness Core.
 * @param {object[]} conditions - The array of meta conditions.
 * @param {object[]} language - Array of meta key definitions form the
 * NetWitness Core endpoint.  This is used for checking the data types of meta
 * keys, which is needed when deciding whether to wrap values in quotes.
 * @returns {string}
 * @public
 */
export const encodeMetaFilterConditions = (conditions = []) => {
  conditions = removeEmptyParens(conditions);
  const conditionsAsString = conditions
    .map((condition) => {
      const { meta, operator, value, complexFilterText, searchTerm, type } = condition;
      if (type === 'open-paren') {
        return '(';
      } else if (type === 'close-paren') {
        return ')';
      } else if (complexFilterText) {
        return complexFilterText;
      } else if (searchTerm) {
        return `${SEARCH_TERM_MARKER}${searchTerm}${SEARCH_TERM_MARKER}`;
      } else {
        return `${(meta) ? meta.trim() : ''} ${(operator) ? operator.trim() : ''} ${(value) ? value.trim() : ''}`.trim();
      }
    })
    .filter(removeEmptyFilters)
    .reduce(mergeFilterStrings, '');
  return conditionsAsString;
};

/**
 * Prepends a query string that will filter results based on a starting
 * sessionId
 * @param {string} filter - A string of filter conditions
 * @param {number} startSessionId
 * @private
 */
export const addSessionIdFilter = (filter, startSessionId) => {
  const out = [];
  if (startSessionId) {
    out.push(`(sessionid > ${startSessionId})`);
  }
  if (filter) {
    out.push(filter);
  }
  return out.join(' && ');
};

// *******
// BEGIN - Should be moved with Download Manager's extract api call
// *******
export const _addFilter = (query, field, value, valueKey = 'value') => {
  if (!query.filter) {
    query.filter = [];
  }

  const obj = { field };
  obj[valueKey] = value;

  query.filter.push(obj);
  return query;
};

export const endpointFilter = (endpointId) => {
  const query = {
    filter: [{
      field: 'endpointId',
      value: endpointId
    }]
  };

  return query;
};

export const addFileTypeFilter = (query, type) => {
  return _addFilter(
    query,
    'outputContentType',
    type
  );
};

export const addSessionIdsFilter = (query, ids) => {
  return _addFilter(
    query,
    'sessionIds',
    ids,
    'values'
  );
};

export const addFilenameFilter = (query, filename) => {
  if (filename) {
    query = _addFilter(
      query,
      'filename',
      filename
    );
  }
  return query;
};

export const addMetaToDownloadFilter = (query, metaKeys) => {
  if (metaKeys) {
    query = _addFilter(
      query,
      'exportSelections',
      metaKeys,
      'values'
    );
  }
  return query;
};

export const createFilename = (eventDownloadType, serviceName, sessionIds, isSelectAll) => {
  /*
   If the file name is empty, the service will return a UUID filename.  And
   if we do not have the required paramters to make a file name, it is best
   to allow the service assign an UUID instead of the UI giving an undefined.
   */
  let fileName = '';

  if (serviceName && eventDownloadType) {
    if (isSelectAll) {
      fileName = `${serviceName.replace(/\s/g, '')}_ALL_EVENTS_${eventDownloadType}`;
    } else if (sessionIds.length > 1) {
      fileName = `${serviceName.replace(/\s/g, '')}_${sessionIds.length}_EVENTS_${eventDownloadType}`;
    } else if (sessionIds.length === 1) {
      fileName = `${serviceName.replace(/\s/g, '')}_SID${sessionIds[0]}_${eventDownloadType}`;
    }
  }

  return fileName;
};
// *******
// END - Should be moved with Download Manager's extract api call
// *******
