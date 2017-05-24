import { streamRequest } from 'streaming-data/services/data-access/requests';
import { buildBaseQuery, addStreaming, addDecode } from '../util/query-util';
import { batchDataHandler, HANDLERS, BATCH_TYPES } from 'recon/actions/util/batch-data-handler';

const BATCH_CHARACTER_SIZE = 10000;
const TIME_BETWEEN_BATCHES = 500;

const selector = (response) => {
  if (response.data && response.data.length > 0) {
    return response.data[0];
  }
  return null;
};

export const fetchTextData = (
  { endpointId, eventId, packetsPageSize, decode },
  dispatchData,
  dispatchBatch,
  dispatchError
) => {
  const basicQuery = buildBaseQuery(endpointId, eventId);
  const streamingQuery = addStreaming(basicQuery, packetsPageSize);
  const decodeQuery = addDecode(streamingQuery, decode);
  streamRequest({
    method: 'stream',
    modelName: 'reconstruction-text-data',
    query: decodeQuery,
    onResponse: batchDataHandler({
      dataHandler: HANDLERS.socketResponse(selector, dispatchData),
      batchType: BATCH_TYPES.TEXT,
      batchCallback: dispatchBatch,
      batchSize: BATCH_CHARACTER_SIZE,
      batchGapTime: TIME_BETWEEN_BATCHES
    }),
    onError: dispatchError
  });
};

// Takes data provided and dispatchs it in batches
export const batchTextData = (data, dispatchBatch) => {
  const provideData = batchDataHandler({
    dataHandler: HANDLERS.bulk(),
    batchType: BATCH_TYPES.TEXT,
    batchCallback: dispatchBatch,
    batchSize: BATCH_CHARACTER_SIZE,
    batchGapTime: TIME_BETWEEN_BATCHES
  });

  provideData(data);
};
