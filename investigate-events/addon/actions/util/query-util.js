import { promiseRequest } from 'streaming-data/services/data-access/requests';

// Generic function that returns an Array of filters with a new key/value pair.
const _addFilter = (field, value, filters) => filters.concat({ field, value });

/**
 * Creates an endpointId filter
 * @param {string|number} value The Id of the endpoint
 * @param {object[]} filters List of filters to augment
 * @return {object[]} New list of filters
 * @public
 */
export const endpointIdFilter = (value, filters) => _addFilter('endpointId', value, filters);

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
 * @param {number} endpointId EndpointId
 * @param {number} eventId EventId
 * @param {string} modelName Name of model
 * @param {object} streamOptions Optional stream params
 * @return {object} An RSVP Promise
 * @public
 */
export const findAllPromiseRequest = (modelName, query, streamOptions = {}) => {
  return promiseRequest({
    method: 'findAll',
    modelName,
    query,
    streamOptions
  });
};

/**
 * Creates a Promise request with its "method" set to `query`,
 * @param {number} endpointId EndpointId
 * @param {number} eventId EventId
 * @param {string} modelName Name of model
 * @param {object} streamOptions Optional stream params
 * @return {object} An RSVP Promise
 * @public
 */
export const queryPromiseRequest = (modelName, query, streamOptions = {}) => {
  return promiseRequest({
    method: 'query',
    modelName,
    query,
    streamOptions
  });
};
