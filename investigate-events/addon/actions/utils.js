import { assert } from '@ember/debug';
import { get, getProperties } from '@ember/object';
import { isBlank, isEmpty } from '@ember/utils';
import { run } from '@ember/runloop';
import RSVP from 'rsvp';
import { encodeMetaFilterConditions } from 'investigate-events/actions/fetch/utils';
import TIME_RANGES from 'investigate-events/constants/time-ranges';

/**
 * Adds a session id filter to a given Core query filter. Appends a condition
 * for session id, but only if a session id is given.
 * @param {string} filter - A Core filter condition (typically for meta keys
 * other than 'sessionid')
 * @param {string} [startSessionId] - Lower bound (exclusive) for session IDs
 * @return {string} A sessionId filter
 * @private
 */
function _addSessionIdFilter(filter, startSessionId) {
  const out = [];
  if (startSessionId) {
    out.push(`(sessionid > ${startSessionId})`);
  }
  if (filter) {
    out.push(filter);
  }
  return out.join(' && ');
}

/**
 * Creates (but does not start) a stream to fetch a given number of events.
 * To start the stream, the caller should call `stream.start()`.
 * @param {object} query Represents the Core query inputs
 * @param {object[]} language Array of meta key definitions from Core SDK `language` call.
 * @param {number} limit The maximum number of records to stream to the client.
 * @param {number} batch The maximum number of records to include in a single socket response message.
 * @param {string} [startSessionId] Optional lower bound (exclusive) for session IDs.
 * @returns {object} Newly created stream instance.
 * @public
 */
function _buildEventStreamInputs(query, language, limit, batch = 1, startSessionId = null) {
  const inputs = _makeServerInputsForQuery(query, language);
  inputs.stream = { limit, batch };
  const metaFilterInput = inputs.filter.findBy('field', 'query');
  metaFilterInput.value = _addSessionIdFilter(metaFilterInput.value, startSessionId);
  return inputs;
}

function buildMetaValueStreamInputs(metaName, query, language, queryOptions, limit, batch) {
  const inputs = _buildEventStreamInputs(query, language, limit, batch);
  inputs.filter.pushObject({ field: 'metaName', value: metaName });
  if (queryOptions) {
    const { size, metric, sortField, sortOrder } = getProperties(queryOptions, 'size', 'metric', 'sortField', 'sortOrder');
    inputs.filter.pushObjects([
      { field: 'valuesCount', value: size },
      { field: 'flags', value: `${metric},sort-${sortField},order-${sortOrder}` }
    ]);
  }
  return inputs;
}

/**
 * Given an object representing a query, computes the input parameters required to submit that
 * query to the server.
 * @param {object} query The query object.
 * @param {object[]} language Array of meta key definitions. @see investigate-events/state/query
 * @public
 */
function _makeServerInputsForQuery(query, language) {
  const {
      serviceId, startTime, endTime, metaFilter
    } = getProperties(
      query || {}, 'serviceId', 'startTime', 'endTime', 'metaFilter'
    );

  assert(
    'Cannot make a core query without a service id, start time & end time.',
    !isBlank(serviceId) && !isNaN(startTime) && !isNaN(endTime)
  );

  return {
    filter: [
      { field: 'endpointId', value: serviceId },
      { field: 'timeRange', range: { from: startTime, to: endTime } },
      { field: 'query', value: encodeMetaFilterConditions(get(metaFilter || {}, 'conditions'), language) }
    ]
  };
}

function executeMetaValuesRequest(request, inputs, values) {
  return new RSVP.Promise((resolve, reject) => {
    values.setProperties({
      data: [],
      status: 'streaming',
      reason: undefined
    });

    // let rendering for request about to go out happen
    // before request goes out
    run.next(() => {
      request.streamRequest({
        method: 'stream',
        modelName: 'core-meta-value',
        query: inputs,
        onInit(stopStream) {
          values.set('stopStreaming', stopStream);
        },
        onResponse(response) {
          if (response) {
            run.next(function() {
              if (response.data && response.data.length) {
                // Meta Values call *sometimes* returns "partial" results while still computing results.
                // So when we get values back, replace whatever the previous set of values were; don't append to them.
                values.set('data', response.data);
              }
              values.set('description', response.meta && response.meta.description);
              const percent = response.meta && response.meta.percent;
              if (percent !== undefined) {
                values.set('percent', percent);
              }
            });
          }
        },
        onError(response) {
          values.setProperties({
            status: 'error',
            reason: response && response.code
          });
          reject();
        },
        onCompleted() {
          values.set('status', 'complete');
          resolve();
        },
        onStopped() {
          values.set('status', 'idle');
          resolve();
        }
      });
    });
  });
}

/**
 * Parses a URL query param object.
 * The `metaFilter` piece is further parsed into an list of individual filter
 * conditions (@see _parseMetaFilterUri).
 * @param {object} params Query param object.
 * @return {object}
 * @public
 */
function parseQueryParams(params) {
  return {
    endTime: params.et,
    sessionId: params.eid,
    metaFilter: {
      uri: params.mf,
      conditions: _parseMetaFilterUri(params.mf)
    },
    metaPanelSize: params.mps,
    reconSize: params.rs,
    serviceId: params.sid,
    startTime: params.st,
    selectedTimeRangeId: _getTimeRangeIdFromRange(params.st, params.et)
  };
}

/**
 * Given startTime and endTime, this calculates number of days, hours and minutes and matches that with the RANGES array from time-ranges.js
 * to find the matching id. (eg.'LAST_30_MINUTES')
 * @param {number} startTime
* @param {number} endTime
 * @return {object}
 * @private
 */
