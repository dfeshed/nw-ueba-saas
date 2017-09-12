import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'respond/actions/types';
import reduxActions from 'redux-actions';
import { load, persist } from './util/local-storage';
import explorerInitialState from './util/explorer-reducer-initial-state';
import explorerReducers from './util/explorer-reducer-fns';

const localStorageKey = 'rsa::nw::respond::incidents';

// Load local storage values and incorporate into initial state
const initialState = load(explorerInitialState, localStorageKey);

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
  [ACTION_TYPES.DELETE_INCIDENT]: explorerReducers.deleteItem,
  [ACTION_TYPES.UPDATE_INCIDENT_FILTERS]: persistState(explorerReducers.updateFilter),
  [ACTION_TYPES.TOGGLE_FILTER_PANEL]: persistState(explorerReducers.toggleFilterPanel),
  [ACTION_TYPES.TOGGLE_CUSTOM_DATE_RESTRICTION]: persistState(explorerReducers.toggleCustomDateRestriction),
  [ACTION_TYPES.RESET_INCIDENT_FILTERS]: persistState(explorerReducers.resetFilters),
  [ACTION_TYPES.TOGGLE_FOCUS_INCIDENT]: explorerReducers.toggleFocusItem,
  [ACTION_TYPES.CLEAR_FOCUS_INCIDENTS]: explorerReducers.clearFocusItem,
  [ACTION_TYPES.TOGGLE_INCIDENT_SELECTED]: explorerReducers.toggleSelectItem,
  [ACTION_TYPES.TOGGLE_SELECT_ALL_INCIDENTS]: explorerReducers.toggleSelectAll,
  [ACTION_TYPES.SORT_BY]: persistState(explorerReducers.sortBy)

}, Immutable.from(initialState));

export default incidentsReducers;