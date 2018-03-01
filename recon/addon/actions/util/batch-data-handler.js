import { join, later } from '@ember/runloop';

const BATCH_CHARACTER_SIZE = 10;
const WAIT = [0, 100];

const batchCancellations = {};
const dataHandlingCancellations = {};
const dataQueue = {};

export const BATCH_TYPES = {
  TEXT: 'TEXT',
  PACKET: 'PACKET'
};

// list of functions to call once batching resumes
const resumptionHandlers = [];

// Whether or not batching should be paused, done when, for instance
// UI intensive activity is under way that batching would slow down
let batchingPaused = false;

// means for external processes to pauseBatching
export const pauseBatching = () => {
  batchingPaused = true;
};

// means for external processes to resume batching
// which means executing any of the resumption handlers
export const resumeBatching = () => {
  batchingPaused = false;
  resumptionHandlers.forEach((handler) => handler());
  resumptionHandlers.length = 0;
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
 * - batchGapTime: OPTIONAL, defaults to WAIT, an array of times between batches. Each batch
 *   uses the next entry in the array. The last value in the array is used if there are more
 *   batches than entries in the array.
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

  // FIFO queue containing all the data that needs batching.
  // How this queue gets filled depends on the dataHandler provided.
  dataQueue[batchType] = [];

  // this function gets called on a timer, it inspects the queue
  // and decides if any data should be batched
  const timeoutCallback = () => {

    // if batching is paused, try again later
    if (batchingPaused) {
      resumptionHandlers.push(() => {
        join(timeoutCallback);
      });
      return;
    }

    // If this batch should be aborted, exit out of recursion
    if (abortBatching) {
      return;
    }

    // If no more left, and the queue no longer being filled,
    // exit out of recursion
    if (dataQueue[batchType].length === 0 && queueDoneFilling) {
      // ended gracefully/normally, remove the cancellation callback
      if (batchCancellations[batchType]) {
        delete batchCancellations[batchType];
      }
      return;
    }

    const wasBatchSent = _sendBatch(batchType, batchSize, batchCallback);

    // is batch time if no batch was sent
    let nextBatchTime = 50;
    if (wasBatchSent) {

      // if only one time left in array, use it
      if (batchGapTime.length === 1) {
        nextBatchTime = batchGapTime[0];
      } else {
        // otherwise take first time off array and use that
        nextBatchTime = batchGapTime.shift();
      }
    }

    later(timeoutCallback, nextBatchTime);
  };

  join(this, timeoutCallback);

  return dataHandler(
    batchType,
    batchCallback,
    () => abortBatching,
    () => abortDataHandling,
    () => queueDoneFilling = true);
};

const _sendBatch = function(batchType, batchSize, batchCallback) {
  // if there is nothing in the queue, will not be batching,
  // need to return that no batch was sent;
  let wasBatchSent = false;

  // If running this fucntion, we are not done processing data,
  // but data queue can be empty waiting for more stuff,
  // if so, do not bother running a batch
  const queueLength = dataQueue[batchType].length;
  if (queueLength > 0) {
    let accum = 0;
    let index = 0;
    for (; index < queueLength; index++) {
      // 2nd element in dataQueue array is the size, add it up
      accum += dataQueue[batchType][index][1];

      // have we accumulated enough data to reach the batch size?
      if (accum >= batchSize) {
        break;
      }
    }

    // remove items from the queue, those are the next batch, ship em out,
    // need to grab first element from each item as it contains the data
    // console.log('SENDING BATCH: size:', accum, ', count:', index + 1);
    const nextBatch = dataQueue[batchType].splice(0, index + 1).map((r) => r[0]);
    join(this, batchCallback, nextBatch);

    // We sent a batch, set flag
    wasBatchSent = true;
  }

  // Return whether or not a batch was sent
  return wasBatchSent;
};

// An exported means to nuke any batching that is under way
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
const _pushDataToQueue = (data, batchType) => {
  data.forEach((d) => {
    const size = JSON.stringify(d).length;
    dataQueue[batchType].push([d, size]);
  });
};

// Bulk data handler. Allows for just shoving all the data
// into the batching queue all at once.
const _bulkDataHandler = () => {
  return (batchType) => {
    return (data) => _pushDataToQueue(data, batchType);
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
  return (batchType, batchCallback, abortBatching, abortHandling, done) => {
    let dataReturned = false;
    return (response) => {

      // If data handling is cancelled, that means we have abandoned
      // the need to process this data, so just dump it.
      if (abortHandling()) {
        return;
      }

      const data = selector(response);
      if (data) {
        dataReturned = true;
        // If no longer batching data, no need to push to queue
        if (!abortBatching()) {
          _pushDataToQueue(data, batchType);
        }
      }

      // If the server returned a response with no data, rather than do nothing
      // send an empty array in case there are side effects related to
      // no data vs has data
      dispatchData({
        data: data || [],
        meta: response.meta
      });

      if (response.meta && response.meta.complete === true) {

        // if no data was ever sent, and we are done
        // then trigger empty batchCallback to trigger
        // any possible side effects
        if (!dataReturned) {
          join(this, batchCallback, []);
        }
        done();
      }
    };
  };
};

export const HANDLERS = {
  socketResponse: _apiDataHandler,
  bulk: _bulkDataHandler
};
