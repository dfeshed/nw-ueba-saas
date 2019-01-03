// Temporary while feature goes through QE
/* eslint-disable  no-console */

// window.DEBUG_STREAMS = true;

import fetchStreamingEvents from 'investigate-shared/actions/api/events/events';
import { queryIsRunning } from 'investigate-events/actions/initialization-creators';
import { fetchLog } from './fetch/logs';
import fetchCount from 'investigate-shared/actions/api/events/event-count';
import * as ACTION_TYPES from './types';
import { getActiveQueryNode } from 'investigate-events/reducers/investigate/query-node/selectors';
import { getFlattenedColumnList } from 'investigate-events/reducers/investigate/data-selectors';
import { handleInvestigateErrorCode } from 'component-lib/utils/error-codes';
import {
  calculateNewStartForNextBatch,
  mergeMetaIntoEvent,
  calculateNextGapAfterFailure
} from './events-creators-utils';

const INITIAL_TIME_WINDOW_IN_SECONDS = 5 * 60;

const currentStreamState = {
  // stopStreaming callbacks for events and event count,
  // used to stop calls mid-stream if we aren't happy
  // with what they are returning or if the user
  // cancels the request
  stopStreamingCallbacks: [],

  // An accumulation of all the events that have come in
  // for the current stream. If the stream finishes and
  // the events in this array are under the limit, they
  // will be sent to state.
  currentBatchEvents: [],

  // When we need to do a binary search with the batch
  // start time in order to find a set of results that is
  // less than the stream limit and more than 0, we use
  // tooMany/noResults to track our binary search window
  // to narrow in on the right time to use
  binarySearchBatchStartTime: {
    // tooMany is the last startTime used that resulted
    // in too many results being returned
    tooMany: 0,

    // noResults is the last startTime used that resulted
    // in no results being returned
    noResults: 0
  },

  // whether or not the user has cancelled the search
  cancelled: false,

  // Keeps track of the columns needed for the query
  // between query calls as it only needs to be
  // calculated once
  flattenedColumnList: undefined
};

// Anytime a batch needs to be kicked off, _resetForNextBatch
// makes sure that everything that needs to be reset is reset
// and that previous streams are stopped/unsubscribed.
const _resetForNextBatch = () => {
  if (currentStreamState.stopStreamingCallbacks.length > 0) {
    currentStreamState.stopStreamingCallbacks.forEach((cb) => cb());
    currentStreamState.stopStreamingCallbacks.length = 0;
  }
  currentStreamState.currentBatchEvents.length = 0;
};

// Called when all batching is complete and we are done
// with an entire query. This is not called when we cancel.
const _done = () => {
  if (window.DEBUG_STREAMS) {
    console.log('ALL DONE');
  }

  // currentBatchEvents is a temporary holding spot for
  // events, we want clear out that memory ASAP
  // because it could be holding 100k events. Rather
  // not wait for garbage collection.
  currentStreamState.currentBatchEvents.length = 0;

  return {
    type: ACTION_TYPES.SET_EVENTS_PAGE_STATUS,
    payload: 'complete'
  };
};

// Ensure we don't dispatch a status update
// if we do not have an actual update
const _handleEventsStatus = (newStatus) => {
  return (dispatch, getState) => {
    const { status } = getState().investigate.eventResults;
    if (status !== newStatus) {
      dispatch({
        type: ACTION_TYPES.SET_EVENTS_PAGE_STATUS,
        payload: newStatus
      });
    }
  };
};

// Checks to see if the count provided is over the streamLimit,
// if it is, then we need to re-execute another batch with a smaller
// time window.
const _determineEventsOverLimit = (batchStartTime, batchEndTime, countToCheck) => {
  return (dispatch, getState) => {
    const { investigate } = getState();
    const { streamLimit } = investigate.eventResults;
    if (countToCheck >= streamLimit) {
      if (window.DEBUG_STREAMS) {
        console.log('too MANY results, need to try for less');
      }

      // too many results, need better gap
      const batchWindow = batchEndTime - batchStartTime;
      const newGap = calculateNextGapAfterFailure(
        currentStreamState.binarySearchBatchStartTime, batchWindow, true);
      const newStartTime = batchEndTime - newGap;
      dispatch(_getEventsBatch(newStartTime, batchEndTime));
    }
  };
};

