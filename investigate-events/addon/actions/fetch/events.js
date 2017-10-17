import {
  conditionsFilter,
  nwEncodeMetaFilterConditions,
  serviceIdFilter,
  streamingRequest,
  timeRangeFilter
} from '../util/query-util';

/**
 * Fetch the number of event results for a given query.
 * @param {object} queryNode
 * @param {object[]} language
 * @param {number} limit - The stream limit
 * @param {number} batch - The stream batch size
 * @param {object} handlers - Stream event handlers
 * @param {number} startSessionId - (Optional) SessionId from which to start query
 * @return {object} RSVP Promise
 * @public
 */
export default function fetchStreamingEvents(queryNode, language, limit, batch, handlers, startSessionId = null) {
  const query = {
    filter: [
      serviceIdFilter(queryNode.serviceId),
      timeRangeFilter(queryNode.startTime, queryNode.endTime),
      conditionsFilter(nwEncodeMetaFilterConditions(queryNode.metaFilter.conditions, language))
    ],
    stream: { limit, batch }
  };
  const metaFilterInput = query.filter.find((el) => el.field === 'query');
  metaFilterInput.value = _addSessionIdFilter(metaFilterInput.value, startSessionId);

  return streamingRequest(
    'core-event',
    query,
    handlers
  );
}

/**
 * Prepends a query string that will filter results based on a starting
 * sessionId
 * @param {string} filter - A string of filter conditions
 * @param {number} startSessionId
 * @private
 */
const _addSessionIdFilter = (filter, startSessionId) => {
  const out = [];
  if (startSessionId) {
    out.push(`(sessionid > ${startSessionId})`);
  }
  if (filter) {
    out.push(filter);
  }
  return out.join(' && ');
};
