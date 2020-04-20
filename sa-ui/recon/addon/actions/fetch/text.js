import { lookup } from 'ember-dependency-lookup';
import {
  buildBaseQuery,
  addStreaming,
  addDecode
} from '../util/query-util';
import {
  batchDataHandler,
  HANDLERS,
  BATCH_TYPES
} from 'recon/actions/util/batch-data-handler';

const BATCH_CHARACTER_SIZE = 15000;
const TIME_BETWEEN_BATCHES = [400];

const TEXT_BATCH_SIZE = 50;
let cursor;

export const fetchTextData = (
  { endpointId, eventId, decode },
  dispatchData,
  dispatchBatch,
  dispatchCursor,
  dispatchError
) => {
  const request = lookup('service:request');
  const basicQuery = buildBaseQuery(endpointId, eventId);
  const streamingQuery = addStreaming(basicQuery, undefined, TEXT_BATCH_SIZE);
  const decodeQuery = addDecode(streamingQuery, decode);

  cursor = request.pagedStreamRequest({
    method: 'stream',
    modelName: 'reconstruction-text-data',
    query: decodeQuery,
    streamOptions: {
      cancelPreviouslyExecuting: true // can only have one event's text at a time
    },
    onResponse: batchDataHandler({
      dataHandler: HANDLERS.socketResponse((response) => {
        if (response.data && response.data.length > 0) {
          // dispatch the cursor to state so that components/selectors can use it's properties like canFirst, canNext etc to perform UI actions.
          dispatchCursor(cursor);
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

// The below four functions directly call the methods on the cursor object that is returned by the streaming-data API. The client can paginate (keep calling cursor.next) until MT sends complete = true. At that point we set that page as the lastPage.
// Note that we keep getting a marker with the response, until complete = true
// However this marker is not exposed to the client, streaming-data takes care of markers and only exposes the cursor object.
export const cursorNext = () => {
  cursor.next();
};

export const cursorPrevious = () => {
  cursor.previous();
};

export const cursorLast = () => {
  cursor.last();
};

export const cursorFirst = () => {
  cursor.first();
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