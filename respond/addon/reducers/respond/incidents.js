import * as ACTION_TYPES from 'respond/actions/types';
import { CANNED_FILTER_TYPES_BY_NAME } from 'respond/utils/canned-filter-types';
import { SORT_TYPES_BY_NAME } from 'respond/utils/sort-types';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import { load, persist } from './util/local-storage';

const localStorageKey = 'rsa::nw::respond::incidents';

let initialState = {
  // the known list of incidents
  incidents: [],

  // either 'wait', 'error' or 'completed'
  incidentsStatus: null,

  // subset of `incidents` selected by the user
  incidentsSelected: [],

  // true when user toggles on "Select" mode for enabling multiple selections
  isInSelectMode: false,

  // true when user toggles "More Filters" to reveal filter panel
  isFilerPanelOpen: false,

  // true if the user is using the alternate theme (light instead of dark). Temporary property.
  isAltThemeActive: false,

  // the sort field and direction
  // LocalStorage is a temporary mechanism for remembering user's sort selection. May use user preference service or a more
  // sophisticated mechanism for handling all incidents criteria as a final solution
  incidentsSort: SORT_TYPES_BY_NAME.SCORE_DESC.name,

  // total number of matching known incidents
  incidentsTotal: null,

  // map of filters applied to the list of incidents
  incidentsFilters: {
    cannedFilter: CANNED_FILTER_TYPES_BY_NAME.ALL.name
  }
};

// Load local storage values and incorporate into initial state
initialState = load(initialState, localStorageKey);

// Mechanism to persist some of the state to local storage
// This function will curry a given reducer (function), enabling it to persist its resulting state to a given
// local storage key.
// Note: this implementation may be replaced either with (a) user preference service calls, or (b) with a more
// sophisticated solution with local storage
// @param {function} callback A reducer that will update a given state before persisting it to local storage.
// @returns {Function} The curried reducer.
const persistIncidentsState = (callback) => {
  return (function() {
    const state = callback(...arguments);
    const { incidentsSort, incidentsFilters, isFilterPanelOpen, isAltThemeActive } = state;
    persist({ incidentsSort, incidentsFilters, isFilterPanelOpen, isAltThemeActive }, localStorageKey);
    return state;
  });
};

const incidents = reduxActions.handleActions({

  [ACTION_TYPES.FETCH_INCIDENTS]: (state, action) => {
    return handle(state, action, {
      start: (s) => ({ ...s, incidentsStatus: 'wait', incidentsTotal: null }),
      failure: (s) => ({ ...s, incidents: [], incidentsStatus: 'error' }),
      success: (s) => ({
        ...s,
        incidents: action.payload.data,
        incidentsTotal: action.payload.meta.total,
        incidentsStatus: 'completed'
      })
    });
  },

  [ACTION_TYPES.TOGGLE_SELECT_MODE]: (state) => ({
    ...state,
    incidentsSelected: [],
    isInSelectMode: !state.isInSelectMode
  }),

  [ACTION_TYPES.TOGGLE_FILTER_PANEL]: persistIncidentsState((state) => ({
    ...state,
    isFilterPanelOpen: !state.isFilterPanelOpen
  })),

  [ACTION_TYPES.TOGGLE_THEME]: persistIncidentsState((state) => ({
    ...state,
    isAltThemeActive: !state.isAltThemeActive
  })),

  [ACTION_TYPES.TOGGLE_INCIDENT_SELECTED]: (state, { payload: incident }) => {
    if (!incident) {
      return;
    }
    const { incidentsSelected } = state;
    const index = incidentsSelected.indexOf(incident);

    if (index > -1) {
      incidentsSelected.removeAt(index);
    } else {
      incidentsSelected.pushObject(incident);
    }

    return {
      ...state,
      incidentsSelected: [...incidentsSelected]
    };
  },

  [ACTION_TYPES.CLEAR_SELECTED_INCIDENTS]: (state) => ({
    ...state,
    incidentsSelected: []
  }),

  [ACTION_TYPES.SORT_BY]: persistIncidentsState((state, { payload }) => ({
    ...state,
    incidentsSort: payload
  })),

  [ACTION_TYPES.UPDATE_INCIDENT_FILTERS]: persistIncidentsState((state, { payload }) => ({
    ...state,
    incidentsFilters: {
      ...state.incidentsFilters,
      ...payload
    }
  })),

  [ACTION_TYPES.UPDATE_SELECTED_CANNED_FILTER]: persistIncidentsState((state, { payload }) => ({
    ...state,
    incidentsFilters: {
      ...state.incidentsFilters,
      cannedFilter: payload
    }
  }))
}, initialState);

export default incidents;