import { streamRequest } from 'streaming-data/services/data-access/requests';
import {
  buildBaseQuery,
  addStreaming,
  addDecode
} from './util/query-util';
import { timedBatchResponse } from './util/execute-util';

const BATCH_SIZE = 10;
const TIME_BETWEEN_BATCHES = 800;

const selector = (response) => {
  if (response.data && response.data.length > 0) {
    return response.data[0];
  }
  return [];
};

const fetchTextData = ({ endpointId, eventId, packetsPageSize, decode }, dispatchPage, dispatchError) => {
  const basicQuery = buildBaseQuery(endpointId, eventId);
  const streamingQuery = addStreaming(basicQuery, packetsPageSize);
  const decodeQuery = addDecode(streamingQuery, decode);
  streamRequest({
    method: 'stream',
    modelName: 'reconstruction-text-data',
    query: decodeQuery,
    onResponse: timedBatchResponse(dispatchPage, selector, BATCH_SIZE, TIME_BETWEEN_BATCHES),
    onError: dispatchError
  });
};

export default fetchTextData;