// We do an event counts call with every batch that has a time range
// larger than 1 minute. (Less than one minute and the event counts
// call is not accurate.) When the event counts call returns, we
// delegate to _determineEventsOverLimit to decide what to do, if
// anything, with the result.
const _getBatchEventCount = (queryNode, language, streamLimit, dispatch) => {
  const handlers = {
    onInit(stopStream) {
      // Stash the count stop in case the onResponse isn't called
      currentStreamState.stopStreamingCallbacks.push(stopStream);
    },
    onResponse(response, _stopStream) {
      // protect against null data while query is being processed
      // and when devices are re  turned
      if (response.data != null) {
        // Don't need to keep the event count stream going, kill it
        _stopStream();
        // check if the count is over the limit and react
        dispatch(
          _determineEventsOverLimit(queryNode.startTime, queryNode.endTime, response.data)
        );
      }
    }
  };

  fetchCount(
    queryNode.serviceId,
    queryNode.startTime,
    queryNode.endTime,
    queryNode.metaFilter,
    language,
    streamLimit,
    handlers
  );
};

/**
 * Fetches a stream of events for the given query node.
 *
 * @param {number} batchStartTime - the time to start this batch
 * @param {number} batchEndTime - the time to end this batch
 *
 * @private
 */
const _getEventsBatch = (batchStartTime, batchEndTime) => {

  // If the stream was cancelled, GTFO
  if (currentStreamState.cancelled) {
    return;
  }

  _resetForNextBatch();

  // In order for counts to work properly, everything has to
  // be evenly a minute and sometimes because the entire range
  // end time can end with 59 seconds, we can have a start
  // time that ends in 59 seconds (like end time - 300 seconds)
  // Resolve here by adding the missing second.
  if (batchStartTime % 60 === 59) {
    batchStartTime += 1;
  }

  return (dispatch, getState) => {
    const allState = getState();
    const { investigate } = allState;
    const { queryNode } = investigate;

    // Need to know if we are on the first stream to
    // properly initialize
    const isFirstStream = queryNode.endTime === batchEndTime;

    const handlers = {
      onInit(stopStream) {
        currentStreamState.stopStreamingCallbacks.push(stopStream);
        if (isFirstStream) {
          dispatch({
            type: ACTION_TYPES.INIT_EVENTS_STREAMING
          });
        }
      },
      onResponse(response) {
        const { data: _payload, meta } = response || {};
        const payload = Array.isArray(_payload) ? _payload : [];

        // A streaming websocket call goes through different phases. First is
        // `Queued`, then `Executing`, then an optional, unnamed "data" phase.
        // Brokers appear to only use the first two pahses, while concentrators
        // use all three. For the first two phases, the data property could be
        // an empty array. When we dispatch that, it will show a message that
        // the query filters returned no data, which isn't necessarily true.
        // We will always skip the `Queued` phase. We will skip the `Executing`
        // phase if `percent` is less than 100% and we have no data to show.
        // This covers brokers as they fetch data from their connected devices
        // and return it in the `Executing` phase. Concentrators will return
        // their data in the unnamed "data" phase.
        const description = meta ? meta.description : null;
        const percent = meta ? meta.percent : '0';
        const lowerCaseDesc = description ? description.toLowerCase() : null;
        if (description && (lowerCaseDesc === 'queued' ||
           (lowerCaseDesc === 'executing' && parseInt(percent, 10) < 100 && payload.length === 0))) {
          return;
        } else {
          if (window.DEBUG_STREAMS && response.data.length) {
            console.log(
              `Received batch of ${response.data.length} results with start time ${batchStartTime} and end time ${batchEndTime}`)
            ;
            console.timeEnd();
            console.time();
          }

          // Add events to cache of current requests events
          currentStreamState.currentBatchEvents =
            [...currentStreamState.currentBatchEvents, ...payload];

          // eager clearing out of memory
          response.data.length = 0;

          // If the stream limit is reached, onCompleted and onStopped are
          // not called, so have to catch over-max issues in onResponse
          //
          // If the CURRENT query (not the accumulation of all the queries)
          // has reached the stream limit, that's not good. It means we
          // didn't get to the end of the query's results and we need to,
          // because the end of the query is where the most recent records
          // are. So, we need to re-execute the previous query with a
          // smaller time range which scraps any events we had accumulated
          // with the query that went over the limit.
          dispatch(
            _determineEventsOverLimit(batchStartTime, batchEndTime, currentStreamState.currentBatchEvents.length)
          );
        }
      },
      onError(response = {}) {
        const { errorCode, serverMessage } = handleInvestigateErrorCode(response);
        dispatch({
          type: ACTION_TYPES.SET_EVENTS_PAGE_ERROR,
          payload: {
            status: 'error',
            reason: errorCode,
            message: serverMessage
          }
        });
        dispatch(queryIsRunning(false));
      },
      onCompleted() {
        const { investigate } = getState();
        const { data: eventCount } = investigate.eventCount;

        // has the event count come back with 0? then we know
        // this query will never return any results, so indicate
        // we are done and escape
        if (eventCount === 0) {
          dispatch(_done());
          return;
        }

        // IF WE ARE THIS FAR...
        // We either expect results (99.9% of the time) to come back
        // or we haven't got an event count yet (0.1% of the time).

        // completed with no results? We need to try again with
        // a larger window, because we expect results.
        if (currentStreamState.currentBatchEvents.length === 0) {
          if (window.DEBUG_STREAMS) {
            console.log('too FEW results, need to try for more');
          }

          // calculate a new start time
          const batchWindow = batchEndTime - batchStartTime;
          const newGap = calculateNextGapAfterFailure(
            currentStreamState.binarySearchBatchStartTime, batchWindow, false);
          let newStartTime = batchEndTime - newGap;

          // Don't let the start time be before the actual window
          // start time
          if (newStartTime < queryNode.startTime) {
            newStartTime = queryNode.startTime;
          }

          dispatch(_getEventsBatch(newStartTime, batchEndTime));
          return;
        }

        // IF WE ARE THIS FAR...
        // GOOD! We have a completed stream with results we can use!
        // We can process these results into state and figure out
        // whether we need to run another query or if we are done.

        if (window.DEBUG_STREAMS) {
          console.log(
            `Happy with batch result, sending ${currentStreamState.currentBatchEvents.length} records to state for rendering`
          );
        }

        // reset the failures tracker since this was a success
        currentStreamState.binarySearchBatchStartTime = { tooMany: 0, noResults: 0 };

        // Preprocess and send these results to state
        currentStreamState.currentBatchEvents.forEach(mergeMetaIntoEvent);
        dispatch({
          type: ACTION_TYPES.SET_EVENTS_PAGE,
          payload: currentStreamState.currentBatchEvents
        });

        const { data, streamLimit } = investigate.eventResults;
        const totalEventsAccumulated = data.length + currentStreamState.currentBatchEvents.length;
        const isAtOrAboveMaxEventsAllowed = totalEventsAccumulated >= streamLimit;
        const isAtBeginningOfTimeRange = batchStartTime <= queryNode.startTime;

        // Have we gone over the max event limit?
        // Or have we backed our way up to the start of the
        // time range? If so, we are done, ship it.
        if (isAtOrAboveMaxEventsAllowed || isAtBeginningOfTimeRange) {
          dispatch(_done());
          return;
        }

        if (window.DEBUG_STREAMS) {
          console.log('But we are not done, need more');
        }

        // IF WE ARE THIS FAR...
        // We are NOT done. We have results, but we both are not at the max
        // AND we have not reached the end of the time range. Need to go
        // get more events.

        const eventCountLessThanStreamLimit = !!eventCount && eventCount < streamLimit;
        if (eventCountLessThanStreamLimit) {
          if (window.DEBUG_STREAMS) {
            console.log('Event count says we can just get all the rest, so lets do that');
          }
          // Because the event count is less than the limit, we can just
          // kick off a request to get all the rest without worrying
          // about going over the max.
          // Subtract 1 because time ranges are inclusive. If we do not subtract
          // then the first second of the last range will duplicate with the
          // last second of this range
          dispatch(_getEventsBatch(queryNode.startTime, batchStartTime - 1));
          return;
        }

        // IF WE ARE THIS FAR...
        // We need to get the next batch, but can't get all of the time
        // range, so we have to calculate it smartly.

        // Calculate new start time for next batch. To handle the case
        // where the most recent event is no where near the 'endTime',
        // we want to pass in the most recent event time into the
        // calculation so it can calculate the next gap smartly
        const eventsInState = getState().investigate.eventResults.data;
        let endTimeToUseForCalculations = queryNode.endTime;
        if (eventsInState.length > 0) {
          endTimeToUseForCalculations = eventsInState[0].timeAsNumber;
        }

        const newStartTime =
          calculateNewStartForNextBatch(
            batchStartTime,
            endTimeToUseForCalculations,
            totalEventsAccumulated,
            streamLimit
          );

        // subtract 1 because time ranges are inclusive. If we do not subtract
        // then the first second of the last range will duplicate with the
        // last second of this range
        dispatch(_getEventsBatch(newStartTime, batchStartTime - 1));
      },
      onStopped() {
        // Only reason we "stop" is because we are going again.
        //
        // TODO check eventual cancel logic to see how that
        // ends up working
        dispatch(_handleEventsStatus('between-streams'));
        dispatch(queryIsRunning(false));
      }
    };

    // TODO This `if` is a short term-hack because of double execution when
    // first launching route. Happens when going from /investigate =>
    // /investigate/events
    if (investigate.eventResults.status !== 'streaming') {
      const { language } = investigate.dictionaries;
      const { streamLimit, streamBatch } = investigate.eventResults;
      const modifiedQueryNode = {
        ...queryNode,
        startTime: batchStartTime,
        endTime: batchEndTime
      };
      if (window.DEBUG_STREAMS) {
        console.log(`Running query with gap of ${batchEndTime - batchStartTime}`);
      }
      fetchStreamingEvents(
        modifiedQueryNode,
        language,
        streamLimit,
        streamBatch,
        handlers,
        currentStreamState.flattenedColumnList
      );

      // Count calls are only accurate if begin/end are rounded to the
      // minute. If there is less than a minute for the gap, no point
      // in doing any sort of count checks while streaming.
      if (batchEndTime - batchStartTime > 60) {
        _getBatchEventCount(modifiedQueryNode, language, streamLimit, dispatch);
      }
    }
  };
};

