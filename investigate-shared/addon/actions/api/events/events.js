import {
  conditionsFilter,
  encodeMetaFilterConditions,
  serviceIdFilter,
  streamingRequest,
  timeRangeFilter
} from './utils';

/**
 * Fetch the number of event results for a given query.
 * @param {object} queryNode
 * @param {object[]} language
 * @param {number} limit - The stream limit
 * @param {number} batch - The stream batch size
 * @param {object} handlers - Stream event handlers
 * @param {array} desiredMetas - (Optional) array of metas to return
 *   from query for each event
 * @return {object} RSVP Promise
 * @public
 */
export default function(queryNode, language, limit, batch, handlers, desiredMetas) {
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

  if (desiredMetas) {
    query.filter.push({
      field: 'select',
      value: desiredMetas.join(',')
    });
  }

  return streamingRequest(
    'core-event',
    query,
    handlers
  );
}

