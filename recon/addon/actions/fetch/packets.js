import { streamRequest } from 'streaming-data/services/data-access/requests';
import { buildBaseQuery, addStreaming } from 'recon/actions/util/query-util';
import { timedBatchResponse, HANDLERS, BATCH_TYPES } from 'recon/actions/util/execute-util';

const BATCH_CHARACTER_SIZE = 5000;
const TIME_BETWEEN_BATCHES = 500;

const fetchPacketData = ({ endpointId, eventId, packetsPageSize }, dispatchPage, dispatchError) => {
  const basicQuery = buildBaseQuery(endpointId, eventId);
  const streamingQuery = addStreaming(basicQuery, packetsPageSize);
  streamRequest({
    method: 'stream',
    modelName: 'reconstruction-packet-data',
    query: streamingQuery,
    onResponse: timedBatchResponse({
      dataHandler: HANDLERS.API,
      batchType: BATCH_TYPES.PACKET,
      batchCallback: dispatchPage,
      selector: (response) => response.data,
      batchSize: BATCH_CHARACTER_SIZE,
      batchGapTime: TIME_BETWEEN_BATCHES
    }),
    onError: dispatchError
  });
};

export default fetchPacketData;
