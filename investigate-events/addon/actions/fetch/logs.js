import {
  sessionIdsFilter,
  serviceIdFilter,
  streamPromiseRequest
} from '../util/query-util';

/**
 * Fetch a single log of a log event.
 * @param {string|number} serviceId
 * @param {object[]} sessionIds
 * @return {object} RSVP Promise
 * @public
 */
export function fetchLog(serviceId, sessionIds = []) {
  const query = {
    filter: [
      serviceIdFilter(serviceId),
      sessionIdsFilter(sessionIds)
    ]
  };

  return streamPromiseRequest(
    'core-event-log',
    query
  );
}