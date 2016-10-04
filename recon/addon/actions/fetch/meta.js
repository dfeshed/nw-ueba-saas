import { promiseRequest } from 'streaming-data/services/data-access/requests';
import {
  addStreaming,
  addSessionQueryFilter,
  endpointFilter,
  addCatchAllTimeRange
} from './util/query-util';

const fetchMeta = ({ endpointId, eventId }) => {
  let query = endpointFilter(endpointId);
  query = addStreaming(query);
  query = addSessionQueryFilter(query, eventId);
  query = addCatchAllTimeRange(query);

  return promiseRequest({
    method: 'stream',
    modelName: 'core-event',
    query
  });
};

export default fetchMeta;