import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';

import * as ACTION_TYPES from 'investigate-events/actions/types';

const _initialState = Immutable.from({
  serviceId: undefined,
  sessionId: undefined,
  startTime: 0,
  endTime: 0,
  metaFilter: {
    uri: undefined,
    conditions: []
  },
  results: {
    events: {
      status: undefined,
      data: undefined,
      reason: undefined,
      anchor: 0,
      goal: 0
    },
    eventCount: {
      data: undefined,
      reason: undefined,
      status: undefined
    },
    eventTimeline: {
      data: undefined,
      reason: undefined,
      status: undefined
    },
    metaKeyStates: []
  }
});

export default handleActions({
  [ACTION_TYPES.INITIALIZE]: (state, { payload }) => {
    return _initialState.merge(payload.queryNode, { deep: true });
  },

  [ACTION_TYPES.SESSION_SELECTED]: (state, { payload }) => {
    return state.set('sessionId', payload);
  },

  [ACTION_TYPES.SERVICE_SELECTED]: (state, { payload }) => {
    return state.set('serviceId', payload);
  },

  [ACTION_TYPES.SET_QUERY_PARAMS]: (state, { payload }) => {
    return state.merge(payload);
  },

  [ACTION_TYPES.SET_QUERY_TIME_RANGE]: (state, { payload }) => {
    return state.merge({
      endTime: payload.endTime,
      startTime: payload.startTime
    });
  }
}, _initialState);
