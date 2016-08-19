import Ember from 'ember';
import hasherizeEventMeta from './hasherize-event-meta';

const {
  assert,
  getProperties
} = Ember;

// Adds a session id filter to a given Core query filter.
// Appends a condition for session id, but only if a session id is given.
// @param {string} filter A Core filter condition (typically for meta keys other than 'sessionid').
// @param {string} [startSessionId] Optional lower bound (exclusive) for session IDs.
function addSessionIdFilter(filter, startSessionId) {
  let out = [];
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
 * @param {object} query Represents the Core query inputs (@see investigate/state/query)
 * @param {number} limit The maximum number of records to stream to the client.
 * @param {number} batch The maximum number of records to include in a single socket response message.
 * @param {string} [startSessionId] Optional lower bound (exclusive) for session IDs.
 * @returns {object} Newly created stream instance.
 * @public
 */
function buildEventStreamInputs(query, limit, batch = 1, startSessionId = null) {
  let inputs = makeServerInputsForQuery(query, startSessionId);
  inputs.stream = { limit, batch };
  let metaFilterInput = inputs.filter.findBy('field', 'query');
  metaFilterInput.value = addSessionIdFilter(metaFilterInput.value, startSessionId);
  return inputs;
}

/**
 * Given an object representing a query, computes the input parameters required to submit that
 * query to the server.
 * @param {object} query The query object. @see investigate/state/query
 * @public
 */
function makeServerInputsForQuery(query) {
  let {
      serviceId, startTime, endTime, metaFilter
    } = getProperties(
      query || {}, 'serviceId', 'startTime', 'endTime', 'metaFilter'
    );

  assert(
    serviceId && startTime && endTime,
    'Cannot make a core query without a service id, start time & end time.'
  );

  return {
    filter: [
      { field: 'endpointId', value: serviceId },
      { field: 'timeRange', range: { from: startTime, to: endTime } },
      { field: 'query', value: metaFilter || '' }
    ]
  };
}

function executeEventsRequest(request, inputs, events) {
  events.setProperties({
    status: 'streaming',
    reason: undefined
  });

  request.streamRequest({
    method: 'stream',
    modelName: 'core-event',
    query: inputs,
    onInit(stopStream) {
      events.set('stopStreaming', stopStream);
    },
    onResponse(response) {
      let arr = response && response.data;
      if (arr) {
        arr.forEach(hasherizeEventMeta);
        const data = events.get('data');
        const goal = events.get('goal');
        data.pushObjects(arr);
        if (goal && data.length >= goal) {
          events.set('status', 'idle');
          events.get('stopStreaming')();
        }
      }
    },
    onError(response) {
      events.setProperties({
        status: 'error',
        reason: response && response.code
      });
    },
    onCompleted() {
      events.set('status', 'complete');
    },
    onStopped() {
      events.set('status', 'idle');
    }
  });
}

export {
  buildEventStreamInputs,
  makeServerInputsForQuery,
  executeEventsRequest
};
