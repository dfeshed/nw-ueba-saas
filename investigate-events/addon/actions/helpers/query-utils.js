import Ember from 'ember';
import hasherizeEventMeta from './hasherize-event-meta';
import { setEventLogData, setEventLogDataStatus, getEventLogDataStatus } from 'component-lib/utils/log-utils';

const {
  assert,
  get,
  getProperties,
  isBlank,
  RSVP,
  run
} = Ember;

const _addMilliseconds = (dateInSeconds) => dateInSeconds * 1000;
const _removeMilliseconds = (dateInMilliseconds) => dateInMilliseconds / 1000 | 0;
const _floorMinute = (dateInMilliseconds) => new Date(dateInMilliseconds).setSeconds(0);
const _ceilMinute = (dateInMilliseconds) => new Date(dateInMilliseconds).setSeconds(59);
const _floorAtMinute = (dateInSeconds) => _removeMilliseconds(_floorMinute(_addMilliseconds(dateInSeconds)));
const _ceilAtMinute = (dateInSeconds) => _removeMilliseconds(_ceilMinute(_addMilliseconds(dateInSeconds)));

// Adds a session id filter to a given Core query filter.
// Appends a condition for session id, but only if a session id is given.
// @param {string} filter A Core filter condition (typically for meta keys other than 'sessionid').
// @param {string} [startSessionId] Optional lower bound (exclusive) for session IDs.
function addSessionIdFilter(filter, startSessionId) {
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
 * @param {object} query Represents the Core query inputs (@see investigate-events/state/query-definition)
 * @param {object[]} language Array of meta key definitions from Core SDK `language` call.
 * @param {number} limit The maximum number of records to stream to the client.
 * @param {number} batch The maximum number of records to include in a single socket response message.
 * @param {string} [startSessionId] Optional lower bound (exclusive) for session IDs.
 * @returns {object} Newly created stream instance.
 * @public
 */
function buildEventStreamInputs(query, language, limit, batch = 1, startSessionId = null) {
  const inputs = makeServerInputsForQuery(query, language);
  inputs.stream = { limit, batch };
  const metaFilterInput = inputs.filter.findBy('field', 'query');
  metaFilterInput.value = addSessionIdFilter(metaFilterInput.value, startSessionId);
  return inputs;
}

function buildMetaValueStreamInputs(metaName, query, language, queryOptions, limit, batch) {
  const inputs = buildEventStreamInputs(query, language, limit, batch);
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
 * @param {object} query The query object. @see investigate-events/state/query-definition
 * @param {object[]} language Array of meta key definitions. @see investigate-events/state/query
 * @public
 */
function makeServerInputsForQuery(query, language) {
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
      { field: 'query', value: nwEncodeMetaFilterConditions(get(metaFilter || {}, 'conditions'), language) }
    ]
  };
}

/**
 * Given an object representing a query, and a count threshold, computes the input parameters required to submit that
 * query's event count request to the server.
 * @param {object} query The query object. @see investigate-events/state/query
 * @param {object} language The language object. @see investigate-events/state/language
 * @param {number} [threshold] Optional precision limit. Counts will not go higher than this number.
 * @public
 */
function makeServerInputsForEventCount(query, language, threshold) {
  const out = makeServerInputsForQuery(query, language);
  if (threshold) {
    out.filter.push({ field: 'threshold', value: threshold });
  }
  return out;
}