/**
 * Cancel a currently executing streaming request for events.
 * @public
 */
export const cancelEventsStream = () => {
  if (window.DEBUG_STREAMS) {
    console.log('Cancelling Streams');
  }
  _resetForNextBatch();
  currentStreamState.cancelled = true;
};


/**
 * Kicks off a descending search for events. Descending searches
 * are very complex because the data itself never comes back
 * descending. So we have to slice off little time ranges at the
 * most recent edge of the time boundary in an attempt to
 * piece together a result set comprised of the most recent data.
 * @public
 */
export const eventsStartDescending = () => {
  return (dispatch, getState) => {
    const { queryNode } = getState().investigate;

    currentStreamState.binarySearchBatchStartTime = { tooMany: 0, noResults: 0 };
    currentStreamState.cancelled = false;
    currentStreamState.flattenedColumnList = getFlattenedColumnList(getState());

    let startTimeForFirstBatch = queryNode.endTime - INITIAL_TIME_WINDOW_IN_SECONDS;

    // If a smaller time window is picked by the user
    // than our initial time window, then get started with
    // half the user's window
    if (queryNode.startTime > startTimeForFirstBatch) {
      startTimeForFirstBatch = queryNode.endTime - Math.ceil((queryNode.endTime - queryNode.startTime) * 0.5);
    }

    // do not let initial window be in the middle of a minute
    const minutesRemainder = startTimeForFirstBatch % 60;
    startTimeForFirstBatch -= minutesRemainder;

    // Kick off batching with the initial set
    // of parameters for the first batch
    dispatch(_getEventsBatch(startTimeForFirstBatch, queryNode.endTime));
  };
};

