import { lookup } from 'ember-dependency-lookup';
import { buildBaseQuery, addStreaming } from 'recon/actions/util/query-util';
import { batchDataHandler, HANDLERS, BATCH_TYPES } from 'recon/actions/util/batch-data-handler';
import { addEmail } from '../util/query-util';

const BATCH_CHARACTER_SIZE = 10000;
const TIME_BETWEEN_BATCHES = [400];
const EMAIL_BATCH_SIZE = 50;

export const fetchEmailData = (
  { endpointId, eventId, email },
  dispatchData,
  dispatchBatch,
  dispatchError
) => {
  const request = lookup('service:request');
  const basicQuery = buildBaseQuery(endpointId, eventId);
  const streamingQuery = addStreaming(basicQuery, undefined, EMAIL_BATCH_SIZE);
  const emailQuery = addEmail(streamingQuery, email);

  request.pagedStreamRequest({
    method: 'stream',
    modelName: 'reconstruction-email-data',
    query: emailQuery,
    streamOptions: {
      cancelPreviouslyExecuting: true
    },
    onResponse: batchDataHandler({
      dataHandler: HANDLERS.socketResponse((response) => {
        if (response.data && response.data.length > 0) {
          return response.data;
        }
        return null;
      }, dispatchData),
      batchType: BATCH_TYPES.MAIL,
      batchCallback: dispatchBatch,
      batchSize: BATCH_CHARACTER_SIZE,
      batchGapTime: TIME_BETWEEN_BATCHES
    }),
    onError: dispatchError
  });
};


// Takes data provided and dispatches it in batches
export const batchEmailData = (data, dispatchBatch) => {
  const provideData = batchDataHandler({
    dataHandler: HANDLERS.bulk(),
    batchType: BATCH_TYPES.MAIL,
    batchCallback: dispatchBatch,
    batchSize: BATCH_CHARACTER_SIZE,
    batchGapTime: TIME_BETWEEN_BATCHES
  });
  provideData(data);
};