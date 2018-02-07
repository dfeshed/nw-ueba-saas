import fetchStreamingEvents from './fetch/events';
import { fetchLog } from './fetch/logs';
import * as ACTION_TYPES from './types';

// Common functions.
const commonHandlers = function(dispatch) {
  return {
    onError(response = {}) {
      const { code, meta } = response;
      const message = (meta) ? meta.message : undefined;
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
};

/**
 * Fetches a stream of events for the given query node.
 * @public
 */
export const eventsGetFirst = () => {
  return (dispatch, getState) => {
    const state = getState().investigate;
    const { queryNode } = state;
    const { language } = state.dictionaries;
    const { streamLimit, streamBatch } = state.eventResults;
    const handlers = {
      onInit(stopStream) {
        this.stopStreaming = stopStream;
        dispatch({ type: ACTION_TYPES.INIT_EVENTS_STREAMING });
      },
      onResponse(response) {
        const { data, goal } = getState().investigate.eventResults;
        const { data: _payload, meta } = response || {};
        const payload = Array.isArray(_payload) ? _payload : [];
        const description = meta ? meta.description : null;
        const percent = meta ? meta.percent : 0;
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
        if (description === 'Queued' ||
           (description === 'Executing' && percent < 100 && payload.length === 0)) {
          return;
        } else {
          payload.forEach(_hasherizeEventMeta);
          const count = data.length + payload.length;
          if (count >= goal) {
            this.stopStreaming();
          }
          dispatch({ type: ACTION_TYPES.SET_EVENTS_PAGE, payload });
        }
      },
      ...commonHandlers(dispatch)
    };
    fetchStreamingEvents(queryNode, language, streamLimit, streamBatch, handlers);
  };
};

/**
 * Streams additional events for the current query.
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
        if (Array.isArray(payload) && payload.length) {
          payload.forEach(_hasherizeEventMeta);
          const count = data.length + payload.length;
          if (count >= goal) {
            this.stopStreaming();
          }
          dispatch({ type: ACTION_TYPES.SET_EVENTS_PAGE, payload });
        }
      },
      ...commonHandlers(dispatch)
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
    const state = getState().investigate;
    const { serviceId } = state.queryNode;
    const sessionIds = events.mapBy('sessionId');
    const handlers = {
      onResponse(response) {
        dispatch({ type: ACTION_TYPES.SET_LOG, payload: response });
      },
      onError() {
        // The request won't complete, so mark any events still pending as error.
        const waiting = events.filter((el) => el.logStatus === 'wait');
        waiting.forEach((item) => {
          dispatch({
            type: ACTION_TYPES.SET_LOG_STATUS,
            sessionId: item.sessionId,
            status: 'rejected'
          });
        });
      }
    };

    dispatch({
      type: ACTION_TYPES.GET_LOG,
      promise: fetchLog(serviceId, sessionIds, handlers)
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