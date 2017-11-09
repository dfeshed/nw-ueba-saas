import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';

import * as ACTION_TYPES from 'investigate-events/actions/types';

const _initialState = Immutable.from({
  endTime: 0,
  eventMetas: undefined,
  metaFilter: {
    uri: undefined,
    conditions: []
  },
  previouslySelectedTimeRanges: {},
  queryString: '',
  serviceId: undefined,
  sessionId: undefined,
  startTime: 0
});

export default handleActions({
  [ACTION_TYPES.SET_QUERY_FILTER_META]: (state, { payload }) => {
    return Immutable.setIn(state, ['metaFilter', 'conditions'], payload);
  },

  [ACTION_TYPES.REHYDRATE]: (state, { payload }) => {
    let reducerState = {};
    if (payload && payload.investigate && payload.investigate.queryNode) {
      reducerState = payload.investigate.queryNode;
    }
    return state.merge(reducerState);
  },

  [ACTION_TYPES.INITIALIZE_TESTS]: (state, { payload }) => {
    return _initialState.merge(payload.queryNode, { deep: true });
  },

  [ACTION_TYPES.INITIALIZE_INVESTIGATE]: (state, { payload }) => {
    let sessionId;
    if (payload.sessionId && !Number.isNaN(payload.sessionId)) {
      sessionId = parseInt(payload.sessionId, 10);
    }
    return state.merge({
      endTime: payload.endTime,
      eventMetas: undefined,
      metaFilter: payload.metaFilter,
      queryString: '',
      serviceId: payload.serviceId,
      sessionId,
      startTime: payload.startTime
    }, { deep: true });
  },

  [ACTION_TYPES.RESET_QUERYNODE]: (state) => {
    return state.merge(_initialState, { deep: true });
  },

  [ACTION_TYPES.SESSION_SELECTED]: (state, { payload }) => {
    return state.set('sessionId', payload);
  },

  [ACTION_TYPES.SERVICE_SELECTED]: (state, { payload }) => {
    return state.set('serviceId', payload);
  },

  [ACTION_TYPES.SET_QUERY_PARAMS_FOR_TESTS]: (state, { payload }) => {
    return state.merge(payload);
  },

  [ACTION_TYPES.SET_QUERY_TIME_RANGE]: (state, { payload }) => {
    const { previouslySelectedTimeRanges, serviceId } = state;
    const newRange = {};
    newRange[serviceId] = payload.selectedTimeRangeId;
    return state.merge({
      endTime: payload.endTime,
      startTime: payload.startTime,
      previouslySelectedTimeRanges: previouslySelectedTimeRanges.merge(newRange)
    });
  },

  [ACTION_TYPES.SET_SELECTED_EVENT]: (state, { payload }) => {
    return state.merge(payload);
  }
}, _initialState);
