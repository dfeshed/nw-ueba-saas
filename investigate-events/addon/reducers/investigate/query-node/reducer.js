import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';
import * as ACTION_TYPES from 'investigate-events/actions/types';

const _initialState = Immutable.from({
  atLeastOneQueryIssued: false,
  endTime: 0,
  eventMetas: undefined,
  hasIncommingQueryParams: false,
  metaFilter: {
    uri: undefined,
    conditions: []
  },
  previouslySelectedTimeRanges: {},

  // we save off the old query params for the previously executed
  // query so that we can use them for API calls after the user
  // changes data (like the service) in the UI but hasn't executed
  // the query. If you change the service, for instance, but do not
  // execute the query, and then click to open Recon, we need to use
  // the previous service, not the changed one.
  previousQueryParams: undefined,
  queryTimeFormat: undefined,
  serviceId: undefined,
  sessionId: undefined,
  startTime: 0,
  queryView: 'nextGen'
});

const _cloneQueryParams = (state) => {
  const {
    endTime,
    eventMetas,
    serviceId,
    startTime
  } = state;

  let { metaFilter } = state;

  if (!metaFilter) {
    metaFilter = {
      uri: undefined,
      conditions: []
    };
  }

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

  [ACTION_TYPES.INITIALIZE_INVESTIGATE]: (state, { payload }) => {
    const localStorageObj = JSON.parse(localStorage.getItem('reduxPersist:investigate'));
    if (payload.hardReset) {
      // Check if the previously selected serviceId and timeRange are persisted in localStorage
      if (!localStorageObj) {
        return _initialState;
      } else {
        // pre-populate Event Analysis with previously chosen serviceId and timeRange
        const previousQueryParams = _cloneQueryParams(localStorageObj.queryNode);
        return state.merge({
          ..._initialState,
          serviceId: localStorageObj.queryNode.serviceId,
          previouslySelectedTimeRanges: localStorageObj.queryNode.previouslySelectedTimeRanges,
          queryView: localStorageObj.queryNode.queryView,
          previousQueryParams
        });
      }

    } else {
      // pull out previously selected view (if present)
      let previousView;
      if (localStorageObj) {
        previousView = localStorageObj.queryNode.queryView;
      }
      const { queryParams } = payload;
      const hasIncommingQueryParams = !!(queryParams.endTime && queryParams.serviceId && queryParams.startTime);
      const { previouslySelectedTimeRanges } = state;
      const newRange = {};
      newRange[queryParams.serviceId] = queryParams.selectedTimeRangeId;
      return state.merge({
        endTime: queryParams.endTime && parseInt(queryParams.endTime, 10) || 0,
        eventMetas: undefined,
        hasIncommingQueryParams,
        metaFilter: queryParams.metaFilter,
        serviceId: queryParams.serviceId,
        previouslySelectedTimeRanges: previouslySelectedTimeRanges.merge(newRange),
        sessionId: queryParams.sessionId && parseInt(queryParams.sessionId, 10) || undefined,
        startTime: queryParams.startTime && parseInt(queryParams.startTime, 10) || 0,
        queryView: previousView ? previousView : state.queryView,
        previousQueryParams: _cloneQueryParams(queryParams)
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
    return state.set('queryView', payload.queryView);
  },

  [ACTION_TYPES.SERVICE_SELECTED]: (state, { payload }) => {
    return state.set('serviceId', payload);
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

  [ACTION_TYPES.SET_RECON_VIEWABLE]: (state, { payload: { eventData } }) => {
    return state.merge({ ...eventData });
  },

  [ACTION_TYPES.SET_EVENTS_PAGE]: (state) => {
    return state.merge({ atLeastOneQueryIssued: true });
  }

}, _initialState);