/**
 * Kicks off the fetching of log data for a given array of events.
 * @param {object} queryNode The query which owns the given event records.
 * @param {object[]} events The array of event records.
 * @public
 */
export const eventsLogsGet = (events = []) => {
  return (dispatch, getState) => {
    const { serviceId } = getActiveQueryNode(getState());
    const sessionIds = events.mapBy('sessionId');
    const handlers = {
      onResponse(response) {
        dispatch({ type: ACTION_TYPES.SET_LOG, payload: response });
      },
      onError({ code, request }) {
        // When an error comes back, it could point to multiple sessionIds, like
        // in the instance where you don't have permission to view logs. So we
        // need to look if there are sessionIds defined, then dispatch a SET_LOG
        // action for each one.
        const filter = Array.isArray(request.filter) ? request.filter : [];
        const sessionIds = filter.find((d) => d.field === 'sessionIds');
        const values = sessionIds ? sessionIds.values : [];
        values.forEach((d) => {
          const payload = {
            code,
            data: { sessionId: d }
          };
          dispatch({ type: ACTION_TYPES.SET_LOG, payload });
        });
      }
    };

    dispatch({
      type: ACTION_TYPES.GET_LOG,
      promise: fetchLog(serviceId, sessionIds, handlers)
    });
  };
};