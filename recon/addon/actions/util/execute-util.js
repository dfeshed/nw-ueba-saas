import { next } from 'ember-runloop';

const BATCH_CHARACTER_SIZE = 10;
const WAIT = 100;

const batchCancellations = {};

export const BATCH_TYPES = {
  TEXT: 'TEXT',
  PACKET: 'PACKET'
};

/*
 * Slows down any spammy APIs by batching processing of responses.
 * Takes the processing callback, a selector to retrieve necessary
 * data out of the response, a batchSize to indicate how big batches
 * should be and the time to wait in betweeen batches.
 *
 * BATCH_CHARACTER_SIZE is the number of __characters__ across the payload of responses
 * that determine when to flush the queue
 */
export const timedBatchResponse = (
  batchType,
  batchCallback,
  selector,
  batchSize = BATCH_CHARACTER_SIZE,
  time = WAIT
) => {
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
        delete batchCancellations[batchType]();
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

    processBatch(time);
  };

  const processBatch = (time) => {
    // first time we want to run at time 0
    setTimeout(timeoutCallback, time || 0);
  };

  processBatch();

  return (response) => {

    // If batching cancelled because another batch has started
    // then disregard any responses coming in
    if (abort) {
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
      done = true;
    }
  };
};
