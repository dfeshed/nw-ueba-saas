import { lookup } from 'ember-dependency-lookup';

/**
 * Executes a websocket fetch call for endpoint servers and returns a Promise.
 *
 * @method fetchEndpointServers
 * @public
 * @returns {Promise}
 */
export const fetchEndpointServers = () => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'findAll',
    modelName: 'endpoint-server',
    query: {}
  });
};