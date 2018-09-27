import {
  conditionsFilter,
  encodeMetaFilterConditions,
  serviceIdFilter,
  streamingRequest,
  thresholdFilter,
  timeRangeFilter
} from './utils';

/**
 * Fetch the number of event results for a given query.
 * @param {string|number} serviceId Id of the service
 * @param {number} startTime
 * @param {number} endTime
 * @param {object[]} conditions
 * @param {object[]} language
 * @param {number} threshold
 * @return {object} RSVP Promise
 * @public
 */
export default function fetchCount(serviceId, startTime, endTime, conditions, language, threshold, handlers) {
  const query = {
    filter: [
      serviceIdFilter(serviceId),
      thresholdFilter(threshold),
      timeRangeFilter(startTime, endTime),
      conditionsFilter(encodeMetaFilterConditions(conditions, language))
    ]
  };
  return streamingRequest(
    'core-event-count',
    query,
    handlers
  );
}
