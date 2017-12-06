import { assert } from 'ember-metal/utils';
import get, { getProperties } from 'ember-metal/get';
import { isBlank } from 'ember-utils';
import run from 'ember-runloop';
import RSVP from 'rsvp';
import { encodeMetaFilterConditions } from 'investigate-events/actions/fetch/utils';

// Adds a session id filter to a given Core query filter.
// Appends a condition for session id, but only if a session id is given.
// @param {string} filter A Core filter condition (typically for meta keys other than 'sessionid').
// @param {string} [startSessionId] Optional lower bound (exclusive) for session IDs.
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
 * Parses a given URI string that represents a filter for a Core Events query.
 * Assumes the URI is of the following syntax: `serviceId/startTime/endTime/metaFilterUri`.
 * The `metaFilter` piece is further parsed into an list of individual filter conditions (@see _parseMetaFilterUri).
 * @param {string} uri The URI string.
 * @returns {{ serviceId: string, startTime: number, endTime: number, metaFilter: object }}
 * @public
 */
function parseEventQueryUri(uri) {
  const parts = uri ? uri.split('/') : [];
  const [ serviceId, startTime, endTime ] = parts;
  const metaFilterUri = parts.slice(3, parts.length).join('/');
  const metaFilterConditions = _parseMetaFilterUri(metaFilterUri);

  return {
    serviceId,
    startTime,
    endTime,
    metaFilter: {
      uri: metaFilterUri,
      conditions: metaFilterConditions
    }
  };
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
  return uri.split('/')
    .filter((segment) => !!segment)
    .map((queryString) => {
      const [ meta, operator, ...valuePieces ] = decodeURIComponent(queryString).split(' ');
      const value = valuePieces.join(' ');

      return { meta, value, operator };
    });
}

/**
 * Composes a URI component string for a given set of attributes that define a Core query. This URI component
 * can be used for routing/bookmarking.
 * The reverse of `parseEventQueryUri`.
 * @param {object} queryAttrs An object whose attributes define a Core query. For structure, @see the return
 * value of `parseEventQueryUri`.
 * @returns {string} The URI component string.
 * @public
 */
function uriEncodeEventQuery(queryAttrs) {
  const {
    serviceId, startTime, endTime, metaFilter
  } = getProperties(queryAttrs, 'serviceId', 'startTime', 'endTime', 'metaFilter');

  const conditions = get(metaFilter || {}, 'conditions');
  const metaFilterUri = _uriEncodeMetaFilterConditions(conditions);

  return [ serviceId, startTime, endTime, metaFilterUri ].join('/');
}

/**
 * Encodes a given list of meta conditions into a URI string component that can be used for routing.
 * The reverse of `parseMetaFilterUri()`.
 * @param {object[]} conditions The array of meta conditions. For structure, @see return value of parseMetaFilterUri.
 * @returns {string}
 * @private
 */
function _uriEncodeMetaFilterConditions(conditions = []) {
  return conditions
    .map((condition) => {
      if (condition.operator === 'exists' || condition.operator === '!exists') {
        return encodeURIComponent(`${condition.meta} ${condition.operator}`);
      } else {
        if (condition.meta && condition.operator && condition.value) {
          return encodeURIComponent(`${condition.meta} ${condition.operator} ${condition.value}`);
        }
      }
    })
    .join('/');
}

export {
  buildMetaValueStreamInputs,
  executeMetaValuesRequest,
  parseEventQueryUri,
  uriEncodeEventQuery
};
