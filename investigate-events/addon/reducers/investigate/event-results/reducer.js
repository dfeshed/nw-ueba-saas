import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';

import * as ACTION_TYPES from 'investigate-events/actions/types';

const _initialState = Immutable.from({
  status: undefined,
  data: [], // *
  reason: undefined,
  anchor: 0,
  goal: 0,
  streamLimit: 1000,
  streamGoal: 100,
  streamBatch: 50,
  metaKeyStates: [],
  message: undefined
});
// * `data` is an array of objects with the following properties
// {
//   sessionId,
//   time,
//   metas,
//   ...metas converted to props,
//   log, // if log
//   logStatus
// }

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

  [ACTION_TYPES.SET_LOG]: (state, { payload }) => {
    const { code, data: { log, sessionId } } = payload;
    const item = _find(state.data, sessionId);
    let updatedItem;
    if (code) {
      // codes other than 0 are errors
      updatedItem = item.set('logStatus', 'rejected');
    } else {
      // set new log and status
      updatedItem = item.merge({ log, logStatus: 'resolved' });
    }
    // replace event in array
    const newData = _update(state.data, sessionId, updatedItem);
    return state.set('data', newData);
  },

  [ACTION_TYPES.SET_LOG_STATUS]: (state, { sessionId, status }) => {
    // Update event's logStatus property
    const item = _find(state.data, sessionId);
    const updatedItem = item.set('logStatus', status);
    // replace event in array
    const newData = _update(state.data, sessionId, updatedItem);
    return state.set('data', newData);
  }
}, _initialState);

const _find = (data, sessionId) => data.find((d) => d.sessionId === sessionId);

const _update = (data, sessionId, updatedItem) => data.map((d) => {
  return (d.sessionId === sessionId) ? updatedItem : d;
});
