import {
  queryPromiseRequest,
  serviceIdFilter
} from 'investigate-shared/actions/api/events/utils';

/**
 * Fetch the language for a given service.
 * @param {string|number} serviceId Id of the service
 * @return {object} RSVP Promise
 * @public
 */
const fetchLanguage = (serviceId) => {
  const query = {
    filter: [
      serviceIdFilter(serviceId)
    ]
  };
  return queryPromiseRequest(
    'core-meta-key',
    query
  );
};

/**
 * Fetch the aliases for a given service.
 * @param {string|number} serviceId Id of the service
 * @return {object} RSVP Promise
 * @public
 */
const fetchAliases = (serviceId) => {
  const query = {
    filter: [
      serviceIdFilter(serviceId)
    ]
  };
  return queryPromiseRequest(
    'core-meta-alias',
    query
  );
};

export {
  fetchLanguage,
  fetchAliases
};
