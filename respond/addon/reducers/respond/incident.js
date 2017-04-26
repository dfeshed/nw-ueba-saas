import * as ACTION_TYPES from 'respond/actions/types';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import { load, persist } from './util/local-storage';
import { toggle } from 'respond/utils/immut/array';

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

  // width of the incident details inspector UI, in pixels
  inspectorWidth: 400,

  // true when user toggles "Journal" btn to reveal journal
  isJournalPanelOpen: false,

  // currently selected data in the storyline
  selection: {
    type: '', // either 'storyPoint', 'event', 'node' or 'link'; possibly empty
    ids: [] // array of ids; possibly empty
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
const persistIncidentState = (callback) => {
  return (function() {
    const state = callback(...arguments);
    const { viewMode, isJournalPanelOpen, inspectorWidth } = state;
    persist({ viewMode, isJournalPanelOpen, inspectorWidth }, localStorageKey);
    return state;
  });
};

// Updates the state value with the value updated on the server.
// If the updated incidents includes the incident currently in the incident details route,
// we must update that incident's info as well.
const _handleUpdates = (action) => {
  return (state) => {
    const { payload: { request: { updates, entityIds } } } = action;
    const { id, info } = state;
    const updatedIncidentInfo = entityIds.includes(id) ? { ...info, ...updates } : info;
    return {
      ...state,
      info: updatedIncidentInfo
    };
  };
};

const incident = reduxActions.handleActions({

  [ACTION_TYPES.INITIALIZE_INCIDENT]: (state, { payload }) => ({
    // reset state for a new incident id, even if it matches the old incident id,
    // because we don't want to reuse info, we want to reload it in case it may have changed on server
    ...initialState,
    id: payload,
    inspectorWidth: state.inspectorWidth || initialState.inspectorWidth
  }),

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

  [ACTION_TYPES.RESIZE_INCIDENT_INSPECTOR]: persistIncidentState((state, { payload }) => ({
    ...state,
    inspectorWidth: payload
  })),

  [ACTION_TYPES.TOGGLE_JOURNAL_PANEL]: persistIncidentState((state) => ({
    ...state,
    isJournalPanelOpen: !state.isJournalPanelOpen
  })),

  [ACTION_TYPES.SET_INCIDENT_SELECTION]: (state, { payload: { type, id } }) => {
    const { selection: { type: wasType, ids: wasIds } } = state;
    let newSelection;
    if (wasType !== type) {
      // type has changed, so reset selection to given inputs
      newSelection = { type, ids: id ? [ id ] : [] };
    } else {
      // type hasn't changed
      // was the given id already the only selection? if so toggle it, otherwise reset to it
      const wasAlreadyOnlySelection = wasIds && (wasIds.length === 1) && (wasIds[0] === id);
      newSelection = { type, ids: wasAlreadyOnlySelection ? [] : [ id ] };
    }
    return {
      ...state,
      selection: newSelection
    };
  },

  [ACTION_TYPES.TOGGLE_INCIDENT_SELECTION]: (state, { payload: { type, id } }) => {
    const { selection: { type: wasType, ids: wasIds } } = state;
    let newSelection;
    if (wasType !== type) {
      // type has changed, so reset selection to given inputs
      newSelection = { type, ids: id ? [ id ] : [] };
    } else {
      // type hasn't changed
      newSelection = { type, ids: toggle(wasIds, id) };
    }
    return {
      ...state,
      selection: newSelection
    };
  },

  [ACTION_TYPES.UPDATE_INCIDENT]: (state, action) => {
    return handle(state, action, {
      success: _handleUpdates(action)
    });
  }
}, initialState);

export default incident;