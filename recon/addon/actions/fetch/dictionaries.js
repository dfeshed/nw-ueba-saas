import { buildBaseQuery } from './util/query-util';
import { promiseRequest } from 'streaming-data/services/data-access/requests';

const fetchLanguage = ({ endpointId, eventId }) => {
  const query = buildBaseQuery(endpointId, eventId);
  return promiseRequest({
    method: 'query',
    modelName: 'core-meta-key',
    query
  });
};

const fetchAliases = ({ endpointId, eventId }) => {
  const query = buildBaseQuery(endpointId, eventId);
  return promiseRequest({
    method: 'query',
    modelName: 'core-meta-alias',
    query
  });
};

export {
  fetchLanguage,
  fetchAliases
};