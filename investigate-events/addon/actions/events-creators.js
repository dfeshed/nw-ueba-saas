import { SORT_ORDER } from 'investigate-events/reducers/investigate/event-results/selectors';
import fetchStreamingEvents from 'investigate-shared/actions/api/events/events';
import { queryIsRunning } from 'investigate-events/actions/initialization-creators';
import { fetchLog } from './fetch/logs';
import * as ACTION_TYPES from './types';
import { getActiveQueryNode } from 'investigate-events/reducers/investigate/query-node/selectors';
import { getFlattenedColumnList, hasMetaSummaryColumn } from 'investigate-events/reducers/investigate/data-selectors';
import { resultCountAtThreshold } from 'investigate-events/reducers/investigate/event-count/selectors';
import { hasMinimumCoreServicesVersionForColumnSorting } from '../reducers/investigate/services/selectors';
import { handleInvestigateErrorCode } from 'component-lib/utils/error-codes';
import { mergeMetaIntoEvent } from './events-creators-utils';
import { updateErrorMsgIfMaxMemory } from './utils';

export const _deriveSort = (field, sortDirection, state) => {
  const hasRequiredVersion = hasMinimumCoreServicesVersionForColumnSorting(state);
  const atThreshold = resultCountAtThreshold(state);
  const sortingRequired = sortDirection && sortDirection.toLowerCase() !== SORT_ORDER.NO_SORT.toLowerCase();
  if (atThreshold && hasRequiredVersion && field && sortingRequired) {
    return {
      field,
      descending: sortDirection.toLowerCase() === SORT_ORDER.DESC.toLowerCase()
    };
  }
};

const currentStreamState = {
  // tracks the callback function for the event stream
  // in the event we need to async cancel it
  eventStreamCallback: undefined,

  // An accumulation of all the events that have come in
  // for the current stream batch. If the stream finishes
  // and the events in this array are under the limit,
  // they will be dispatched to state.
  currentBatchEvents: [],

  // The number of events that have been dispatched to
  // state during this stream
  eventsDispatchedCount: 0,

  // The number of batches that have come in since the
  // last time currentBatchEvents has been flushed to
  // state
  interimBatchCount: 0,

  // whether or not the user has cancelled the stream
  cancelled: false,

  // Keeps track of the columns needed for the query
  // between query calls as it only needs to be
  // calculated once
  flattenedColumnList: undefined
};

// Ember uses sessionId internally, but a user can configure
// sessionid (all lower case) as a field. We need to know if
// sessionid is in the list of columns so we can treat it
// correctly
const _isSessionIdInColumnList = () => {
  return currentStreamState && currentStreamState.flattenedColumnList && currentStreamState.flattenedColumnList.includes('sessionid');
};

// Calculate the total events we've acquired so far
const _totalEvents = () => {
  return currentStreamState.eventsDispatchedCount + currentStreamState.currentBatchEvents.length;
};

// Accumulate events and increment batch count and dispatch events
// to state if enough events have accumulated and if the calling
// function allows it
const _addEventsToResponseCache = (newBatchEvents, canDispatch = false, dispatchNow, isOldestEvents = false) => {
  return (dispatch) => {
    currentStreamState.currentBatchEvents.push(...newBatchEvents);
    currentStreamState.interimBatchCount++;

    // >= because the caller may let many more batches than
    // accumulate before it allows events to be dispatched.
    // We want to dispatch a few batches at a time to avoid
    // throttling state with too many state updates
    const shouldDispatch = dispatchNow || currentStreamState.interimBatchCount >= 5;
    if (canDispatch && shouldDispatch) {
      dispatch(_dispatchEvents(isOldestEvents));
    }
  };
};

// Cleans up any streams that are currently open
const _cleanUpStreams = () => {
  if (currentStreamState.eventStreamCallback) {
    currentStreamState.eventStreamCallback();
    currentStreamState.eventStreamCallback = undefined;
  }
};

