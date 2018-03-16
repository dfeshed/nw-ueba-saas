import { lookup } from 'ember-dependency-lookup';
import { buildBaseQuery, addStreaming } from 'recon/actions/util/query-util';
import {
  batchDataHandler,
  HANDLERS,
  BATCH_TYPES
} from 'recon/actions/util/batch-data-handler';

const BATCH_CHARACTER_SIZE = 5000;
let TIME_BETWEEN_BATCHES = [3000, 500];

// gimp IE along since it renders slowly
// first packets render right away so take longer
// rest just render packet frame so can speed up
const IS_IE = !!window.document.documentMode;
if (IS_IE) {
  TIME_BETWEEN_BATCHES = [5000, 850];
}

// The data, once in memory, is much larger because it has already been processed
// and enriched, so the BULK character size, to match the BATCH_CHARACTER_SIZE
// needs to be approx 50-100X
const BULK_BATCH_CHARACTER_SIZE = 400000;

export const fetchPacketData = (
  { endpointId, eventId, packetsPageSize, packetsRowIndex },
  dispatchData,
  dispatchBatch,
  dispatchError
) => {
  const request = lookup('service:request');
  const basicQuery = buildBaseQuery(endpointId, eventId);
  const streamingQuery = addStreaming(basicQuery, packetsPageSize, undefined, undefined, packetsRowIndex);
  request.streamRequest({
    method: 'stream',
    modelName: 'reconstruction-packet-data',
    query: streamingQuery,
    streamOptions: {
      cancelPreviouslyExecuting: true // can only have one event in recon at a time
    },
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
