import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';
import _ from 'lodash';
import { handle } from 'redux-pack';
import { isEmpty } from '@ember/utils';

import * as ACTION_TYPES from 'investigate-events/actions/types';
import { createQueryHash } from 'investigate-events/util/query-hash';
import { createParens } from 'investigate-events/util/query-parsing';
import { pillBeingEdited, focusedPill } from './selectors';
import TIME_RANGES from 'investigate-shared/constants/time-ranges';

const { log } = console; // eslint-disable-line no-unused-vars

const ID_PREFIX = 'guidedPill_';

const _initialState = Immutable.from({
  atLeastOneQueryIssued: false,
  endTime: 0,
  timeRangeInvalid: undefined,
  hasIncommingQueryParams: false,
  metaFilter: [],
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
  queryView: 'guided',

  // GUIDED, keeping separated until chance to clean up reducer

  // Stores all data for pills
  pillsData: [],

  // Is a query in progress. This possibly includes server
  // validation if that is taking a long time to return
  isQueryRunning: false,

  // Stores a hash of query inputs for the last executed query
  currentQueryHash: undefined,

  // stores updates to free form text that haven't
  // yet been converted into pill form (pillsData)
  updatedFreeFormTextPill: undefined,

  // pill data being used for a query gets saved server side
  // as a series of ids so that URLs do not grow unbounded
  // and get unexpectedly truncated. This array tracks
  // the pillDataHashes for the current query
  pillDataHashes: undefined,

  // Default list of most recent queries used to display when
  // no text is supplied to API.
  // This list is updated every time a new predicate is added and
  // also when we enter the route for the first time.
  // This is required to prevent calling the API everytime user
  // backspaces, focuses on the query bar.
  recentQueriesUnfilteredList: [],

  // Holds queries from query history stack which match the provided
  // recentQueriesFilterText
  recentQueriesFilteredList: [],
  recentQueriesFilterText: undefined,
  recentQueriesCallInProgress: false

});

const _initialPillState = {
  complexFilterText: undefined,
  id: undefined,
  isEditing: false,
  isFocused: false,
  isInvalid: false,
  isSelected: false,
  isValidationInProgress: false,
  meta: undefined,
  operator: undefined,
  searchTerm: undefined,
  validationError: undefined,
  value: undefined
};

const _cloneQueryParams = (state) => {
  const {
    endTime,
    serviceId,
    startTime
  } = state;

  let { pillsData } = state;

  if (!pillsData) {
    pillsData = [];
  }

  return {
    endTime,
    metaFilter: JSON.parse(JSON.stringify(pillsData)),
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
    id: _.uniqueId(ID_PREFIX)
  };

  return Immutable.from([
    ...state.pillsData.slice(0, position),
    { ...newPillData },
    ...state.pillsData.slice(position + 1)
  ]);
};

const handlePillSelection = (state, pillData, isSelected) => {

  const selectIds = pillData.map((pD) => pD.id);
  const newPillsData = state.pillsData.map((pD) => {
    if (selectIds.includes(pD.id)) {
      return {
        ...pD,
        id: _.uniqueId(ID_PREFIX),
        isSelected
      };
    }

    return pD;
  });
  return state.set('pillsData', newPillsData);
};

const _shouldRemoveFocus = (selectedOrDeselectedPills, focusedPillData) => {
  // Pills only have a focus side-effect if there is one pill being
  // selected or deselected, because when multiple pills are being
  // selected/deselected we do not touch any focus state
  if (selectedOrDeselectedPills.length === 1) {
    const [pill] = selectedOrDeselectedPills;

    // If there isn't currently a focused pill, we do not need to
    // worry about cleaning up current focus
    if (focusedPillData) {

      // If the focused pill IS the pill being acted upon
      // there is no focus side effect.
      const isFocusedPillSameAsSelectedDeselectedPill = focusedPillData.id !== pill.id;

      return isFocusedPillSameAsSelectedDeselectedPill;
    }
  }
  return false;
};

