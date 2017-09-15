import { queryPromiseRequest, endpointIdFilter } from '../util/query-util';

/**
 * Fetch the language for a given endpoint.
 * @param {string|number} endpointId Id of the endpoint
 * @return {object} RSVP Promise
 * @public
 */
const fetchLanguage = (endpointId) => {
  const query = {
    filter: endpointIdFilter(endpointId, [])
  };
  return queryPromiseRequest(
    'core-meta-key',
    query,
    { cancelPreviouslyExecuting: true }
  );
};

/**
 * Fetch the aliases for a given endpoint.
 * @param {string|number} endpointId Id of the endpoint
 * @return {object} RSVP Promise
 * @public
 */
const fetchAliases = (endpointId) => {
  const query = {
    filter: endpointIdFilter(endpointId, [])
  };
  return queryPromiseRequest(
    'core-meta-alias',
    query,
    { cancelPreviouslyExecuting: true }
  );
};

export {
  fetchLanguage,
  fetchAliases
};