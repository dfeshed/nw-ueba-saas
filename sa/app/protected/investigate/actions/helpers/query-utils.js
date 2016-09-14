import Ember from 'ember';
import hasherizeEventMeta from './hasherize-event-meta';

const {
  assert,
  getProperties,
  RSVP
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

function buildMetaValueStreamInputs(metaName, query, queryOptions, limit, batch) {
  let inputs = buildEventStreamInputs(query, limit, batch);
  inputs.filter.pushObject({ field: 'metaName', value: metaName });
  if (queryOptions) {
    let { size, metric, sortField, sortOrder } = getProperties(queryOptions, 'size', 'metric', 'sortField', 'sortOrder');
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

/**
 * Given an object representing a query, and a count threshold, computes the input parameters required to submit that
 * query's event count request to the server.
 * @param {object} query The query object. @see investigate/state/query
 * @param {number} [threshold] Optional precision limit. Counts will not go higher than this number.
 * @public
 */
function makeServerInputsForEventCount(query, threshold) {
  let out = makeServerInputsForQuery(query);
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
      events.set('status', 'stopped');
    }
  });
}

function executeMetaValuesRequest(request, inputs, values) {
  return new RSVP.Promise((resolve, reject) => {
    values.setProperties({
      data: [],
      status: 'streaming',
      reason: undefined
    });

    request.streamRequest({
      method: 'stream',
      modelName: 'core-meta-value',
      query: inputs,
      onInit(stopStream) {
        values.set('stopStreaming', stopStream);
      },
      onResponse(response) {
        if (!response) {
          return;
        }
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
}

/**
 * Given a Core service ID, computes the input parameters required to submit that request info about that service.
 * For example, a list of meta keys, a hashtable of meta value aliases, etc.
 * @param {string|number} endpointId The ID of the Core service whose info is to be queried.
 * @public
 */
function makeServerInputsForEndpointInfo(endpointId) {
  assert(
    endpointId,
    'Cannot make a core query without a service id.'
  );

  return {
    filter: [
      { field: 'endpointId', value: endpointId }
    ]
  };
}

export {
  buildEventStreamInputs,
  makeServerInputsForQuery,
  makeServerInputsForEventCount,
  executeEventsRequest,
  buildMetaValueStreamInputs,
  executeMetaValuesRequest,
  makeServerInputsForEndpointInfo
};
