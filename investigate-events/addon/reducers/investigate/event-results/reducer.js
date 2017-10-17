import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';

import * as ACTION_TYPES from 'investigate-events/actions/types';

const _initialState = Immutable.from({
  status: undefined,
  data: [],
  reason: undefined,
  anchor: 0,
  goal: 0,
  streamLimit: 1000,
  streamGoal: 100,
  streamBatch: 50,
  metaKeyStates: [],
  message: undefined
});

export default handleActions({
  [ACTION_TYPES.SET_ANCHOR]: (state, { payload }) => state.set('anchor', payload),

  [ACTION_TYPES.SET_GOAL]: (state, { payload }) => state.set('goal', payload),

  [ACTION_TYPES.INIT_EVENTS_STREAMING]: (state) => {
    return state.merge({
      anchor: 0,
      data: [],
      goal: state.streamGoal,
      message: undefined,
      reason: undefined,
      status: 'streaming'
    });
  },

  [ACTION_TYPES.SET_EVENTS_PAGE]: (state, { payload }) => {
    return state.set('data', state.data.concat(payload));
  },

  [ACTION_TYPES.SET_EVENTS_PAGE_STATUS]: (state, { payload }) => {
    return state.set('status', payload);
  },

  [ACTION_TYPES.SET_EVENTS_PAGE_ERROR]: (state, { payload }) => {
    return state.merge(payload);
  },

  [ACTION_TYPES.GET_LOG]: (state, action) => {
    return handle(state, action, {
      start: (s) => {
        // Mark the event objects as waiting.
        // events.forEach((item) => {
        //   setEventLogDataStatus(item, 'wait');
        // });
        return s;
      },
      failure: (s) => {
        // The request won't complete, so mark any events still pending as error.
        // events
        // .filter((item) => {
        //   return getEventLogDataStatus(item) === 'wait';
        // })
        // .forEach((item) => {
        //   setEventLogDataStatus(item, 'rejected');
        // });
        return s;
      },
      success: (s) => {
        // const { data: { sessionId, log, code } } = action.payload;
        // Each event (i.e., sessionId) gets its own response message with its own error code.
        // const item = events.findBy('sessionId', sessionId);
        // if (item) {
        //   if (code) {
        //     // Any non-zero code means there was an error.
        //     setEventLogDataStatus(item, 'rejected');
        //   } else {
        //     // No error, cache the log data into the event object itself.
        //     setEventLogData(item, log);
        //     setEventLogDataStatus(item, 'resolved');
        //   }
        // }
        return s;
      }
    });
  }
}, _initialState);

