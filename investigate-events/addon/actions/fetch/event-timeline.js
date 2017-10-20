import {
  conditionsFilter,
  encodeMetaFilterConditions,
  queryPromiseRequest,
  serviceIdFilter,
  timeRangeFilter
} from './utils';

/**
 * Fetch the number of event results for a given query.
 * @param {string|number} serviceId Id of the service
 * @param {number} startTime
 * @param {number} endTime
 * @param {object[]} conditions
 * @param {object[]} language
 * @return {object} RSVP Promise
 * @public
 */
export default function fetchTimeline(serviceId, startTime, endTime, conditions, language) {
  const query = {
    filter: [
      serviceIdFilter(serviceId),
      timeRangeFilter(startTime, endTime),
      conditionsFilter(encodeMetaFilterConditions(conditions, language))
    ]
  };
  return queryPromiseRequest(
    'core-event-timeline',
    query,
    { cancelPreviouslyExecuting: true }
  );
}
