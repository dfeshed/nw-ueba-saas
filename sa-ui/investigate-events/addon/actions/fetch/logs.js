import {
  sessionIdsFilter,
  serviceIdFilter,
  streamingRequest
} from 'investigate-shared/actions/api/events/utils';

/**
 * Fetch a single log of a log event.
 * @param {string|number} serviceId
 * @param {object[]} sessionIds
 * @param {object} handlers - Stream event handlers
 * @return {null}
 * @public
 */
export function fetchLog(serviceId, sessionIds = [], handlers = {}) {
  const query = {
    filter: [
      serviceIdFilter(serviceId),
      sessionIdsFilter(sessionIds)
    ]
  };

  streamingRequest(
    'core-event-log',
    query,
    handlers
  );
}
