import fetchStreamingEvents from './fetch/events';
import { fetchLog } from './fetch/logs';
import * as ACTION_TYPES from './types';
// import {
//   buildEventStreamInputs,
//   executeEventsRequest,
//   buildEventLogStreamInputs,
//   executeLogDataRequest
// } from './helpers/query-utils';

const {
  log
} = console;

/**
 * Fetches a stream of events for the given query node. Stores the stream's state in node's `value.results.events`.
 * Re-uses any previous results for the same query, UNLESS `forceReload` is truthy.
 * @param {object} queryNode
 * @param {boolean} [forceReload=false] If truthy, any previous results for the same query are discarded.
 * @public
 */
export const eventsGetFirst = (forceReload = false) => {
  return (dispatch, getState) => {
    const state = getState().investigate;
    const { queryNode } = state;
    const { language } = state.dictionaries;
    const { status, streamLimit, streamBatch } = state.eventResults;
    const skipLoad = !forceReload && (status || '').match(/streaming|complete|stopped/);

    if (skipLoad) {
      return;
    }
    // @workaround ASOC-22125: Due to a server issue, a query for records that doesn't match any events will never
    // return a response back to the client. That will cause our UI to wait endlessly. To workaround, before
    // submitting the query, check if the (separate) server call for the event count has already returned zero.
    // If so, skip the query for the records.  (If the event count hasn't come back yet, no worries, submit this
    // query for now. We'll also add a check for count=0 in the count response callback, and that check will
    // abort this server call if need be.)
      // const eventCountStatus = queryNode.get('value.results.eventCount.status');
      // const eventCountData = queryNode.get('value.results.eventCount.data');
      // const eventCountIsZero = (eventCountStatus === 'resolved') && (eventCountData === 0);
      // if (eventCountIsZero) {
      //   events.set('status', 'complete');
      //   return;
      // }
    // end @workaround
    const handlers = {
      onInit(stopStream) {
        this.stopStreaming = stopStream;
        dispatch({ type: ACTION_TYPES.INIT_EVENTS_STREAMING });
      },
      onResponse(response) {
        const { data, goal } = getState().investigate.eventResults;
        const payload = response && response.data;
        if (payload) {
          payload.forEach(_hasherizeEventMeta);
          const count = data.length + payload.length;
          if (count >= goal) {
            this.stopStreaming();
          }
          dispatch({ type: ACTION_TYPES.SET_EVENTS_PAGE, payload });
        }
      },
      onError(response = {}) {
        const { code, meta: { message } = {} } = response;
        dispatch({
          type: ACTION_TYPES.SET_EVENTS_PAGE_ERROR,
          payload: { status: 'error', reason: code, message }
        });
      },
      onCompleted() {
        dispatch({
          type: ACTION_TYPES.SET_EVENTS_PAGE_STATUS,
          payload: 'complete'
        });
      },
      onStopped() {
        dispatch({
          type: ACTION_TYPES.SET_EVENTS_PAGE_STATUS,
          payload: 'stopped'
        });
      }
    };
    fetchStreamingEvents(queryNode, language, streamLimit, streamBatch, handlers);
  };
};

/**
 * Streams additional events for the current query, if the query is not already streaming and not complete.
 * Any previous results found are appended to, not discarded.
 * @public
 */
export const eventsGetMore = () => {
  return (dispatch, getState) => {
    const state = getState().investigate;
    const { queryNode } = state;
    const { language } = state.dictionaries;
    const { data, streamLimit, streamGoal, streamBatch } = state.eventResults;

    const len = data.length || 0;
    const anchor = len;
    const goal = len + streamGoal;
    const lastSessionId = len ? data[len - 1].sessionId : null;
    const handlers = {
      onInit(stopStream) {
        this.stopStreaming = stopStream;
        dispatch({ type: ACTION_TYPES.SET_ANCHOR, payload: anchor });
        dispatch({ type: ACTION_TYPES.SET_GOAL, payload: goal });
      },
      onResponse(response) {
        const { data, goal } = getState().investigate.eventResults;
        const payload = response && response.data;
        if (payload) {
          payload.forEach(_hasherizeEventMeta);
          const count = data.length + payload.length;
          if (count >= goal) {
            this.stopStreaming();
          }
          dispatch({ type: ACTION_TYPES.SET_EVENTS_PAGE, payload });
        }
      },
      onError(response = {}) {
        const { code, meta: { message } = {} } = response;
        dispatch({
          type: ACTION_TYPES.SET_EVENTS_PAGE_ERROR,
          payload: { status: 'error', reason: code, message }
        });
      },
      onCompleted() {
        dispatch({
          type: ACTION_TYPES.SET_EVENTS_PAGE_STATUS,
          payload: 'complete'
        });
      },
      onStopped() {
        dispatch({
          type: ACTION_TYPES.SET_EVENTS_PAGE_STATUS,
          payload: 'stopped'
        });
      }
    };

    fetchStreamingEvents(queryNode, language, streamLimit, streamBatch, handlers, lastSessionId);
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
    const { serviceId } = getState().investigate.queryNode;
    const sessionIds = events.mapBy('sessionId');

    dispatch({
      type: ACTION_TYPES.GET_LOG,
      promise: fetchLog(serviceId, sessionIds),
      meta: {
        onFailure(response) {
          log('GET_LOG', response);
        }
      }
    });
  };
};

/**
 * Takes a NetWitness Core event object with a `metas` array, and applies each
 * meta value as a key-value pair on the event object (while leaving the
 * original `metas` intact.
 * Example: `{metas: [ [a, b], [c, d], .. ]} => {metas: [..], a: b, c: d, ..}
 * If any duplicate keys are found in `metas`, only the last key value will be
 * applied.
 * NOTE: This function will be executed thousands of times, with high frequency,
 * so its need to be performant. Therefore we forego using closures or
 * `[].forEach()` and instead use a `for` loop.
 * @param {object} event
 * @private
 */
const _hasherizeEventMeta = (event) => {
  if (event) {
    const { metas } = event;
    if (!metas) {
      return;
    }
    const len = (metas && metas.length) || 0;
    let i;
    for (i = 0; i < len; i++) {
      const meta = metas[i];
      event[meta[0]] = meta[1];
    }
  }
};