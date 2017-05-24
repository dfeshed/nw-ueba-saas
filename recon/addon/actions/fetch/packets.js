import { streamRequest } from 'streaming-data/services/data-access/requests';
import { buildBaseQuery, addStreaming } from 'recon/actions/util/query-util';
import { batchDataHandler, HANDLERS, BATCH_TYPES } from 'recon/actions/util/batch-data-handler';

const BATCH_CHARACTER_SIZE = 7500;
const TIME_BETWEEN_BATCHES = 500;

// The data, once in memory, is much larger because it has already been processed
// and enriched, so the BULK character size, to match the BATCH_CHARACTER_SIZE
// needs to be approx 50-100X
const BULK_BATCH_CHARACTER_SIZE = 400000;

export const fetchPacketData = (
  { endpointId, eventId, packetsPageSize },
  dispatchData,
  dispatchBatch,
  dispatchError
) => {
  const basicQuery = buildBaseQuery(endpointId, eventId);
  const streamingQuery = addStreaming(basicQuery, packetsPageSize);
  streamRequest({
    method: 'stream',
    modelName: 'reconstruction-packet-data',
    query: streamingQuery,
    onResponse: batchDataHandler({
      dataHandler: HANDLERS.socketResponse((response) => response.data, dispatchData),
      batchType: BATCH_TYPES.PACKET,
      batchCallback: dispatchBatch,
      batchSize: BATCH_CHARACTER_SIZE,
      batchGapTime: TIME_BETWEEN_BATCHES
    }),
    onError: dispatchError
  });
};

// Takes data provided and dispatchs it in batches
export const batchPacketData = (data, dispatchBatch) => {
  const provideData = batchDataHandler({
    dataHandler: HANDLERS.bulk(),
    batchType: BATCH_TYPES.PACKET,
    batchCallback: dispatchBatch,
    batchSize: BULK_BATCH_CHARACTER_SIZE,
    batchGapTime: TIME_BETWEEN_BATCHES
  });

  provideData(data);
};
