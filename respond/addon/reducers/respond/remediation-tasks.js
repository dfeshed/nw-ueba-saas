import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'respond/actions/types';
import reduxActions from 'redux-actions';
import { load, persist } from './util/local-storage';
import explorerInitialState from './util/explorer-reducer-initial-state';
import explorerReducers from './util/explorer-reducer-fns';

const localStorageKey = 'rsa::nw::respond::remediation-tasks';

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

const remdiationTasks = reduxActions.handleActions({

  [ACTION_TYPES.FETCH_REMEDIATION_TASKS_TOTAL_COUNT]: explorerReducers.fetchItemCount,
  [ACTION_TYPES.FETCH_REMEDIATION_TASKS]: explorerReducers.fetchItems,
  [ACTION_TYPES.UPDATE_REMEDIATION_TASK]: explorerReducers.updateItem,
  [ACTION_TYPES.DELETE_REMEDIATION_TASK]: explorerReducers.deleteItem,
  [ACTION_TYPES.UPDATE_REMEDIATION_TASK_FILTERS]: persistState(explorerReducers.updateFilter),
  [ACTION_TYPES.TOGGLE_FILTER_PANEL_REMEDIATION_TASKS]: persistState(explorerReducers.toggleFilterPanel),
  [ACTION_TYPES.TOGGLE_REMEDIATION_TASKS_CUSTOM_DATE_RESTRICTION]: persistState(explorerReducers.toggleCustomDateRestriction),
  [ACTION_TYPES.RESET_REMEDIATION_TASK_FILTERS]: persistState(explorerReducers.resetFilters),
  [ACTION_TYPES.TOGGLE_FOCUS_REMEDIATION_TASK]: explorerReducers.toggleFocusItem,
  [ACTION_TYPES.CLEAR_FOCUS_REMEDIATION_TASK]: explorerReducers.clearFocusItem,
  [ACTION_TYPES.TOGGLE_REMEDIATION_TASK_SELECTED]: explorerReducers.toggleSelectItem,
  [ACTION_TYPES.TOGGLE_SELECT_ALL_REMEDIATION_TASKS]: explorerReducers.toggleSelectAll,
  [ACTION_TYPES.REMEDIATION_TASK_SORT_BY]: persistState(explorerReducers.sortBy)

}, Immutable.from(initialState));

export default remdiationTasks;