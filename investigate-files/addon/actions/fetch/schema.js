import { lookup } from 'ember-dependency-lookup';

/**
 * Retrieves all available schema for global module from server.
 * @returns Promise that will resolve with the server response.
 * @public
 */
const fetchSchema = () => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'schema',
    modelName: 'files',
    query: {}
  });
};

export default { fetchSchema };
