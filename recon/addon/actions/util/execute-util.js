import { next } from 'ember-runloop';

const BATCH_CHARACTER_SIZE = 10;
const WAIT = 100;

const batchCancellations = {};

export const BATCH_TYPES = {
  TEXT: 'TEXT',
  PACKET: 'PACKET'
};

/*
 * Slows down processing of large amounts of data by batching their processing (usually
 * into state) according to sizes and times provided. Via the api and memory handlers,
 * this can handle large amounts of data coming in over time via an API. Can also handle
 * a big array of data passed in all at once that needs to be broken down.
 *
 * Configuration Object
 * - dataHandler: either an apiDataHandler which knows how to handle responses from APIs
 *   or a memoryDataHandler which handles data already in memory
 * - batchType: one of `BATCH_TYPES`
 * - batchCallback: function to call to process a batch
 * - selector: OPTIONAL, used to find the data to be batched in an API response
 * - batchSize: OPTIONAL, defaults to BATCH_CHARACTER_SIZE, the size of each batch by
 *   character count of the items being batched after
 * - batchGapTime: OPTIONAL, defaults to WAIT, the time between batchCallback executions
 */
export const timedBatchResponse = ({
  dataHandler,
  batchType,
  batchCallback,
  selector,
  batchSize = BATCH_CHARACTER_SIZE,
  batchGapTime = WAIT
}) => {
  let done = false;
  let abort = false;
  const responsesQueue = [];

  // if a previous request exists for the same batchType
  // call the cancel function for that request so that it
  // stops batching
  if (batchCancellations[batchType]) {
    batchCancellations[batchType]();
  }

  // register cancellation callback for this batch request,
  // if another request comes in for same type
  // need to cancel previous request
  batchCancellations[batchType] = () => {
    abort = true;
  };

  const timeoutCallback = () => {
    // If this batch should be aborted, exit out of recursion
    if (abort) {
      return;
    }

    // If no more left, and the response has finished, exit out of recursion
    if (responsesQueue.length === 0 && done) {
      // ended gracefully, remove the cancellation callback
      if (batchCancellations[batchType]) {
        delete batchCancellations[batchType];
      }
      return;
    }

    // Not done, but responses queue is empty, no
    // reason to run a batch if that is the case
    const queueLength = responsesQueue.length;
    if (queueLength > 0) {
      let accum = 0;
      let index = 0;
      for (; index < queueLength; index++) {
        accum += responsesQueue[index][1];
        // have we accumulated enough data to reach the batch size?
        if (accum >= batchSize) {
          break;
        }
      }

      // remove items from the queue, those are the next batch, ship em out
      // need to grab first element from queue item as it contains data
      // console.log('SENDING BATCH: size:', accum, ', count:', index + 1);
      const nextBatch = responsesQueue.splice(0, index + 1).map((r) => r[0]);
      next(batchCallback, nextBatch);
    }

    processBatch(batchGapTime);
  };

  const processBatch = (time) => {
    // first time we want to run at time 0
    setTimeout(timeoutCallback, time || 0);
  };

  processBatch();

  return dataHandler(
    responsesQueue,
    selector,
    batchCallback,
    () => abort,
    () => done = true);
};

const _memoryDataHandler = (responsesQueue) => {
  return (data) => {
    data.forEach((d) => {
      const size = JSON.stringify(d).length;
      responsesQueue.push([d, size]);
    });
  };
};

const _apiDataHandler = (
  responsesQueue,
  selector,
  batchCallback,
  shouldAbort,
  done) => {
  return (response) => {
    // If batching cancelled because another batch has started
    // then disregard any responses coming in
    if (shouldAbort()) {
      return;
    }

    let data = selector(response);
    // TODO
    // rather than use the full length
    // incorporate any truncation length since
    // we know we can render faster once character
    // truncation is done
    if (data) {
      if (!Array.isArray(data)) {
        data = [data];
      }
      data.forEach((d) => {
        const size = JSON.stringify(d).length;
        responsesQueue.push([d, size]);
      });
    } else {
      // The server returned a response with no data, rather than do nothing
      // send an empty array in case there are side effects related to an
      // no data vs has data
      batchCallback([]);
    }

    if (response.meta && response.meta.complete === true) {
      done();
    }
  };
};

export const HANDLERS = {
  API: _apiDataHandler,
  MEMORY: _memoryDataHandler
};
