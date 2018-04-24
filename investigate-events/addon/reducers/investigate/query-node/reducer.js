import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';
import * as ACTION_TYPES from 'investigate-events/actions/types';

const _initialState = Immutable.from({
  atLeastOneQueryIssued: false,
  endTime: 0,
  eventMetas: undefined,
  hasIncommingQueryParams: false,
  isDirty: false,
  metaFilter: {
    uri: undefined,
    conditions: []
  },
  previouslySelectedTimeRanges: {},
  previousQueryParams: undefined,
  queryTimeFormat: undefined,
  serviceId: undefined,
  sessionId: undefined,
  startTime: 0,
  queryView: 'guided',
  freeFormText: undefined,
  toggledOnceFlag: false
});

const _cloneQueryParams = (state) => {
  const { endTime, eventMetas, metaFilter, serviceId, startTime } = state;
  const _eventMetas = eventMetas ? JSON.parse(JSON.stringify(eventMetas)) : undefined;
  return {
    endTime,
    eventMetas: _eventMetas,
    metaFilter: JSON.parse(JSON.stringify(metaFilter)),
    serviceId,
    startTime
  };
};

export default handleActions({
  [ACTION_TYPES.SET_PREFERENCES]: (state, { payload }) => {
    return state.set('queryTimeFormat', payload.queryTimeFormat || state.queryTimeFormat);
  },

  [ACTION_TYPES.REHYDRATE]: (state, { payload }) => {
    const reducerState = {};
    const qn = (payload && payload.investigate && payload.investigate.queryNode) ? payload.investigate.queryNode : null;
    if (qn) {
      // if state already has a serviceId and previouslySelectedTimeRanges, use that one instead, because
      // that is coming from parsing the url and we do not want to use
      // the one stored in localStorage
      if (!state.serviceId) {
        reducerState.serviceId = qn.serviceId;
      }
      if (!state.previouslySelectedTimeRanges[state.serviceId]) {
        reducerState.previouslySelectedTimeRanges = qn.previouslySelectedTimeRanges;
      }
    }
    return state.merge(reducerState);
  },

  [ACTION_TYPES.INITIALIZE_TESTS]: (state, { payload }) => {
    return _initialState.merge(payload.queryNode, { deep: true });
  },

  [ACTION_TYPES.INITIALIZE_INVESTIGATE]: (state, { payload, hardReset }) => {
    if (hardReset) {
      // Check if the previously selected serviceId and timeRange are persisted in localStorage
      const localStorageObj = JSON.parse(localStorage.getItem('reduxPersist:investigate'));
      if (!localStorageObj) {
        return _initialState;
      } else {
        // pre-populate Event Analysis with previously chosen serviceId and timeRange
        return state.merge({
          ..._initialState,
          serviceId: localStorageObj.queryNode.serviceId,
          previouslySelectedTimeRanges: localStorageObj.queryNode.previouslySelectedTimeRanges
        });
      }

    } else {
      const hasIncommingQueryParams = !!(payload.endTime && payload.serviceId && payload.startTime);
      const { previouslySelectedTimeRanges } = state;
      const newRange = {};
      newRange[payload.serviceId] = payload.selectedTimeRangeId;
      return state.merge({
        endTime: payload.endTime && parseInt(payload.endTime, 10) || 0,
        eventMetas: undefined,
        hasIncommingQueryParams,
        metaFilter: payload.metaFilter,
        serviceId: payload.serviceId,
        previouslySelectedTimeRanges: previouslySelectedTimeRanges.merge(newRange),
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

  [ACTION_TYPES.SET_QUERY_VIEW]: (state, { payload }) => {
    return state.set('queryView', payload);
  },

  [ACTION_TYPES.SET_FREE_FORM_TEXT]: (state, { payload }) => {
    return state.set('freeFormText', payload);
  },

  [ACTION_TYPES.TOGGLE_FOCUS_FLAG]: (state, { payload }) => {
    return state.set('toggledOnceFlag', payload);
  },

  [ACTION_TYPES.SERVICE_SELECTED]: (state, { payload }) => {
    // Even though we highlight the query button when a query is dirty, we don't
    // prevent a user from interacting with the UI in a way that could make API
    // calls with incorrect parameters. So, we save off relevent query params
    // when the service is changed so we can use those for API calls. We clear
    // those out when the query is marked as clean. See MARK_QUERY_DIRTY.
    // We do `state.previousQueryParams || _cloneQueryParams(state)` to handle
    // the situation where a user selects multiple services in a row. We want to
    // save off the old params only for the first change.
    return state.merge({
      isDirty: true,
      previousQueryParams: state.previousQueryParams || _cloneQueryParams(state),
      serviceId: payload
    });
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
      isDirty: true,
      startTime: payload.startTime,
      previouslySelectedTimeRanges: previouslySelectedTimeRanges.merge(newRange)
    });
  },

  [ACTION_TYPES.SET_RECON_VIEWABLE]: (state, { payload: { eventData } }) => {
    return state.merge({ ...eventData });
  },

  [ACTION_TYPES.SET_EVENTS_PAGE]: (state) => {
    return state.merge({ atLeastOneQueryIssued: true });
  },

  /**
   * Marks a query as clean or dirty depending on the Boolean payload. If a
   * query is clean, we remove "dirty" properties.
   * @public
   */
  [ACTION_TYPES.MARK_QUERY_DIRTY]: (state, { payload }) => {
    // If a query is not "dirty", remove saved query params
    return state.merge({
      isDirty: payload,
      previousQueryParams: payload ? state.previousQueryParams : undefined
    });
  }
}, _initialState);
