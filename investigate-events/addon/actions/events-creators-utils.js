// Temporary while feature goes through QE
/* eslint-disable  no-console */

// Event count calls rely on times being rounded to the minute in
// order to be accurate. If we have more than a minute of gap, then
// make sure we round to the minute.
const _roundToMinute = (gap, time) => {
  if (gap > 60) {
    const minutesRemainder = time % 60;
    time -= minutesRemainder;
  }
  return time;
};


/**
 * Takes a NetWitness Core event object with a `metas` array, and applies each
 * meta value as a key-value pair on the event object and then removes the
 * `metas` array.
 *
 * Example: `{metas: [ [a, b], [c, d], .. ]} => {metas: [..], a: b, c: d, ..}
 * If any duplicate keys are found in `metas`, only the last key value will be
 * applied.
 *
 * NOTE: This function will be executed thousands of times, with high frequency,
 * so its need to be performant. Therefore we forego using closures or
 * `[].forEach()` and instead use a `for` loop.
 *
 * @param {object} event
 * @public
 */
export const mergeMetaIntoEvent = (event) => {
  if (event) {
    const { metas } = event;
    if (!metas) {
      return;
    }
    const len = metas.length || 0;
    for (let i = 0; i < len; i++) {
      const meta = metas[i];
      event[meta[0]] = meta[1];
    }

    // convert to something easily sortable later
    // divide by 1000 as milliseconds have no meaning
    // in netwitness (for now) and no need to use up
    // the storage
    event.timeAsNumber = new Date(event.time).getTime() / 1000;

    // now that we have unraveled the metas
    // into the object remove the metas
    delete event.metas;

    // Don't need duplicate sessionid
    delete event.sessionid;
  }
};

// The goal of this function is to come up with a new start time that
// creates a batch that is safely under the limit based on the how many
// results we have so far and what time period those results represent
export const calculateNewStartForNextBatch = (
  lastBatchStartTime, dataEndTime, totalEvents, maxEvents
) => {
  const timeConsumedByQuerySoFar = dataEndTime - lastBatchStartTime;
  const secondsPerEvent = timeConsumedByQuerySoFar / totalEvents;

  // how many events do we want to go after per query?
  // maxEvents * .25 means we want to attempt to get a 4th
  // of the limit with our next batch
  const target = maxEvents * 0.25;
  let nextGap = Math.floor(target * secondsPerEvent);

  // If we calculate down to 0, that'll obviously do nothing
  // so try 1 second
  if (nextGap === 0) {
    nextGap++;
  }

  // subtract one from the lastBatchStartTime as the
  // end time for a range is the last batches start time
  // minus a second
  let newStartTime = lastBatchStartTime - nextGap;

  const timeGap = lastBatchStartTime - newStartTime;
  newStartTime = _roundToMinute(timeGap, newStartTime);

  if (newStartTime === lastBatchStartTime - 1) {
    if (window.DEBUG_STREAMS) {
      console.log('the next batch time start time was calculated to be the same as the next batch end time. This means no gap. Subtracting 1 for a gap of 1.');
    }
    newStartTime--;
  }

  return newStartTime;
};

// This function handles cases where we have a search that has
// either returned too many results (over the limit) or no
// results at all. Both cases mean we have to tweak the gap
// between the begin and end time in the search. If we have 0
// results, the gap needs to be larger, if we have too many
// the gap needs to be smaller.
//
// This logic handles the case where we have issues on both
// sides. We get 0, so adjust to a higher gap, but then we
// get too many, so we have to adjust back down. In these cases
// it becomes a binary search to try and find something in the
// middle that allows results that are > 0 && < limit.
export const calculateNextGapAfterFailure = (
  binarySearchData, lastGap, isReturningTooManyResults
) => {
  let newGap;
  if (isReturningTooManyResults) {
    // too many results being returned, need to shrink gap
    binarySearchData.tooMany = lastGap;
    newGap = Math.ceil((binarySearchData.tooMany + binarySearchData.noResults) * 0.5);
  } else {
    // 0 results being returned, need to increase gap to try to get some
    binarySearchData.noResults = lastGap;
    if (binarySearchData.tooMany) {
      // This failures is because there are no results being returned,
      // but we have also had too many returned in a previous attempt
      // to find results, so pick in the middle of tooMany/noResults
      newGap = Math.ceil((binarySearchData.tooMany + binarySearchData.noResults) * 0.5);
    } else {
      // haven't hit a case where we have too many results yet,
      // so just double the failure amount to try and get something
      // to come back
      newGap = binarySearchData.noResults * 2;
    }
  }

  // Event count calls rely on times being rounded to the minute in
  // order to be accurate. If we have more than a minute of gap, then
  // make sure we round to the minute.
  newGap = _roundToMinute(newGap, newGap);

  return newGap;
};