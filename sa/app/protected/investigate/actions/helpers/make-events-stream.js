import Ember from 'ember';
import makeServerInputsForQuery from './make-server-inputs-for-query';

const {
  $,
  assert
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
 * @param {object} store Reference to the Ember Data store.
 * @param {object} query Represents the Core query inputs (@see investigate/state/query)
 * @param {number} limit The maximum number of records to stream to the client.
 * @param {number} batch The maximum number of records to include in a single socket response message.
 * @param {string} [startSessionId] Optional lower bound (exclusive) for session IDs.
 * @returns {object} Newly created stream instance.
 * @public
 */
export default function(store, query, limit, batch = 1, startSessionId = null) {
  assert(store && $.isFunction(store.stream), 'Invalid store was given to makeStream().');

  let inputs = makeServerInputsForQuery(query, startSessionId);
  inputs.stream = { limit, batch };

  let metaFilterInput = inputs.filter.findBy('field', 'query');
  metaFilterInput.value = addSessionIdFilter(metaFilterInput.value, startSessionId);

  return store.stream('core-event', inputs);
}
