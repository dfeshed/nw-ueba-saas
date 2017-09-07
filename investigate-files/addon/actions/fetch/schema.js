import { promiseRequest } from 'streaming-data/services/data-access/requests';

/**
 * Retrieves all available schema for global module from server.
 * @returns Promise that will resolve with the server response.
 * @public
 */
const fetchSchema = () => {
  return promiseRequest({
    method: 'schema',
    modelName: 'files',
    query: {}
  });
};

export default { fetchSchema };
