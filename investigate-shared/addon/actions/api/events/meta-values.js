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
export default function(queryNode, metaName, size, language, limit, batch, handlers, startSessionId = 1) {
  const filters = queryNode.metaFilter.conditions || queryNode.metaFilter;
  const query = {
    filter: [
      { field: 'metaName', value: `${metaName}` },
      { field: 'valuesCount', value: size },
      serviceIdFilter(queryNode.serviceId),
      timeRangeFilter(queryNode.startTime, queryNode.endTime),
      conditionsFilter(encodeMetaFilterConditions(filters, language))
    ],
    stream: { limit, batch }
  };
  const metaFilterInput = query.filter.find((el) => el.field === 'query');
  metaFilterInput.value = addSessionIdFilter(metaFilterInput.value, startSessionId);

  return streamingRequest(
    'core-meta-value',
    query,
    handlers
  );
}
