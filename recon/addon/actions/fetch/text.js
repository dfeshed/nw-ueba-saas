import { streamRequest } from 'streaming-data/services/data-access/requests';
import {
  buildBaseQuery,
  addStreaming,
  addDecode
} from './util/query-util';
import { delayedResponse } from './util/execute-util';

const fetchTextData = ({ endpointId, eventId, packetsPageSize, decode }, dispatchPage, dispatchError) => {
  const basicQuery = buildBaseQuery(endpointId, eventId);
  const streamingQuery = addStreaming(basicQuery, packetsPageSize);
  const decodeQuery = addDecode(streamingQuery, decode);
  streamRequest({
    method: 'stream',
    modelName: 'reconstruction-text-data',
    query: decodeQuery,
    onResponse: delayedResponse(dispatchPage, (response) => response.data),
    onError: dispatchError
  });
};

export default fetchTextData;
