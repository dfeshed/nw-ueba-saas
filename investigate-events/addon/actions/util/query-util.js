import { promiseRequest } from 'streaming-data/services/data-access/requests';

// Generic function that returns an Array of filters with a new key/value pair.
const _addFilter = (field, value, filters) => filters.concat({ field, value });

/**
 * Creates an serviceId filter
 * @param {string|number} value The Id of the service
 * @param {object[]} filters List of filters to augment
 * @return {object[]} New list of filters
 * @public
 */
export const serviceIdFilter = (value, filters) => _addFilter('endpointId', value, filters);

/**
 * Creates a sessionId filter
 * @param {number} value The Id of the session
 * @param {object[]} filters List of filters to augment
 * @return {object[]} New list of filters
 * @public
 */
export const sessionIdFilter = (value, filters) => _addFilter('sessionId', value, filters);

/**
 * Creates a Promise request with its "method" set to `findAll`,
 * @param {string} modelName Name of model
 * @param {object} query Optional query params for request
 * @param {object} streamOptions Optional stream params
 * @return {object} An RSVP Promise
 * @public
 */
export const findAllPromiseRequest = (modelName, query = {}, streamOptions = {}) => {
  return promiseRequest({
    method: 'findAll',
    modelName,
    query,
    streamOptions
  });
};

/**
 * Creates a Promise request with its "method" set to `query`,
 * @param {string} modelName Name of model
 * @param {object} query Optional query params for request
 * @param {object} streamOptions Optional stream params
 * @return {object} An RSVP Promise
 * @public
 */
export const queryPromiseRequest = (modelName, query = {}, streamOptions = {}) => {
  return promiseRequest({
    method: 'query',
    modelName,
    query,
    streamOptions
  });
};
