import { join, later } from 'ember-runloop';

const BATCH_CHARACTER_SIZE = 10;
const WAIT = 100;

const batchCancellations = {};
const dataHandlingCancellations = {};

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
 * Via the dataHandler, this manages handling of data as it comes in, and batching of data
 * separately. You can both process/manage injestion and manage batching.
 *
 * Configuration Object
 * - dataHandler: either an apiDataHandler which knows how to handle responses from APIs
 *   or a memoryDataHandler which handles data already in memory
 * - batchType: one of `BATCH_TYPES`
 * - batchCallback: function to call to process a batch
 * - batchSize: OPTIONAL, defaults to BATCH_CHARACTER_SIZE, the size of each batch by
 *   character count of the items being batched after
 * - batchGapTime: OPTIONAL, defaults to WAIT, the time between batchCallback executions
 */
export const batchDataHandler = ({
  dataHandler,
  batchType,
  batchCallback,
  batchSize = BATCH_CHARACTER_SIZE,
  batchGapTime = WAIT
}) => {
  // whether or not the data is done coming into the queue
  // Ex: when API done sending data
  let queueDoneFilling = false;

  // Whether or not to abort the calling of the batch callback
  // Ex: If we have moved away from view, and batching of rendering needs to stop
  let abortBatching = false;

  // Whether or not to abort the handling of data
  // Ex: If we start processing the same batch type (different event clicked
  // for same view) we do not want to handle the old event's data any longer
  // if it is still coming in
  //
  // NOTE: batching and queueing are important to control separately.
  // Ex: If I am batching text view, and user clicks packet, we need to stop
  // batching rendering of text view. But if we stop injesting/handling data,
  // when the user returns to text view, data will be truncated/missing
  let abortDataHandling = false;

  // FIFO queue containing all the data that needs batching.
  // How this queue gets filled depends on the dataHandler provided.
  const dataQueue = [];

  // Only one batch type at a time.
  // if a previous request exists for the same batchType,
  // then abort the handling/batching of the data
  _abortHandlingIfRunning(batchType);
  _abortBatchingOfOtherTypes(batchType);

  // create/register batch cancellation callback for this batch request
  batchCancellations[batchType] = () => {
    abortBatching = true;
  };

  // create/register data handling cancellation callback for this batch request,
  dataHandlingCancellations[batchType] = () => {
    abortDataHandling = true;
  };

  // this function gets called on a timer, it inspects the queue
  // and decides if any data should be batched
  const timeoutCallback = () => {

    // If this batch should be aborted, exit out of recursion
    if (abortBatching) {
      return;
    }

    // If no more left, and the queue no longer being filled,
    // exit out of recursion
    if (dataQueue.length === 0 && queueDoneFilling) {
      // ended gracefully/normally, remove the cancellation callback
      if (batchCancellations[batchType]) {
        delete batchCancellations[batchType];
      }
      return;
    }

    // If we got this far, we are not done processing data,
    // but data queue can be empty waiting for more stuff,
    // if so, do not bother running a batch
    const queueLength = dataQueue.length;
    // if there is nothing in the queue, will not be batching
    // so check back quickly
    let nextCallbackTime = 50;
    if (queueLength > 0) {
      let accum = 0;
      let index = 0;
      for (; index < queueLength; index++) {
        accum += dataQueue[index][1];
        // have we accumulated enough data to reach the batch size?
        if (accum >= batchSize) {
          break;
        }
      }

      // remove items from the queue, those are the next batch, ship em out
      // need to grab first element from queue item as it contains data
      // console.log('SENDING BATCH: size:', accum, ', count:', index + 1);
      const nextBatch = dataQueue.splice(0, index + 1).map((r) => r[0]);
      join(this, batchCallback, nextBatch);

      // There is something in the queue, so will be batching,
      // so run next batch at batchGapTime
      nextCallbackTime = batchGapTime;
    }

    later(timeoutCallback, nextCallbackTime);
  };

  join(this, timeoutCallback);

  return dataHandler(
    dataQueue,
    () => abortBatching,
    () => abortDataHandling,
    () => queueDoneFilling = true);
};

export const killAllBatching = () => {
  Object.keys(BATCH_TYPES).forEach(_abortBatchingIfRunning);
};

// Runs batch cancellation for the batchType provided
const _abortBatchingIfRunning = (batchType) => {
  if (batchCancellations[batchType]) {
    batchCancellations[batchType]();
    delete batchCancellations[batchType];
  }
};

// Runs batch cancellation on any types that do not
// match the type provided.
const _abortBatchingOfOtherTypes = (batchType) => {
  Object.keys(BATCH_TYPES)
    .filter((btKey) => BATCH_TYPES[btKey] !== batchType)
    .forEach(_abortBatchingIfRunning);
};

// mechanism to cancel the handling of data. And if
// no longer handling the data no need to batch the
// data either.
const _abortHandlingIfRunning = (batchType) => {
  _abortBatchingIfRunning(batchType);
  if (dataHandlingCancellations[batchType]) {
    dataHandlingCancellations[batchType]();
  }
};

// Process array of data into the queue. Data goes
// into the queue along with its size.
//
// TODO
// rather than use the full length of the stringify
// incorporate any truncation length since
// we know we can render faster once character
// truncation is done
const _pushDataToQueue = (data, dataQueue) => {
  data.forEach((d) => {
    const size = JSON.stringify(d).length;
    dataQueue.push([d, size]);
  });
};

// Bulk data handler. Allows for just shoving all the data
// into the batching queue all at once.
const _bulkDataHandler = () => {
  return (dataQueue) => {
    return (data) => _pushDataToQueue(data, dataQueue);
  };
};

// Streaming API Data handler, allows for pushing data into the
// batching queue from many streaming API responses
//
// Inputs:
// - selector, function that takes a response and returns the items
//   to be batched
// - dispatchData, callback that is executed every time data return
//   from the API and after it has been run through the selector
// to find the data inside the API response
const _apiDataHandler = (selector, dispatchData) => {
  return (dataQueue, abortBatching, abortHandling, done) => {
    return (response) => {
      // If data handling is cancelled, that means we have abandoned
      // the need to process this data, so just dump it.
      if (abortHandling()) {
        return;
      }

      let data = selector(response);
      if (data) {
        if (!Array.isArray(data)) {
          data = [data];
        }

        // If no longer batching data, no need to push to queue
        if (!abortBatching()) {
          _pushDataToQueue(data, dataQueue);
        }
      }

      // If the server returned a response with no data, rather than do nothing
      // send an empty array in case there are side effects related to
      // no data vs has data
      dispatchData(data || []);

      if (response.meta && response.meta.complete === true) {
        done();
      }
    };
  };
};

export const HANDLERS = {
  socketResponse: _apiDataHandler,
  bulk: _bulkDataHandler
};
