import { findAllPromiseRequest } from '../util/query-util';

/**
 * Fetch all of the services to which we're connected.
 * @return {object} RSVP Promise
 * @public
 */
export const fetchServices = () => findAllPromiseRequest('core-service', {});
