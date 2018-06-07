import { lookup } from 'ember-dependency-lookup';
import {
  buildBaseQuery,
  addStreaming,
  addDecode,
  addMaxPackets
} from '../util/query-util';
import {
  batchDataHandler,
  HANDLERS,
  BATCH_TYPES
} from 'recon/actions/util/batch-data-handler';

const BATCH_CHARACTER_SIZE = 15000;
const TIME_BETWEEN_BATCHES = [400];

const TEXT_BATCH_SIZE = 50;

export const fetchTextData = (
  { endpointId, eventId, decode, maxPacketsForText },
  dispatchData,
  dispatchBatch,
  dispatchError
) => {
  const request = lookup('service:request');
  const basicQuery = buildBaseQuery(endpointId, eventId);
  const streamingQuery = addStreaming(basicQuery, undefined, TEXT_BATCH_SIZE);
  const maxPacketsQuery = addMaxPackets(streamingQuery, maxPacketsForText);
  const decodeQuery = addDecode(maxPacketsQuery, decode);

  const cursor = request.pagedStreamRequest({
    method: 'stream',
    modelName: 'reconstruction-text-data',
    query: decodeQuery,
    streamOptions: {
      cancelPreviouslyExecuting: true // can only have one event's text at a time
    },
    onResponse: batchDataHandler({
      dataHandler: HANDLERS.socketResponse((response) => {
        if (response.data && response.data.length > 0) {
          const packetProgress = response.meta['packet-progress'] || 0;
          // call cursor.next repeatedly until MT sends complete = true
          // Note that we keep getting a marker with the meta, until complete = true
          // limit of 2500 packets will be removed once pagination is in place
          if (packetProgress < 2500 && !response.meta.complete) {
            cursor.next();
          }
          return response.data;
        }
        return null;
      }, dispatchData),
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
