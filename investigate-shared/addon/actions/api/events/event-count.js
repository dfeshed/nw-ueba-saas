import {
  conditionsFilter,
  encodeMetaFilterConditions,
  extractSearchTermFromFilters,
  searchTermFilter,
  serviceIdFilter,
  streamingRequest,
  thresholdFilter,
  timeRangeFilter,
  metaRangeFilter
} from './utils';

/**
 * Fetch the number of event results for a given query.
 * @param {string|number} serviceId Id of the service
 * @param {number} startTime
 * @param {number} endTime
 * @param {object[]} filters
 * @param {object[]} language
 * @param {number} threshold
 * @return {object} RSVP Promise
 * @public
 */
export default function fetchCount(serviceId, startTime, endTime, filters, language, threshold, handlers, startMeta, endMeta) {
  const { metaFilters, searchTerm } = extractSearchTermFromFilters(filters);
  const query = {
    filter: [
      serviceIdFilter(serviceId),
      thresholdFilter(threshold),
      timeRangeFilter(startTime, endTime),
      conditionsFilter(encodeMetaFilterConditions(metaFilters, language)),
      searchTermFilter(searchTerm)
    ]
  };

  if (startMeta && endMeta) {
    query.filter.push(metaRangeFilter(startMeta, endMeta));
  }

  return streamingRequest(
    'core-event-count',
    query,
    handlers
  );
}
