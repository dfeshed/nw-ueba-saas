import { later, next } from 'ember-runloop';

const BATCH_CHARACTER_SIZE = 10;
const WAIT = 100;

// Used to delay API responses using a run.later.
// Allows rendering to keep up.
//
// Takes a cb to execute later and a selector that
// returns the data to pass into the callback
export const delayedResponse = (cb, selector, time = WAIT) => {
  let count = 1;
  return (response) => {
    later(cb, selector(response), time * count++);
  };
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
  batchCallback,
  selector,
  batchSize = BATCH_CHARACTER_SIZE,
  time = WAIT
) => {
  let done = false;
  const responsesQueue = [];

  const timeoutCallback = () => {
    // If no more left, and the response has finished
    // then we are done, no more recursion
    if (responsesQueue.length === 0 && done) {
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
    const data = selector(response);
    // TODO
    // rather than use the full length
    // incorporate any truncation length since
    // we know we can render faster once character
    // truncation is done
    const size = JSON.stringify(data).length;
    responsesQueue.push([data, size]);
    if (response.meta && response.meta.complete === true) {
      done = true;
    }
  };
};
