import { later, next } from 'ember-runloop';

const BATCH_SIZE = 10;
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
 */
export const timedBatchResponse = (batchCallback, selector, batchSize = BATCH_SIZE, time = WAIT) => {
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
    if (responsesQueue.length > 0) {
      const nextBatchSize = batchSize > responsesQueue.length ? responsesQueue.length : batchSize;
      const nextBatch = responsesQueue.splice(0, nextBatchSize);
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
    responsesQueue.push(selector(response));
    if (response.meta && response.meta.complete === true) {
      done = true;
    }
  };
};