function _getTimeRangeIdFromRange(startTime, endTime) {
  const seconds = (endTime - startTime) + 1;
  const rangeObj = _getDaysHrsMinsFromSecs(seconds);
  let unit, value;
  for (const prop in rangeObj) {
    if (rangeObj[prop] !== 0) {
      unit = prop;
      value = rangeObj[prop];
    }
  }
  const getMatchingRange = (unit, value) => TIME_RANGES.RANGES.find((d) => (d.unit === unit && d.value === value));
  const range = getMatchingRange(unit, value);
  return range ? range.id : TIME_RANGES.ALL_DATA;
}

/**
 * Given the seconds, it calculates the number of months, days, hours and minutes.
 * @param {number} seconds
 * @return {object}
 * @private
 */
function _getDaysHrsMinsFromSecs(s) {
  let h, mi, mo, d;
  mi = Math.floor(s / 60);
  s = s % 60;
  h = Math.floor(mi / 60);
  mi = mi % 60;
  d = Math.floor(h / 24);
  h = h % 24;
  // if number of days is 30, we are considering it as a month for our timeRange calculations.
  if (d === 30 && h === 0 && mi === 0) {
    mo = 1;
    d = 0;
  } else {
    mo = 0;
  }
  return { months: mo, days: d, hours: h, minutes: mi };
}

/**
 * Parses a given URI string component that represents 0, 1 or more meta conditions for a Core query.
 * Assumes the URI is of the following syntax: `key1 operator1 value1/key2 operator1 value2/../keyN operatorN valueN`, where each `key#` string is
 * a meta key identifier (e.g., `ip.src`, not a display name), each operator is a logical operator (e.g. =, !=, <), and each `value#` string is a meta value (raw, not alias).
 * Assumes `key#` strings do not need URI decoding (they're just alphanumerics, plus dots maybe), but `value#` strings
 * and operators will need URI decoding.
 * If any duplicate conditions are found, the duplicates are discarded; i.e., only 1 instance of the condition is
 * returned. This is done because the duplicate conditions don't have any net effect on the filter.
 * @param {string} uri
 * @returns {object[]} Array of condition objects. Each array item is an object with properties `key` & `value`, where:
 * (i) `key` is a meta key identifier (e.g., "ip.src", not a display name); and
 * (ii) value` is a meta key value (raw, not alias).
 * @private
 */
function _parseMetaFilterUri(uri) {
  if (isBlank(uri)) {
    // When uri is empty, return empty array. Alas, ''.split() returns a non-empty array; it's a 1-item array with
    // an empty string in it, which is not what we want.  So we check for '' and return [] explicitly here.
    return [];
  }
  const operators = [
    '!=', '<=', '<', '>=', '>', '=', '!exists', 'exists', 'contains', 'begins', 'ends'
  ];

  // look for a set of double quotes
  // replace found forward slashes with a temp placeholder because forward slashes are used to split the uri
  const matches = uri.match(/'([^']*)'/g);

  if (matches) {
    matches.forEach((match) => {
      uri = uri.replace(match, match.replace(/\//g, '__slash_place_holder__'));
    });
  }

  return uri.split('/')
    .filter((segment) => !!segment)
    .map((queryString) => {
      // replace slash placeholders with forward slash now that uri has already been split
      queryString = queryString.replace(/__slash_place_holder__/g, '/');

      const decodedQuery = decodeURIComponent(queryString);
      if (queryString.includes('||') || queryString.includes('&&')) {
        return {
          complexFilter: queryString
        };
      }

      const operator = operators.find((option) => {
        if (decodedQuery.includes('!exists')) {
          return option === '!exists';
        } else if (decodedQuery.includes('<=')) {
          return option === '<=';
        } else if (decodedQuery.includes('>=')) {
          return option === '>=';
        } else {
          return decodedQuery.includes(option);
        }
      });

      const chunks = decodedQuery.split(operator);

      if (chunks.length > 2) {
        const [ meta, ...value ] = chunks;
        return {
          meta,
          operator,
          value: value.join(operator)
        };
      } else {
        const [ meta, value ] = chunks;
        return { meta, operator, value };
      }
    });
}

function serializeQueryParams(qp = []) {
  const keys = Object.keys(qp);
  const values = Object.values(qp);
  return keys.map((d, i) => `${d}=${values[i]}`).join('&');
  // Once we drop IE11 we should be able to use Object.entries
  // return Object.entries(qp).map((d) => `${d[0]}=${d[1]}`).join('&');
}

/**
 * Encodes a given list of meta conditions into a URI string component that can be used for routing.
 * The reverse of `parseMetaFilterUri()`.
 * @param {object[]} conditions The array of meta conditions. For structure, @see return value of parseMetaFilterUri.
 * @returns {string}
 * @private
 */
function uriEncodeMetaFilters(filters = []) {
  const encodedFilters = filters
    .map((d) => {
      let ret;

      if (d.complexFilter) {
        ret = d.complexFilter;
      } else {
        if (d.operator === 'exists' || d.operator === '!exists') {
          ret = `${d.meta} ${d.operator}`;
        } else if (d.meta && d.operator && d.value) {
          ret = `${d.meta}${d.operator}${d.value}`;
        }
        return encodeURIComponent(ret);
      }
    })
    .filter((d) => !!d)
    .join('/');

  return encodedFilters || undefined;
}

function uriEncodeFreeFormText(rawText) {
  if (isEmpty(rawText)) {
    return undefined;
  } else {
    return encodeURIComponent(rawText);
  }
}

export {
  buildMetaValueStreamInputs,
  executeMetaValuesRequest,
  parseQueryParams,
  serializeQueryParams,
  uriEncodeMetaFilters,
  uriEncodeFreeFormText,
  _getTimeRangeIdFromRange
};
