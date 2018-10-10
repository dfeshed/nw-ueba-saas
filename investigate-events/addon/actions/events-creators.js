import fetchStreamingEvents from 'investigate-shared/actions/api/events/events';
import { fetchLog } from './fetch/logs';
import * as ACTION_TYPES from './types';
import { getActiveQueryNode } from 'investigate-events/reducers/investigate/query-node/selectors';
import { handleInvestigateErrorCode } from 'component-lib/utils/error-codes';
import _ from 'lodash';
import getEventCount from './event-count-creators';

// Common functions.
const commonHandlers = function(dispatch) {
  return {
    onError(response = {}) {
      const { errorCode, serverMessage } = handleInvestigateErrorCode(response);
      dispatch({
        type: ACTION_TYPES.SET_EVENTS_PAGE_ERROR,
        payload: { status: 'error', reason: errorCode, message: serverMessage }
      });
      dispatch({ type: ACTION_TYPES.QUERY_IS_RUNNING, payload: false });
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
      dispatch({ type: ACTION_TYPES.QUERY_IS_RUNNING, payload: false });
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
          payload.forEach(_hasherizeEventMeta);
          const count = data.length + payload.length;
          if (count >= goal) {
            this.stopStreaming();
          }
          dispatch({ type: ACTION_TYPES.SET_EVENTS_PAGE, payload });
          dispatch({ type: ACTION_TYPES.QUERY_IS_RUNNING, payload: false });
        }
      },
      ...commonHandlers(dispatch)
    };
    fetchStreamingEvents(queryNode, language, streamLimit, streamBatch, handlers);
  };
};

export const toggleSelectAllEvents = () => ({
  type: ACTION_TYPES.TOGGLE_SELECT_ALL_EVENTS
});

export const toggleEventSelection = ({ sessionId }) => {
  return (dispatch, getState) => {
    const state = getState().investigate.eventResults;
    const { allEventsSelected, selectedEventIds, data } = state;

    if (allEventsSelected) {
      // if all events already selected and one event is toggled
      // toggle allEventsSelected and also select all event ids minus the one just toggled
      dispatch(toggleSelectAllEvents());
      dispatch({
        type: ACTION_TYPES.SELECT_EVENTS,
        payload: _.without(data.map((d) => d.sessionId), sessionId)
      });
    } else {
      if (selectedEventIds.includes(sessionId)) {
        // otherwise, if the event is already selected, deselect it
        dispatch({ type: ACTION_TYPES.DESELECT_EVENT, payload: sessionId });
      } else {
        if (selectedEventIds.length === (getState().investigate.eventCount.data - 1)) {
          // if the event is not already selected, but it's the last unselected event
          // toggle allEventsSelected
          dispatch(toggleSelectAllEvents());
        } else {
          // lastly, if the toggled event is not already selected, and is not the last unselected event
          // select the event
          dispatch({ type: ACTION_TYPES.SELECT_EVENTS, payload: [sessionId] });
        }
      }
    }
  };
};

/**
 * Streams additional events for the current query.
 * @public
 */
export const eventsGetMore = () => {
  return (dispatch, getState) => {
    const state = getState().investigate;
    const queryNode = getActiveQueryNode(getState());
    const { language } = state.dictionaries;
    const { data, streamLimit, streamGoal, streamBatch } = state.eventResults;

    const len = data.length || 0;
    const anchor = len;
    const goal = len + streamGoal;
    const lastSessionId = len ? data[len - 1].sessionId : null;
    const handlers = {
      onInit(stopStream) {
        this.stopStreaming = stopStream;
        dispatch(getEventCount());
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
          dispatch({ type: ACTION_TYPES.QUERY_IS_RUNNING, payload: false });
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
