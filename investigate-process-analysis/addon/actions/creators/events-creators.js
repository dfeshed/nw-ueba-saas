import fetchStreamingEvents from 'investigate-shared/actions/api/investigate-events/events';
import * as ACTION_TYPES from 'investigate-process-analysis/actions/types';
import _ from 'lodash';

// Common functions.
const commonHandlers = function(dispatch) {
  return {
    onError(response = {}) {
      const { meta } = response;
      const message = (meta) ? meta.message : undefined;
      dispatch({
        type: ACTION_TYPES.SET_EVENTS_PAGE_ERROR,
        payload: { error: message, streaming: false }
      });
    },
    onCompleted() {
      dispatch({ type: ACTION_TYPES.COMPLETED_EVENTS_STREAMING });
    }
  };
};

const _getMetaFilter = (agentId, processName) => {
  return {
    conditions: [
      {
        meta: 'agent.id',
        operator: '=',
        value: `'${agentId}'`
      },
      {
        meta: 'action',
        operator: '=',
        value: '\'createProcess\''
      },
      {
        meta: 'device.type',
        operator: '=',
        value: '\'nwendpoint\''
      },
      {
        meta: 'filename.src',
        operator: '=',
        value: `'${processName}'`
      }
    ]
  };
};
/**
 * Fetches a stream of events for the given query node.
 * @public
 */
export const getEvents = () => {
  return (dispatch, getState) => {
    const state = getState();
    const { et, st, pn: processName, sid, aid: agentId } = state.processAnalysis.processTree.queryInput;

    const queryNode = {
      endTime: et,
      startTime: st,
      queryTimeFormat: 'DB',
      serviceId: sid,
      metaFilter: _getMetaFilter(agentId, processName)
    };

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
          payload.forEach(_hasherizeEventMeta);
          dispatch({ type: ACTION_TYPES.SET_EVENTS, payload });
        }
      },
      ...commonHandlers(dispatch)
    };
    fetchStreamingEvents(queryNode, null, streamLimit, streamBatch, handlers);
  };
};


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
      if (meta[0] === 'filename.dst') {
        event.processName = meta[1];
      }
      if (meta[0] === 'agent.id') {
        event.agentId = meta[1];
      }
      event[meta[0]] = meta[1];
    }
    event.id = _.uniqueId('event_'); // Adding unique id to node, currently server is not sending
    event.metas = null;
    event.children = [];
  }
};
