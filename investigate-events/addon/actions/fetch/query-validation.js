import { queryPromiseRequest } from 'investigate-shared/actions/api/events/utils';

/**
* Validate each query
* @param {string|number} serviceId Id of the service
* @param {Array} filterStrings encoded query strings
* @return {object} RSVP Promise
* @public
*/
const validateQueries = (serviceId, filterArray) => {
  const query = {
    data: {
      endpointId: serviceId,
      queries: filterArray
    }
  };
  return queryPromiseRequest(
    'core-queries-validate',
    query,
    { cancelPreviouslyExecuting: false }
  );
};

export {
  validateQueries
};