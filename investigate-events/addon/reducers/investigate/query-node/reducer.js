import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';
import _ from 'lodash';
import { handle } from 'redux-pack';

import * as ACTION_TYPES from 'investigate-events/actions/types';
import { createQueryHash } from 'investigate-events/util/query-hash';

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
  queryView: 'nextGen',

  // NEXT GEN, keeping separated until chance to clean up reducer

  // Stores all data for pills
  pillsData: [],

  // Tracks whether or not server side validation is under way
  serverSideValidationInProcess: false,

  // Stores a hash of query inputs for the last executed query
  currentQueryHash: undefined,

  // stores updates to free form text that haven't
  // yet been converted into pill form (pillsData)
  updatedFreeFormTextPill: undefined
});

const _initialPillState = {
  id: undefined,
  meta: undefined,
  operator: undefined,
  value: undefined,
  complexFilterText: undefined,

  isEditing: false,
  isSelected: false,
  isInvalid: false,
  validationError: undefined
};

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

// Takes in state and a new pill, finds the old pill in
// state and replaces it with a new version
const _replacePill = (state, pillData) => {
  const position = state.pillsData.map((pD) => pD.id).indexOf(pillData.id);

  const newPillData = {
    ...pillData,
    id: _.uniqueId('nextGenPill_')
  };

  return Immutable.from([
    ...state.pillsData.slice(0, position),
    { ...newPillData },
    ...state.pillsData.slice(position + 1)
  ]);
};

const handlePillSelection = (state, payload, isSelected) => {
  const { pillData } = payload;
  const selectIds = pillData.map((pD) => pD.id);
  const newPillsData = state.pillsData.map((pD) => {
    if (selectIds.includes(pD.id)) {
      return {
        ...pD,
        id: _.uniqueId('nextGenPill_'),
        isSelected
      };
    }

    return pD;
  });
  return state.set('pillsData', newPillsData);
};

const _handlePillUpdate = (state, pillData) => {
  const newPillsData = _replacePill(state, pillData);
  return state.set('pillsData', newPillsData);
};

const _replaceAllPills = (state, pillData) => {
  const newPills = pillData.map((pD) => {
    return {
      ..._initialPillState,
      ...pD,
      id: _.uniqueId('nextGenPill_')
    };
  });
  return state.set('pillsData', newPills);
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

      const newHash = createQueryHash(
        queryParams.serviceId,
        queryParams.startTime,
        queryParams.endTime,
        queryParams.metaFilter.conditions
      );

      state = _replaceAllPills(state, queryParams.metaFilter.conditions);

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
        previousQueryParams: _cloneQueryParams(queryParams),
        updatedFreeFormTextPill: undefined,
        currentQueryHash: newHash
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
  },

  // START NEXT GEN

  [ACTION_TYPES.ADD_NEXT_GEN_PILL]: (state, { payload }) => {
    const { pillData, position } = payload;
    const newPillData = {
      ..._initialPillState,
      ...pillData,
      id: _.uniqueId('nextGenPill_')
    };
    if (state.pillsData.length === 0) {
      return state.set('pillsData', Immutable.from([ newPillData ]));
    }

    return state.set('pillsData', Immutable.from([
      ...state.pillsData.slice(0, position),
      newPillData,
      ...state.pillsData.slice(position)
    ]));
  },

  [ACTION_TYPES.EDIT_NEXT_GEN_PILL]: (state, { payload }) => {
    return _handlePillUpdate(state, payload.pillData);
  },

  [ACTION_TYPES.VALIDATE_NEXT_GEN_PILL]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('serverSideValidationInProcess', !!action.meta.isServerSide),
      failure: (s) => {
        const { meta: { position } } = action;
        const { pillsData } = s;
        const currentPill = pillsData[position];
        const validatedPill = {
          ...currentPill,
          isInvalid: !!action.payload.meta,
          validationError: action.payload.meta
        };
        const newPillsData = _replacePill(s, validatedPill);
        return s.merge({ pillsData: newPillsData, serverSideValidationInProcess: false });
      },
      success: (s) => s.set('serverSideValidationInProcess', false)
    });
  },

  [ACTION_TYPES.DELETE_NEXT_GEN_PILLS]: (state, { payload }) => {
    const { pillData } = payload;
    const deleteIds = pillData.map((pD) => pD.id);
    const newPills = state.pillsData.filter((pD) => !deleteIds.includes(pD.id));
    return state.set('pillsData', newPills);
  },

  [ACTION_TYPES.SELECT_NEXT_GEN_PILLS]: (state, { payload }) => {
    return handlePillSelection(state, payload, true);
  },

  [ACTION_TYPES.DESELECT_NEXT_GEN_PILLS]: (state, { payload }) => {
    return handlePillSelection(state, payload, false);
  },

  [ACTION_TYPES.OPEN_NEXT_GEN_PILL_FOR_EDIT]: (state, { payload }) => {
    const newPillData = {
      ...payload.pillData,
      isSelected: false,
      isEditing: true
    };
    return _handlePillUpdate(state, newPillData);
  },

  [ACTION_TYPES.REPLACE_ALL_NEXT_GEN_PILLS]: (state, { payload }) => {
    return _replaceAllPills(state, payload.pillData);
  },

  [ACTION_TYPES.UPDATE_FREE_FORM_TEXT]: (state, { payload }) => {
    return state.set('updatedFreeFormTextPill', payload.pillData);
  },

  [ACTION_TYPES.RESET_NEXT_GEN_PILL]: (state, { payload }) => {
    const { id } = payload.pillData;
    // Reset the id of the pill and then
    // reset all the flags back to initial state
    const newPillsData = state.pillsData.map((pD) => {
      if (id === pD.id) {
        return {
          ...pD,
          id: _.uniqueId('nextGenPill_'),
          isEditing: false,
          isSelected: false,
          isInvalid: false,
          validationError: undefined
        };
      }

      return pD;
    });
    return state.set('pillsData', newPillsData);
  }

}, _initialState);
