import { streamRequest } from 'streaming-data/services/data-access/requests';
import { buildBaseQuery, addStreaming, addDecode, addMaxPackets } from '../util/query-util';
import { batchDataHandler, HANDLERS, BATCH_TYPES } from 'recon/actions/util/batch-data-handler';

const BATCH_CHARACTER_SIZE = 20000;
const TIME_BETWEEN_BATCHES = [500];

const TEXT_BATCH_SIZE = 50;

const selector = (response) => {
  if (response.data && response.data.length > 0) {
    return response.data;
  }
  return null;
};

export const fetchTextData = (
  { endpointId, eventId, decode, maxPacketsForText },
  dispatchData,
  dispatchBatch,
  dispatchError
) => {
  const basicQuery = buildBaseQuery(endpointId, eventId);
  const streamingQuery = addStreaming(basicQuery, undefined, TEXT_BATCH_SIZE);
  const maxPacketsQuery = addMaxPackets(streamingQuery, maxPacketsForText);
  const decodeQuery = addDecode(maxPacketsQuery, decode);
  streamRequest({
    method: 'stream',
    modelName: 'reconstruction-text-data',
    query: decodeQuery,
    streamOptions: {
      cancelPreviouslyExecuting: true // can only have one event's text at a time
    },
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
