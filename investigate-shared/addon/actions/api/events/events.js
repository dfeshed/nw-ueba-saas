import {
  conditionsFilter,
  encodeMetaFilterConditions,
  extractSearchTermFromFilters,
  searchTermFilter,
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
export default function(queryNode, language, limit, batch, handlers, desiredMetas, dedicatedSocketName) {
  // conditions is legacy
  const filters = queryNode.metaFilter.conditions || queryNode.metaFilter;
  const { metaFilters, searchTerm } = extractSearchTermFromFilters(filters);
  const query = {
    filter: [
      serviceIdFilter(queryNode.serviceId),
      timeRangeFilter(queryNode.startTime, queryNode.endTime),
      conditionsFilter(encodeMetaFilterConditions(metaFilters, language)),
      searchTermFilter(searchTerm)
    ],
    stream: { limit, batch }
  };

  if (desiredMetas) {
    query.filter.push({
      field: 'select',
      value: desiredMetas.join(',')
    });
  }

  const streamOptions = {};
  if (dedicatedSocketName) {
    streamOptions.dedicatedSocketName = dedicatedSocketName;
  }

  return streamingRequest(
    'core-event',
    query,
    handlers,
    streamOptions
  );
}

