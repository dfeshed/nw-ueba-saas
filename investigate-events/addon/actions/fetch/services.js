import {
  findAllPromiseRequest,
  queryPromiseRequest,
  serviceIdFilter
} from './utils';

/**
 * Fetch all of the services to which we're connected.
 * @return {object} RSVP Promise
 * @public
 */
export const fetchServices = () => findAllPromiseRequest('core-service');

/**
 * Fetch SDK summary for a given service.
 * @param {string|number} serviceId Id of the service
 * @return {object} RSVP Promise
 * @public
 */
export const fetchSummary = (serviceId) => {
  const query = {
    filter: [
      serviceIdFilter(serviceId)
    ]
  };
  return queryPromiseRequest(
    'core-summary',
    query,
    { cancelPreviouslyExecuting: true }
  );
};