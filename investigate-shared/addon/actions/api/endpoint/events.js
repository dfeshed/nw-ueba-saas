import { lookup } from 'ember-dependency-lookup';

/**
 * Creates a metaFilter conditions filter
 * @param {string} value - A query string
 * @return {object} A field/value object
 * @public
 */
const conditionsFilter = (value) => ({ field: 'query', value });

/**
 * Creates a serviceId filter
 * @param {string|number} value - The Id of the service
 * @return {object} A field/value object
 * @public
 */
const serviceIdFilter = (value) => ({ field: 'endpointId', value });

/**
 * Creates a time range filter
 * @param {number} startTime - The beginning date
 * @param {number} endTime - The ending date
 * @return {object} A field/range object
 * @public
 */
const timeRangeFilter = (startTime, endTime) => ({
  field: 'timeRange',
  range: {
    from: startTime,
    to: endTime
  }
});

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
const streamingRequest = (modelName, query = {}, handlers = {}, streamOptions = {}) => {
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
 * NetWitness Core endpoint.  This is used for checking the data types of meta
 * keys, which is needed when deciding whether to wrap values in quotes.
 * @returns {string}
 * @public
 */
const encodeMetaFilterConditions = (conditions = []) => {
  return conditions
    .map((condition) => {
      const { meta, value, operator } = condition;
      return `${(meta) ? meta : ''}${(operator) ? operator : ''}${(value) ? value : ''}`;
    })
    .join(' && ');
};

/**
 * Fetch the number of event results for a given query.
 * @param {object} queryNode
 * @param {number} limit - The stream limit
 * @param {number} batch - The stream batch size
 * @param {object} handlers - Stream event handlers
 * @return {object} RSVP Promise
 * @public
 */
export default function(queryNode, limit, batch, handlers) {
  const query = {
    filter: [
      serviceIdFilter(queryNode.serviceId),
      timeRangeFilter(queryNode.startTime, queryNode.endTime),
      conditionsFilter(encodeMetaFilterConditions(queryNode.metaFilter.conditions))
    ],
    stream: { limit, batch }
  };
  return streamingRequest(
    'core-event',
    query,
    handlers
  );
}