const _handlePillFocus = (state, selectedOrDeselectedPills, shouldIgnoreFocus = false, isSelected) => {

  // if shouldIgnoreFocus is passed in explicitly, due to multiple
  // selected/deselected pills, no need to handle focus
  if (shouldIgnoreFocus) {
    return handlePillSelection(state, selectedOrDeselectedPills, isSelected);
  } else {
    const focusedPillData = focusedPill({ investigate: { queryNode: state } });
    let newState = state;

    // If existing focus state needs to change based on select/deselect,
    // then remove old focus
    if (_shouldRemoveFocus(selectedOrDeselectedPills, focusedPillData)) {
      newState = _removeFocus(state);
    }

    // Continue adding focus to a pill if the array contains just 1 pill
    if (selectedOrDeselectedPills.length === 1) {
      const [needsFocusPill] = selectedOrDeselectedPills;
      newState = _addFocus(newState, needsFocusPill, isSelected);
    }
    return newState;
  }
};

const _removeFocus = (state) => {
  const { pillsData } = state;
  const newPillsData = pillsData.map((pill) => {
    if (pill.isFocused) {
      return {
        ...pill,
        id: _.uniqueId(ID_PREFIX),
        isFocused: false
      };
    }
    return pill;
  });
  return state.set('pillsData', newPillsData);
};

const _addFocus = (state, needsFocusPill, isSelected) => {
  const { pillsData } = state;
  const newPillsData = pillsData.map((pill) => {
    if (pill.id === needsFocusPill.id) {
      return {
        ...pill,
        id: _.uniqueId(ID_PREFIX),
        isFocused: true,
        isSelected
      };
    }
    return pill;
  });
  return state.set('pillsData', newPillsData);
};

const _deletePills = (state, pillsToBeDeleted) => {
  // get ids for pills that need to be deleted
  const deleteIds = pillsToBeDeleted.map((pD) => pD.id);
  // remove those pill ids from state
  const newPills = state.pillsData.filter((pD) => !deleteIds.includes(pD.id));
  return state.set('pillsData', newPills);
};

const _handlePillUpdate = (state, pillData) => {
  const newPillsData = _replacePill(state, pillData);
  return state.set('pillsData', newPillsData);
};

const _replaceAllPills = (state, pillData, pillHashes) => {
  const newPills = pillData.map((pD) => {
    return {
      ..._initialPillState,
      ...pD,
      id: _.uniqueId(ID_PREFIX)
    };
  });

  state = state.set('pillsData', newPills);
  if (pillHashes) {
    state = state.set('pillDataHashes', pillHashes);
  }

  return state;
};