function executeEventsRequest(request, inputs, events) {
  events.setProperties({
    status: 'streaming',
    reason: undefined
  });

  // let rendering for request about to go out happen
  // before request goes out
  run.next(function() {
    request.streamRequest({
      method: 'stream',
      modelName: 'core-event',
      query: inputs,
      onInit(stopStream) {
        events.set('stopStreaming', stopStream);
      },
      onResponse(response) {
        const arr = response && response.data;
        if (arr) {
          run.next(function() {
            arr.forEach(hasherizeEventMeta);
            const data = events.get('data');
            const goal = events.get('goal');
            data.pushObjects(arr);
            if (goal && data.length >= goal) {
              events.get('stopStreaming')();
            }
          });
        }
      },
      onError(response = {}) {
        const { code, meta: { message } = {} } = response;
        events.setProperties({
          status: 'error',
          reason: code,
          message
        });
      },
      onCompleted() {
        events.set('status', 'complete');
      },
      onStopped() {
        events.set('status', 'stopped');
      }
    });
  });
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
 * Given a Core service ID, computes the input parameters required to submit that request info about that service.
 * For example, a list of meta keys, a hashtable of meta value aliases, etc.
 * @param {string|number} endpointId The ID of the Core service whose info is to be queried.
 * @public
 */
function makeServerInputsForEndpointInfo(endpointId) {
  assert(
    'Cannot make a core query without a service id.',
    !isBlank(endpointId)
  );

  return {
    filter: [
      { field: 'endpointId', value: endpointId }
    ]
  };
}

function buildEventLogStreamInputs(endpointId, eventIds = []) {
  assert(
    'Cannot make a core log query without an event id.',
    eventIds.length
  );
  const inputs = makeServerInputsForEndpointInfo(endpointId);
  inputs.filter.pushObject({ field: 'sessionIds', values: eventIds });
  return inputs;
}

function executeLogDataRequest(request, inputs, events = []) {
  return new RSVP.Promise((resolve, reject) => {

    // Mark the event objects as waiting.
    events.forEach((item) => {
      setEventLogDataStatus(item, 'wait');
    });

    request.streamRequest({
      method: 'stream',
      modelName: 'core-event-log',
      query: inputs,
      onResponse({ data: { sessionId, log, code } }) {

        // Each event (i.e., sessionId) gets its own response message with its own error code.
        const item = events.findBy('sessionId', sessionId);

        if (item) {
          if (code) {
            // Any non-zero code means there was an error.
            setEventLogDataStatus(item, 'rejected');
          } else {
            // No error, cache the log data into the event object itself.
            setEventLogData(item, log);
            setEventLogDataStatus(item, 'resolved');
          }
        }
      },
      onCompleted: resolve,
      onError() {
        // The request won't complete, so mark any events still pending as error.
        events
          .filter((item) => {
            return getEventLogDataStatus(item) === 'wait';
          })
          .forEach((item) => {
            setEventLogDataStatus(item, 'rejected');
          });
        reject();
      }
    });
  });
}

/**
 * Parses a given URI string that represents a filter for a Core Events query.
 * Assumes the URI is of the following syntax: `serviceId/startTime/endTime/metaFilterUri`.
 * The `metaFilter` piece is further parsed into an list of individual filter conditions (@see parseMetaFilterUri).
 * @param {string} uri The URI string.
 * @returns {{ serviceId: string, startTime: number, endTime: number, metaFilter: object }}
 * @public
 */
function parseEventQueryUri(uri) {
  const parts = uri ? uri.split('/') : [];
  const [ serviceId, startTime, endTime ] = parts;
  const metaFilterUri = parts.slice(3, parts.length).join('/');
  const metaFilterConditions = parseMetaFilterUri(metaFilterUri);

  return {
    serviceId,
    startTime: _floorAtMinute(startTime),
    endTime: _ceilAtMinute(endTime),
    metaFilter: {
      uri: metaFilterUri,
      conditions: metaFilterConditions
    }
  };
}

/**
 * Parses a given URI string component that represents 0, 1 or more meta conditions for a Core query.
 * Assumes the URI is of the following syntax: `key1=value1/key2=value2/../keyN=valueN`, where each `key#` string is
 * a meta key identifier (e.g., `ip.src`, not a display name), and each `value#` string is a meta value (raw, not alias).
 * Assumes `key#` strings do not need URI decoding (they're just alphanumerics, plus dots maybe), but `value#` strings
 * will need URI decoding.
 * If any duplicate conditions are found, the duplicates are discarded; i.e., only 1 instance of the condition is
 * returned. This is done because the duplicate conditions don't have any net effect on the filter.
 * @param {string} uri
 * @returns {object[]} Array of condition objects. Each array item is an object with properties `key` & `value`, where:
 * (i) `key` is a meta key identifier (e.g., "ip.src", not a display name); and
 * (ii) value` is a meta key value (raw, not alias).
 * @private
 */
function parseMetaFilterUri(uri) {
  if (isBlank(uri)) {
    // When uri is empty, return empty array. Alas, ''.split() returns a non-empty array; it's a 1-item array with
    // an empty string in it, which is not what we want.  So we check for '' and return [] explicitly here.
    return [];
  }
  return uri.split('/')
    .filter((segment) => !!segment)
    .map((queryString) => {
      queryString = decodeURIComponent(queryString);
      const condition = { queryString };
      const matchPair = queryString.match(/([^\=]+)\=(.*)/);
      const matchAndOr = queryString.match(/\&\&|\|\|/);
      if (matchPair && !matchAndOr) {
        const [ , key, value ] = matchPair;
        condition.isKeyValuePair = true;
        condition.key = key;
        condition.value = value;
      }
      return condition;
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
  const metaFilterUri = uriEncodeMetaFilterConditions(conditions);

  return [ serviceId, startTime, endTime, metaFilterUri ].join('/');
}

/**
 * Encodes a given list of meta conditions into a URI string component that can be used for routing.
 * The reverse of `parseMetaFilterUri()`.
 * @param {object[]} conditions The array of meta conditions. For structure, @see return value of parseMetaFilterUri.
 * @returns {string}
 * @private
 */
function uriEncodeMetaFilterConditions(conditions = []) {
  return conditions
    .map((condition) => {
      return condition.isKeyValuePair ?
        `${condition.key}=${encodeURIComponent(condition.value)}` :
        encodeURIComponent(condition.queryString);
    })
    .join('/');
}

/**
 * Encodes a given list of meta conditions into a "where clause" string that can be used by NetWitness Core.
 * @param {object[]} conditions The array of meta conditions. For structure, @see return value of parseMetaFilterUri.
 * @param {object[]} language Array of meta key definitions form the NetWitness Core endpoint.  This is used for
 * checking the data types of meta keys, which is needed when deciding whether to wrap values in quotes.
 * @returns {string}
 * @public
 */
function nwEncodeMetaFilterConditions(conditions = [], language) {
  return conditions
    .map((condition) => {
      const { queryString, isKeyValuePair, key, value } = condition;
      if (isKeyValuePair) {
        const keyDefinition = language.findBy('metaName', key);
        const useQuotes = String(get(keyDefinition || {}, 'format')).toLowerCase() === 'text';
        const valueEncoded = useQuotes ? `'${String(value).replace(/[\'\"]/g, '')}'` : value;
        return `${key}=${valueEncoded}`;
      } else {
        return queryString;
      }
    })
    .join(' && ');
}

export {
  buildEventStreamInputs,
  makeServerInputsForQuery,
  makeServerInputsForEventCount,
  executeEventsRequest,
  buildMetaValueStreamInputs,
  executeMetaValuesRequest,
  makeServerInputsForEndpointInfo,
  parseEventQueryUri,
  uriEncodeEventQuery,
  nwEncodeMetaFilterConditions,
  buildEventLogStreamInputs,
  executeLogDataRequest
};
