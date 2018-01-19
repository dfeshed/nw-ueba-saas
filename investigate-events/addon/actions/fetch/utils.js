/**
 * If you've come here from a deprecation warning, look at
 * `findAllPromiseRequest()`/`streamingRequest()` for the proper way to
 * initiate `promiseRequest`/`streamRequest`.
 * @public
 */
import { lookup } from 'ember-dependency-lookup';

/**
 * Creates a metaFilter conditions filter
 * @param {string} value - A query string
 * @return {object} A field/value object
 * @public
 */
export const conditionsFilter = (value) => ({ field: 'query', value });

/**
 * Creates a serviceId filter
 * @param {string|number} value - The Id of the service
 * @return {object} A field/value object
 * @public
 */
export const serviceIdFilter = (value) => ({ field: 'endpointId', value });

/**
 * Creates a sessionId filter
 * @param {number} value - The Id of the session
 * @return {object} A field/value object
 * @public
 */
export const sessionIdFilter = (value) => ({ field: 'sessionId', value });

/**
 * Creates a sessionIds filter
 * @param {object[]} values - List of session Ids
 * @return {object} A field/values object
 * @public
 */
export const sessionIdsFilter = (values) => ({ field: 'sessionIds', values });

/**
 * Creates a threshold filter
 * @param {number} value - The threshold value
 * @return {object} A field/value object
 * @public
 */
export const thresholdFilter = (value) => ({ field: 'threshold', value });

/**
 * Creates a time range filter
 * @param {number} startTime - The beginning date
 * @param {number} endTime - The ending date
 * @return {object} A field/range object
 * @public
 */
export const timeRangeFilter = (startTime, endTime) => ({
  field: 'timeRange',
  range: {
    from: startTime,
    to: endTime
  }
});

/**
 * Creates a Promise request with its "method" set to `findAll`,
 * @param {string} modelName - Name of model
 * @param {object} query - (Optional) Query params for request
 * @param {object} streamOptions - (Optional) Stream params
 * @return {object} An RSVP Promise
 * @public
 */
export const findAllPromiseRequest = (modelName, query = {}, streamOptions = {}) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'findAll',
    modelName,
    query,
    streamOptions
  });
};

/**
 * Creates a Promise request with its "method" set to `query`,
 * @param {string} modelName - Name of model
 * @param {object} query - (Optional) Query params for request
 * @param {object} streamOptions - (Optional) Stream params
 * @return {object} An RSVP Promise
 * @public
 */
export const queryPromiseRequest = (modelName, query = {}, streamOptions = {}) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'query',
    modelName,
    query,
    streamOptions
  });
};

/**
 * Creates a Promise request with its "method" set to `stream`,
 * @param {string} modelName - Name of model
 * @param {object} query - (Optional) Query params for request
 * @param {object} streamOptions - (Optional) Stream params
 * @return {object} An RSVP Promise
 * @public
 */
export const streamPromiseRequest = (modelName, query = {}, streamOptions = {}) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'stream',
    modelName,
    query,
    streamOptions
  });
};

/**
 * Creates a Stream request with its "method" set to `stream`,
 * @param {string} modelName - Name of model
 * @param {object} handlers - (Optional) Callbacks to handle stream lifecycle
 * events
 * @param {object} query - (Optional) Query params for request
 * @param {object} streamOptions - (Optional) Stream params
 * @return {null}
 * @public
 */
export const streamingRequest = (modelName, query = {}, handlers = {}, streamOptions = {}) => {
  const request = lookup('service:request');
  request.streamRequest({
    method: 'stream',
    modelName,
    query,
    streamOptions,
    ...handlers
  });
};

/**
 * Encodes a given list of meta conditions into a "where clause" string that can
 * be used by NetWitness Core.
 * @param {object[]} conditions - The array of meta conditions.
 * @param {object[]} language - Array of meta key definitions form the
 * NetWitness Core endpoint.  This is used for checking the data types of meta
 * keys, which is needed when deciding whether to wrap values in quotes.
 * @returns {string}
 * @public
 */
export const encodeMetaFilterConditions = (conditions = []) => {
  return conditions
    .map((condition) => {
      const { meta, value, operator, complexFilter } = condition;

      if (complexFilter) {
        return complexFilter;
      } else {
        if (['contains', 'ends', 'begins'].includes(operator)) {
          return `${meta} ${operator} ${value}`;
        } else if (['!exists', 'exists'].includes(operator)) {
          return `${meta} ${operator}`;
        } else {
          return `${meta}${operator}${value}`;
        }
      }
    })
    .join(' && ');
};
