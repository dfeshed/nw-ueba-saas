import {
  conditionsFilter,
  encodeMetaFilterConditions,
  serviceIdFilter,
  streamingRequest,
  timeRangeFilter,
  addSessionIdFilter
} from './utils';

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
export default function(queryNode, language, limit, batch, handlers, startSessionId = null) {
  // conditions is legacy
  const filters = queryNode.metaFilter.conditions || queryNode.metaFilter;
  const query = {
    filter: [
      serviceIdFilter(queryNode.serviceId),
      timeRangeFilter(queryNode.startTime, queryNode.endTime),
      conditionsFilter(encodeMetaFilterConditions(filters, language))
    ],
    stream: { limit, batch }
  };
  const metaFilterInput = query.filter.find((el) => el.field === 'query');
  metaFilterInput.value = addSessionIdFilter(metaFilterInput.value, startSessionId);

  return streamingRequest(
    'core-event',
    query,
    handlers
  );
}

