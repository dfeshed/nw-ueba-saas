import * as ACTION_TYPES from 'respond/actions/types';
import { CANNED_FILTER_TYPES_BY_NAME } from 'respond/utils/canned-filter-types';
import { SORT_TYPES_BY_NAME } from 'respond/utils/sort-types';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';

const initialState = {
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

  // the sort field and direction
  incidentsSort: SORT_TYPES_BY_NAME.SCORE.name,

  // map of filters applied to the list of incidents
  incidentsFilters: {
    cannedFilter: CANNED_FILTER_TYPES_BY_NAME.ALL.name
  }
};

const incidents = reduxActions.handleActions({

  [ACTION_TYPES.FETCH_INCIDENTS]: (state, action) => {
    return handle(state, action, {
      start: (s) => ({ ...s, incidentsStatus: 'wait' }),
      failure: (s) => ({ ...s, incidents: [], incidentsStatus: 'error' }),
      success: (s) => ({ ...s, incidents: action.payload.data, incidentsStatus: 'completed' })
    });
  },

  [ACTION_TYPES.TOGGLE_SELECT_MODE]: (state) => ({
    ...state,
    incidentsSelected: [],
    isInSelectMode: !state.isInSelectMode
  }),

  [ACTION_TYPES.TOGGLE_FILTER_PANEL]: (state) => ({
    ...state,
    isFilterPanelOpen: !state.isFilterPanelOpen
  }),

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

  [ACTION_TYPES.SORT_BY]: (state, { payload }) => ({
    ...state,
    incidentsSort: payload
  }),

  [ACTION_TYPES.UPDATE_INCIDENT_FILTERS]: (state, { payload }) => ({
    ...state,
    incidentsFilters: {
      ...state.incidentsFilters,
      ...payload
    }
  }),

  [ACTION_TYPES.UPDATE_SELECTED_CANNED_FILTER]: (state, { payload }) => {
    return {
      ...state,
      incidentsFilters: {
        ...state.incidentsFilters,
        cannedFilter: payload
      }
    };
  }
}, initialState);

export default incidents;