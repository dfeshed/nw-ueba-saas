import * as ACTION_TYPES from 'respond/actions/types';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import { load, persist } from './util/local-storage';

const localStorageKey = 'rsa::nw::respond::incident';

let initialState = {
  // id of the incident that owns `info` & `storyline`
  id: null,

  // incident details
  info: null,

  // either 'wait', 'error' or 'completed'
  infoStatus: null,

  // incident storyline information
  storyline: null,

  // either 'wait', 'error' or 'completed'
  storylineStatus: null,

  // either 'overview', 'storyline' or 'events'
  viewMode: 'overview',

  // true when user toggles "Journal" btn to reveal journal
  isJournalPanelOpen: false
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
const persistIncidentState = (callback) => {
  return (function() {
    const state = callback(...arguments);
    const { viewMode, isJournalPanelOpen } = state;
    persist({ viewMode, isJournalPanelOpen }, localStorageKey);
    return state;
  });
};

const incident = reduxActions.handleActions({

  [ACTION_TYPES.INITIALIZE_INCIDENT]: (state, { payload }) => {
    // payload is the new incident id
    if (payload === state.id) {

      // incident id is unchanged, so no need to change state
      return state;
    } else {

      // reset state for a new incident id
      return {
        ...initialState,
        id: payload
      };
    }
  },

  [ACTION_TYPES.FETCH_INCIDENT_DETAILS]: (state, action) => {
    return handle(state, action, {
      start: (s) => ({ ...s, info: null, infoStatus: 'wait' }),
      failure: (s) => ({ ...s, infoStatus: 'error' }),
      success: (s) => ({ ...s, info: action.payload.data, infoStatus: 'completed' })
    });
  },

  [ACTION_TYPES.FETCH_INCIDENT_STORYLINE]: (state, action) => {
    return handle(state, action, {
      start: (s) => ({ ...s, storyline: null, storylineStatus: 'wait' }),
      failure: (s) => ({ ...s, storylineStatus: 'error' }),
      success: (s) => ({ ...s, storyline: action.payload.data, storylineStatus: 'completed' })
    });
  },

  [ACTION_TYPES.SET_VIEW_MODE]: persistIncidentState((state, { payload }) => ({
    ...state,
    viewMode: payload
  })),

  [ACTION_TYPES.TOGGLE_JOURNAL_PANEL]: persistIncidentState((state) => ({
    ...state,
    isJournalPanelOpen: !state.isJournalPanelOpen
  }))
}, initialState);

export default incident;