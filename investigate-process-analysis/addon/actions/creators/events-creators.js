import fetchStreamingEvents from 'investigate-shared/actions/api/investigate-events/events';
import * as ACTION_TYPES from 'investigate-process-analysis/actions/types';
import { handleInvestigateErrorCode } from 'component-lib/utils/error-codes';
import { getQueryNode, hasherizeEventMeta } from './util';

const callbacksDefault = { onComplete() {} };

// Common functions.
const commonHandlers = function(dispatch, callbacks) {
  return {
    onError(response = {}) {
      const errorObj = handleInvestigateErrorCode(response);
      dispatch({
        type: ACTION_TYPES.SET_EVENTS_PAGE_ERROR,
        payload: { error: errorObj.serverMessage, streaming: false }
      });
    },
    onCompleted() {
      dispatch({ type: ACTION_TYPES.GET_EVENTS_COUNT_SAGA, onComplete: callbacks.onComplete });
    }
  };
};


/**
 * Fetches a stream of events for the given query node.
 * @public
 */
export const getEvents = (selectedNode, callbacks = callbacksDefault) => {
  return (dispatch, getState) => {
    const state = getState();
    const queryNode = getQueryNode(state.processAnalysis.processTree.queryInput, selectedNode);

    const streamLimit = 100000;
    const streamBatch = 100000; // Would like to get all the events in one batch

    const handlers = {
      onInit(stopStream) {
        this.stopStreaming = stopStream;
        dispatch({ type: ACTION_TYPES.INIT_EVENTS_STREAMING });
      },
      onResponse(response) {
        const { data: _payload, meta } = response || {};
        const payload = Array.isArray(_payload) ? _payload : [];
        const description = meta ? meta.description : null;
        const percent = meta ? meta.percent : 0;
        if (description === 'Queued' ||
           (description === 'Executing' && percent < 100 && payload.length === 0)) {
          return;
        } else {
          payload.forEach(hasherizeEventMeta);
          dispatch({ type: ACTION_TYPES.SET_EVENTS, payload });
        }
      },
      ...commonHandlers(dispatch, callbacks)
    };
    fetchStreamingEvents(queryNode, null, streamLimit, streamBatch, handlers);
  };
};
