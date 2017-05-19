import { streamRequest } from 'streaming-data/services/data-access/requests';
import { buildBaseQuery, addStreaming } from 'recon/actions/util/query-util';
import { timedBatchResponse, BATCH_TYPES } from 'recon/actions/util/execute-util';

const BATCH_CHARACTER_SIZE = 5000;
const TIME_BETWEEN_BATCHES = 500;

const fetchPacketData = ({ endpointId, eventId, packetsPageSize }, dispatchPage, dispatchError) => {
  const basicQuery = buildBaseQuery(endpointId, eventId);
  const streamingQuery = addStreaming(basicQuery, packetsPageSize);
  streamRequest({
    method: 'stream',
    modelName: 'reconstruction-packet-data',
    query: streamingQuery,
    onResponse: timedBatchResponse(
      BATCH_TYPES.PACKET,
      dispatchPage,
      (response) => response.data,
      BATCH_CHARACTER_SIZE,
      TIME_BETWEEN_BATCHES
    ),
    onError: dispatchError
  });
};

export default fetchPacketData;
