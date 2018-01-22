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
  queryTimeFormat: undefined,
  serviceId: undefined,
  sessionId: undefined,
  startTime: 0,
  atLeastOneQueryIssued: false
});

export default handleActions({
  [ACTION_TYPES.SET_PREFERENCES]: (state, { payload }) => {
    return state.set('queryTimeFormat', payload.queryTimeFormat);
  },

  [ACTION_TYPES.SET_QUERY_FILTER_META]: (state, { payload }) => {
    return Immutable.setIn(state, ['metaFilter', 'conditions'], payload);
  },

  [ACTION_TYPES.REHYDRATE]: (state, { payload }) => {
    const reducerState = {};
    const qn = (payload && payload.investigate && payload.investigate.queryNode) ? payload.investigate.queryNode : null;
    if (qn) {
      reducerState.previouslySelectedTimeRanges = qn.previouslySelectedTimeRanges;
      // if state already has a serviceId, use that one instead, because
      // that is coming from parsing the url and we do not want to use
      // the one stored in localStorage
      if (!state.serviceId) {
        reducerState.serviceId = qn.serviceId;
      }
    }
    return state.merge(reducerState);
  },

  [ACTION_TYPES.INITIALIZE_TESTS]: (state, { payload }) => {
    return _initialState.merge(payload.queryNode, { deep: true });
  },

  [ACTION_TYPES.INITIALIZE_INVESTIGATE]: (state, { payload, hardReset }) => {
    if (hardReset) {
      return _initialState;
    } else {
      return state.merge({
        endTime: payload.endTime && parseInt(payload.endTime, 10) || 0,
        eventMetas: undefined,
        metaFilter: payload.metaFilter,
        queryString: '',
        serviceId: payload.serviceId,
        sessionId: payload.sessionId && parseInt(payload.sessionId, 10) || undefined,
        startTime: payload.startTime && parseInt(payload.startTime, 10) || 0
      }, { deep: true });
    }
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
  },

  [ACTION_TYPES.SET_EVENTS_PAGE]: (state) => {
    return state.set('atLeastOneQueryIssued', true);
  }
}, _initialState);