// Anytime a batch needs to be kicked off, _resetForNextBatches
// makes sure that everything that needs to be reset is reset
// and that previous streams are stopped/unsubscribed.
const _resetForNextBatches = () => {
  _cleanUpStreams();
  currentStreamState.currentBatchEvents.length = 0;
  currentStreamState.interimBatchCount = 0;
};

/**
 *
 * @param errorCode
 * @param serverMessage
 * Called if there's an error or if all batching is complete
 * and we are done with the entire query. This is not called
 * when we cancel.
 */
const _done = (errorCode, serverMessage) => {
  return (dispatch, getState) => {
    _cleanUpStreams();

    // dispatch any events that have accumulated and have
    // not already been sent to state
    dispatch(_dispatchEvents());

    // Set queryIsRunning to false so UI can react
    dispatch(queryIsRunning(false));

    if (errorCode) {
      serverMessage = updateErrorMsgIfMaxMemory(serverMessage);

      dispatch({
        type: ACTION_TYPES.QUERY_STATS,
        payload: {
          message: serverMessage
        },
        code: errorCode,
        time: Date.now()
      });

      dispatch({
        type: ACTION_TYPES.SET_EVENTS_PAGE_ERROR,
        payload: {
          status: 'error',
          reason: errorCode,
          message: serverMessage
        }
      });
    } else {
      const totalEventsRetrieved = _totalEvents();
      const { investigate: { eventCount: { data: eventCount } } } = getState();
      if (totalEventsRetrieved !== eventCount) {
        // This is a unique case where the eventCount returned
        // earlier did not match the number of events retrieved.
        // We will at all times rely on the actual events returned and
        // on completion, update eventCount if there is a mismatch.
        dispatch({
          type: ACTION_TYPES.EVENT_COUNT_RESULTS,
          payload: { data: totalEventsRetrieved }
        });
      }

      dispatch({
        type: ACTION_TYPES.SET_EVENTS_PAGE_STATUS,
        payload: 'complete',
        streamingEndedTime: Date.now()
      });
    }
  };
};

// Ensure we don't dispatch a status update
// if we do not have an actual update
const _handleEventsStatus = (newStatus, streamingEndedTime) => {
  return (dispatch, getState) => {
    const { status } = getState().investigate.eventResults;
    if (status !== newStatus) {
      dispatch({
        type: ACTION_TYPES.SET_EVENTS_PAGE_STATUS,
        payload: newStatus,
        streamingEndedTime
      });
    }
  };
};

// Prepares and sends events to state
const _dispatchEvents = () => {
  return (dispatch) => {
    // don't bother if there are no events to ship out
    if (currentStreamState.currentBatchEvents.length > 0) {
      currentStreamState.currentBatchEvents.forEach(mergeMetaIntoEvent(_isSessionIdInColumnList()));

      dispatch({
        type: ACTION_TYPES.SET_EVENTS_PAGE,
        payload: currentStreamState.currentBatchEvents
      });
      currentStreamState.eventsDispatchedCount += currentStreamState.currentBatchEvents.length;
      currentStreamState.currentBatchEvents.length = 0;
      currentStreamState.interimBatchCount = 0;
    }
  };
};

/**
 * Cancel a currently executing streaming request for events.
 * @public
 */
export const cancelEventsStream = () => {
  _resetForNextBatches();
  currentStreamState.cancelled = true;
};

/**
 * Kicks off a search for the oldest events. This is a simple search to
 * execute because the sort order from the database is naturally oldest
 * first
 * @public
 */
