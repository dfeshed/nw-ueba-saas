import { promiseRequest } from 'streaming-data/services/data-access/requests';
import { buildBaseQuery } from './util/query-util';

const fetchReconFiles = ({ endpointId, eventId }) => {
  return promiseRequest({
    method: 'query',
    modelName: 'reconstruction-file-data',
    query: buildBaseQuery(endpointId, eventId)
  });
};

export default fetchReconFiles;