const _updatePillProperties = (state, position, updatedProperties) => {
  const { pillsData } = state;
  const currentPill = pillsData[position];
  const updatedPill = {
    ...currentPill,
    ...updatedProperties
  };
  return _replacePill(state, updatedPill);
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
    // payload.hardReset will true when
    // 1) Loading the Event Analysis page for the first time
    // 2) Clicking on Event Analysis tab from the results page causing it to reset to previously chosen options
    if (payload.hardReset) {
      // Check if the previously selected serviceId and timeRange are persisted in localStorage
      if (!localStorageObj) {
        return _initialState;
      } else {
        // pre-populate Event Analysis with previously chosen serviceId and timeRange
        const previousQueryParams = _cloneQueryParams({
          ...localStorageObj.queryNode,
          pillsData: localStorageObj.queryNode.pillsData || localStorageObj.queryNode.metaFilter
        });

        let endTime, startTime;
        // For non-custom time ranges, we can extract start and endTime just from the timeRangeId.
        // However, for custom timeRange it is not possible as the start and endTime can be arbitrary.
        // If the previouslySelectedTimeRanges was a CUSTOM ID, pull the endTime and startTime from state.
        if (localStorageObj.queryNode.previouslySelectedTimeRanges[localStorageObj.queryNode.serviceId] === TIME_RANGES.CUSTOM_TIME_RANGE_ID) {
          endTime = state.endTime;
          startTime = state.startTime;
        }

        return state.merge({
          ..._initialState,
          serviceId: localStorageObj.queryNode.serviceId,
          endTime: endTime || 0,
          startTime: startTime || 0,
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

      // if initializing and have no hashes and no incoming pill data
      // then set pillsData to empty array. This handles back/forward
      // case where pills data is full, but when going back/forward
      // pillsData should clear out
      if (!queryParams.pillDataHashes && !queryParams.pillData) {
        state = state.set('pillsData', []);
      }

      return state.merge({
        endTime: queryParams.endTime && parseInt(queryParams.endTime, 10) || 0,
        hasIncommingQueryParams,
        serviceId: queryParams.serviceId,
        previouslySelectedTimeRanges: previouslySelectedTimeRanges.merge(newRange),
        sessionId: queryParams.sessionId && parseInt(queryParams.sessionId, 10) || undefined,
        startTime: queryParams.startTime && parseInt(queryParams.startTime, 10) || 0,
        queryView: previousView ? previousView : state.queryView,
        updatedFreeFormTextPill: undefined,
        pillDataHashes: queryParams.pillDataHashes
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
    const _pillBeingEdited = pillBeingEdited({ investigate: { queryNode: state } });

    // Switching view, exit editing of any pill
    if (_pillBeingEdited) {
      const newPillData = {
        ..._pillBeingEdited,
        isEditing: false
      };
      state = _handlePillUpdate(state, newPillData);
    }
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
      timeRangeInvalid: false,
      previouslySelectedTimeRanges: previouslySelectedTimeRanges.merge(newRange)
    });
  },

  /**
   * Need to set the invalid time selections in state to justify the error notification,
   * else the invalid time selection reverts to previous valid time selection, leaving the user confused
   * why the red border persists with the correct time.
   */
  [ACTION_TYPES.SET_TIME_RANGE_ERROR]: (state, { payload }) => {
    const { previouslySelectedTimeRanges, serviceId } = state;
    const newRange = {};
    newRange[serviceId] = payload.selectedTimeRangeId;
    return state.merge({
      endTime: payload.endTime,
      startTime: payload.startTime,
      timeRangeInvalid: true,
      previouslySelectedTimeRanges: previouslySelectedTimeRanges.merge(newRange)
    });
  },

  [ACTION_TYPES.SET_RECON_VIEWABLE]: (state, { payload: { eventData } }) => {
    return state.merge({ ...eventData });
  },

  [ACTION_TYPES.SET_EVENTS_PAGE]: (state) => {
    return state.set('atLeastOneQueryIssued', true);
  },

  [ACTION_TYPES.QUERY_IS_RUNNING]: (state, { payload }) => {
    return state.set('isQueryRunning', payload);
  },

  // START GUIDED

  [ACTION_TYPES.INITIALIZE_QUERYING]: (state) => {
    const { serviceId, startTime, endTime, pillsData } = state;

    const newHash = createQueryHash(
      serviceId,
      startTime,
      endTime,
      pillsData
    );

    const previousQueryParams = _cloneQueryParams({
      serviceId,
      startTime,
      endTime,
      pillsData
    });

    return state.merge({
      metaFilter: pillsData,
      previousQueryParams,
      currentQueryHash: newHash
    }, { deep: true });
  },

  [ACTION_TYPES.ADD_PILL]: (state, { payload }) => {
    const { pillData, position, shouldAddFocusToNewPill, fromFreeFormMode } = payload;
    const newPillData = {
      ..._initialPillState,
      ...pillData,
      isFocused: shouldAddFocusToNewPill,
      id: _.uniqueId(ID_PREFIX)
    };
    // Create a new array of data in two cases.
    // 1. If there were no pills previously
    // 2. If this pill data is coming from Free Form Mode(FFM). In this case we
    // want to treat it like a rewrite of data. Once FFM is merged into Guided,
    // we can remove this condition and the `fromFreeFormMode` param.
    if (state.pillsData.length === 0 || fromFreeFormMode) {
      return state.set('pillsData', Immutable.from([ newPillData ]));
    }

    return state.set('pillsData', Immutable.from([
      ...state.pillsData.slice(0, position),
      newPillData,
      ...state.pillsData.slice(position)
    ]));
  },

  [ACTION_TYPES.BATCH_ADD_PILLS]: (state, { payload }) => {
    const { pillsData, initialPosition } = payload;
    let newPillsData = pillsData.map((pillData) => {
      return {
        ..._initialPillState,
        ...pillData,
        isFocused: false,
        id: _.uniqueId(ID_PREFIX)
      };
    });

    // Remove any text pills after the first, with the exception that if
    // a text pill is already in state, prefer that one.
    let seenFirstTextPill = state.pillsData.some((pill) => {
      return pill.type === 'text';
    });
    newPillsData = newPillsData.filter((pill) => {
      if (pill.type === 'text') {
        if (!seenFirstTextPill) {
          seenFirstTextPill = true;
          return true;
        } else {
          return false;
        }
      } else {
        return true;
      }
    });

    // Focus the last pill being added
    if (newPillsData.length > 0) {
      newPillsData[newPillsData.length - 1].isFocused = true;
    }

    // Create a new array of data only when there were no pills previously
    if (state.pillsData.length === 0) {
      return state.set('pillsData', Immutable.from(newPillsData));
    }

    // If there are pills in state already, put the array in the middle of
    // the current state
    return state.set('pillsData', Immutable.from(
      state.pillsData.slice(0, initialPosition)
        .concat(newPillsData)
        .concat(state.pillsData.slice(initialPosition))
    ));
  },

  [ACTION_TYPES.EDIT_GUIDED_PILL]: (state, { payload }) => {
    const newPillData = {
      ...payload.pillData,
      isEditing: false,
      isFocused: true
    };
    return _handlePillUpdate(state, newPillData);
  },

  [ACTION_TYPES.VALIDATE_GUIDED_PILL]: (state, action) => {
    return handle(state, action, {
      start: (s) => {
        if (!action.meta.isServerSide) {
          const newPillsData = _updatePillProperties(s, action.meta.position, {
            isValidationInProgress: true
          });
          return s.set('pillsData', newPillsData);
        } else {
          return s;
        }
      },
      failure: (s) => {
        const newPillsData = _updatePillProperties(s, action.meta.position, {
          isInvalid: true,
          isValidationInProgress: false,
          validationError: action.payload.meta
        });
        return s.set('pillsData', newPillsData);
      },
      success: (s) => {
        if (action.meta.isServerSide) {
          const newPillsData = _updatePillProperties(s, action.meta.position, {
            isInvalid: false,
            isValidationInProgress: false,
            validationError: undefined
          });
          return s.set('pillsData', newPillsData);
        } else {
          return s;
        }
      }
    });
  },

  [ACTION_TYPES.DELETE_GUIDED_PILLS]: (state, { payload }) => {
    const { pillData } = payload;
    const deletedPillsState = _deletePills(state, pillData);
    return _removeFocus(deletedPillsState);
  },

  [ACTION_TYPES.ADD_FOCUS_GUIDED_PILL]: (state, { payload }) => {
    const { position } = payload;
    const { pillsData } = state;
    const pill = pillsData[position];

    const newPill = {
      ...pill,
      isFocused: true
    };
    const newPillsData = _replacePill(state, newPill);
    return state.set('pillsData', newPillsData);
  },

  [ACTION_TYPES.REMOVE_FOCUS_GUIDED_PILL]: (state, { payload }) => {
    const { pillData } = payload;
    const newPill = {
      ...pillData,
      isFocused: false
    };
    const newPillsData = _replacePill(state, newPill);
    return state.set('pillsData', newPillsData);
  },

  [ACTION_TYPES.SELECT_GUIDED_PILLS]: (state, { payload }) => {
    const { pillData, shouldIgnoreFocus } = payload;
    return _handlePillFocus(state, pillData, shouldIgnoreFocus, true);
  },

  [ACTION_TYPES.DESELECT_GUIDED_PILLS]: (state, { payload }) => {
    const { pillData, shouldIgnoreFocus } = payload;
    return _handlePillFocus(state, pillData, shouldIgnoreFocus, false);
  },

  [ACTION_TYPES.OPEN_GUIDED_PILL_FOR_EDIT]: (state, { payload }) => {
    const newPillData = {
      ...payload.pillData,
      isSelected: false,
      isFocused: false,
      isEditing: true
    };
    return _handlePillUpdate(state, newPillData);
  },

  [ACTION_TYPES.REPLACE_ALL_GUIDED_PILLS]: (state, { payload }) => {
    return _replaceAllPills(state, payload.pillData, payload.pillHashes);
  },

  [ACTION_TYPES.UPDATE_FREE_FORM_TEXT]: (state, { payload }) => {
    return state.set('updatedFreeFormTextPill', payload.pillData);
  },

  [ACTION_TYPES.RESET_GUIDED_PILL]: (state, { payload }) => {
    const { id } = payload.pillData;
    // Set the id of the pill to a new value, then set the flags related
    // to editing back to their initial state, except for the isFocused flag.
    // The edit cancelled pill should regain focus.
    const newPillsData = state.pillsData.map((pD) => {
      if (id === pD.id) {
        return {
          ...pD,
          id: _.uniqueId(ID_PREFIX),
          isEditing: false,
          isFocused: true,
          isSelected: false
        };
      }
      return pD;
    });
    return state.set('pillsData', newPillsData);
  },

  [ACTION_TYPES.RETRIEVE_HASH_FOR_QUERY_PARAMS]: (state, action) => {
    return handle(state, action, {
      // For now, not dealing with error state for this as
      // this is all URL stuff. Failure logged in creator.
      failure: (s) => s,
      success: (s) => {
        // Yank the ids out of the hash config, we have
        // no use for the rest of the data in there
        const hashIds = action.payload.data.map((hashConfig) => hashConfig.id);
        return s.set('pillDataHashes', hashIds);
      }
    });
  },

  [ACTION_TYPES.SET_RECENT_QUERIES]: (state, action) => {
    return handle(state, action, {
      start: (s) => {
        return s.set('recentQueriesCallInProgress', true);
      },
      success: (s) => {
        const filterText = action.meta.query;
        const recentQueries = action.payload.data.map((queryObject) => {
          return {
            query: queryObject.query,
            displayName: queryObject.displayName
          };
        });
        if (!isEmpty(filterText.trim())) {
          return s.merge({
            recentQueriesFilteredList: recentQueries,
            recentQueriesFilterText: filterText,
            recentQueriesCallInProgress: false
          });
        } else {
          return s.merge({
            recentQueriesUnfilteredList: recentQueries,
            recentQueriesCallInProgress: false
          });
        }
      }
    });
  },

  [ACTION_TYPES.INSERT_PARENS]: (state, { payload }) => {
    let pd;
    const { position } = payload;
    const [open, close] = createParens();
    // assign ids to the new parens
    open.id = _.uniqueId(ID_PREFIX);
    close.id = _.uniqueId(ID_PREFIX);
    // match-up inserted parens
    // open.twinId = close.id;
    // close.twinId = open.id;

    if (state.pillsData.length === 0) {
      // no other pills, insert open and close parens into pillsData
      pd = [
        open,
        close
      ];
    } else {
      if (state.pillsData[position] && state.pillsData[position].isEditing) {
        // we are editing a pill that we now want to convert to open/close
        // parens. Insert open and close parens into pillsData, wrapping the
        // pill that was being edited
        pd = [
          ...state.pillsData.slice(0, position),
          open,
          state.pillsData[position],
          close,
          ...state.pillsData.slice(position + 1)
        ];
      } else {
        pd = [
          // insert open and close parens into pillsData at the desired position
          ...state.pillsData.slice(0, position),
          open,
          close,
          ...state.pillsData.slice(position)
        ];
      }
    }
    return state.set('pillsData', Immutable.from(pd));
  }
}, _initialState);