export const eventsStartOldest = () => {
  currentStreamState.cancelled = false;
  currentStreamState.eventsDispatchedCount = 0;
  let isFirstEventPayload = true;

  _resetForNextBatches();

  return (dispatch, getState) => {

    const { streamLimit } = getState().investigate.eventResults;

    const handlers = {
      onInit(_stopStream) {
        currentStreamState.eventStreamCallback = _stopStream;
        dispatch({
          type: ACTION_TYPES.INIT_EVENTS_STREAMING,
          payload: { eventTimeSortOrderPreferenceWhenQueried: SORT_ORDER.NO_SORT }
        });
      },
      onResponse(response) {
        // if we cancelled before this message got back, do not
        // bother processing it, no need to exit with _done as
        // cancel cleans up
        if (currentStreamState.cancelled) {
          return;
        }

        const { data: _payload, meta } = response || {};
        const payload = Array.isArray(_payload) ? _payload : [];
        const description = meta ? meta.description : null;
        const percent = meta ? meta.percent : '0';

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
        const lowerCaseDesc = description ? description.toLowerCase() : null;
        if (description && (lowerCaseDesc === 'queued' ||
           (lowerCaseDesc === 'executing' && parseInt(percent, 10) < 100 && payload.length === 0))) {
          return;
        } else {
          // Add events to cache of current requests events, want to force
          // a dispatch of the very first batch so the users see something
          // quickly
          dispatch(_addEventsToResponseCache(payload, true, isFirstEventPayload, true));
          isFirstEventPayload = false;

          // The stream does not indicate it is 'complete' if it hits the limit
          // so we have to detect that and jump to complete.
          const areEventsAtLimit = _totalEvents() >= streamLimit;
          if (areEventsAtLimit) {
            dispatch(_done());
          }
        }
      },
      onError(response = {}) {
        const { errorCode, serverMessage } = handleInvestigateErrorCode(response);
        dispatch(_done(errorCode, serverMessage));
      },
      onCompleted() {
        // stream already closed since it completed, can null async callback
        currentStreamState.eventStreamCallback = undefined;
        dispatch(_done());
      },
      onStopped() {
        dispatch(_handleEventsStatus('stopped', Date.now()));
      }
    };

    const state = getState();
    currentStreamState.flattenedColumnList = getFlattenedColumnList(state);
    const { investigate } = state;
    const queryNode = getActiveQueryNode(getState());
    const { language } = investigate.dictionaries;
    const { streamBatch } = investigate.eventResults;
    const { sortField, sortDirection, startMeta, endMeta } = investigate.data;

    fetchStreamingEvents(
      queryNode,
      language,
      streamLimit,
      streamBatch,
      handlers,
      currentStreamState.flattenedColumnList,
      _deriveSort(sortField, sortDirection, state),
      startMeta,
      endMeta,
      'investigate-events-event-stream'
    );
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
    const numSessionIds = sessionIds.length;
    const logAccumulation = [];

    const handlers = {
      onResponse(response, stopStream) {
        logAccumulation.push(response.data);
        // rather than dispatch each one, one at a time
        // take it easy on redux/components and wait until
        // we have them all, then dispatch. Also stop the
        // stream or else it stays open forrrrreverrrr
        if (logAccumulation.length >= numSessionIds) {
          stopStream();
          dispatch({ type: ACTION_TYPES.SET_LOG, payload: logAccumulation });
        }
      },
      onError({ code, request }) {
        // When an error comes back, it could point to multiple sessionIds, like
        // in the instance where you don't have permission to view logs. So we
        // need to look if there are sessionIds defined, then dispatch a SET_LOG
        // action for each one.
        const filter = Array.isArray(request.filter) ? request.filter : [];
        const sessionIds = filter.find((d) => d.field === 'sessionIds');
        const values = sessionIds ? sessionIds.values : [];
        const payload = values.forEach((d) => {
          return {
            sessionId: d,
            code
          };
        });
        dispatch({ type: ACTION_TYPES.SET_LOG, payload });
      }
    };

    // check if meta-summary is present in any of the columns.
    // Fetch logs if it is present, otherwise be done with it.
    const shouldMakeLogCalls = hasMetaSummaryColumn(getState());
    if (shouldMakeLogCalls) {
      dispatch({
        type: ACTION_TYPES.GET_LOG,
        promise: fetchLog(serviceId, sessionIds, handlers)
      });
    }
  };
};

export const toggleEventRelationships = () => {
  return {
    type: ACTION_TYPES.TOGGLE_EVENT_RELATIONSHIPS
  };
};

export const toggleSplitSession = (tuple, relatedEvents, parentIndex) => {
  return { type: ACTION_TYPES.TOGGLE_SPLIT_SESSION, tuple, relatedEvents, parentIndex };
};
