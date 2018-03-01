import {
  conditionsFilter,
  queryPromiseRequest,
  serviceIdFilter
} from './utils';

/**
* Validate each query
* @param {string|number} serviceId Id of the service
* @param {String} filterString query string
* @return {object} RSVP Promise
* @public
*/

export default function validateQueryFragment(serviceId, filterString) {
  const query = {
    filter: [
      serviceIdFilter(serviceId),
      conditionsFilter(filterString)
    ]
  };
  return queryPromiseRequest(
    'core-query-validate',
    query,
    { cancelPreviouslyExecuting: true }
  );
}