import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'respond/actions/types';
import reduxActions from 'redux-actions';
import { load, persist } from './util/local-storage';
import explorerInitialState from 'component-lib/utils/rsa-explorer/explorer-reducer-initial-state';
import explorerReducers from 'component-lib/utils/rsa-explorer/explorer-reducer-fns';
import { handle } from 'redux-pack';

const localStorageKey = 'rsa::nw::respond::incidents';

// Load local storage values and incorporate into initial state
const initialState = load(explorerInitialState, localStorageKey);

// default (initial) state for sendToArcher feature is false/off
initialState.isSendToArcherAvailable = false;
initialState.altRowSelection = true;

// If there are no filters, add the baseline date range filter
if (!initialState.itemsFilters) {
  initialState.itemsFilters = explorerReducers.itemsFilters(initialState);
}

// Mechanism to persist some of the state to local storage
// This function will curry a given reducer (function), enabling it to persist its resulting state to a given
// local storage key.
// Note: this implementation may be replaced either with (a) user preference service calls, or (b) with a more
// sophisticated solution with local storage
// @param {function} callback A reducer that will update a given state before persisting it to local storage.
// @returns {Function} The curried reducer.
const persistState = (callback) => {
  return (function() {
    const state = callback(...arguments);
    const { sortField, isSortDescending, itemsFilters, isFilterPanelOpen, hasCustomDateRestriction } = state;
    persist({ sortField, isSortDescending, itemsFilters, isFilterPanelOpen, hasCustomDateRestriction }, localStorageKey);
    return state;
  });
};

const incidentsReducers = reduxActions.handleActions({

  [ACTION_TYPES.FETCH_INCIDENTS_TOTAL_COUNT]: explorerReducers.fetchItemCount,
  [ACTION_TYPES.FETCH_INCIDENTS_STARTED]: explorerReducers.fetchItemsStreamStarted,
  [ACTION_TYPES.FETCH_INCIDENTS_STREAM_INITIALIZED]: explorerReducers.fetchItemsStreamInitialized,
  [ACTION_TYPES.FETCH_INCIDENTS_RETRIEVE_BATCH]: explorerReducers.fetchItemsStreamBatchRetrieved,
  [ACTION_TYPES.FETCH_INCIDENTS_COMPLETED]: explorerReducers.fetchItemsStreamCompleted,
  [ACTION_TYPES.FETCH_INCIDENTS_ERROR]: explorerReducers.fetchItemsStreamError,
  [ACTION_TYPES.UPDATE_INCIDENT]: explorerReducers.updateItem,
  [ACTION_TYPES.ESCALATE_INCIDENT]: explorerReducers.updateItem,
  [ACTION_TYPES.DELETE_INCIDENT]: explorerReducers.deleteItem,
  [ACTION_TYPES.UPDATE_INCIDENT_FILTERS]: persistState(explorerReducers.updateFilter),
  [ACTION_TYPES.TOGGLE_FILTER_PANEL]: persistState(explorerReducers.toggleFilterPanel),
  [ACTION_TYPES.TOGGLE_CUSTOM_DATE_RESTRICTION]: persistState(explorerReducers.toggleCustomDateRestriction),
  [ACTION_TYPES.RESET_INCIDENT_FILTERS]: persistState(explorerReducers.resetFilters),
  [ACTION_TYPES.TOGGLE_FOCUS_INCIDENT]: explorerReducers.toggleFocusItem,
  [ACTION_TYPES.CLEAR_FOCUS_INCIDENTS]: explorerReducers.clearFocusItem,
  [ACTION_TYPES.TOGGLE_INCIDENT_SELECTED]: explorerReducers.toggleSelectItem,
  [ACTION_TYPES.TOGGLE_SELECT_ALL_INCIDENTS]: explorerReducers.toggleSelectAll,
  [ACTION_TYPES.SORT_BY]: persistState(explorerReducers.sortBy),
  [ACTION_TYPES.FETCH_INCIDENTS_SETTINGS]: (state, action) => (
    handle(state, action, {
      success: (s) => {
        const { payload: { data: { isArcherDataSourceConfigured } } } = action;
        return s.set('isSendToArcherAvailable', isArcherDataSourceConfigured);
      },
      failure: (s) => {
        return s.set('isSendToArcherAvailable', false);
      }
    })
  ),
  [ACTION_TYPES.SEND_INCIDENT_TO_ARCHER]: (state, action) => {
    return handle(state, action, {
      success: (s) => {
        const { payload: { data: updatedIncident } } = action;
        const items = s.items.map((item) => item.id === updatedIncident.id ? updatedIncident : item);
        const focusedItem = items.findBy('id', updatedIncident.id);
        const state = s.merge({
          items,
          focusedItem
        });
        return state;
      }
    });
  }

}, Immutable.from(initialState));

export default incidentsReducers;