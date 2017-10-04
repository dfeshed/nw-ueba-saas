import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'respond/actions/types';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import { load, persist } from '../util/local-storage';
import explorerInitialState from '../util/explorer-reducer-initial-state';
import explorerReducers from '../util/explorer-reducer-fns';

const localStorageKey = 'rsa::nw::respond::alerts';
// Load local storage values and incorporate into initial state
const initialState = load(explorerInitialState, localStorageKey);

initialState.sortField = 'receivedTime';
initialState.defaultDateFilterField = 'receivedTime';
initialState.defaultDateRangeTypeName = 'LAST_HOUR';

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

const alertsReducers = reduxActions.handleActions({
  [ACTION_TYPES.FETCH_ALERTS_TOTAL_COUNT]: explorerReducers.fetchItemCount,
  [ACTION_TYPES.FETCH_ALERTS_STARTED]: explorerReducers.fetchItemsStreamStarted,
  [ACTION_TYPES.FETCH_ALERTS_STREAM_INITIALIZED]: explorerReducers.fetchItemsStreamInitialized,
  [ACTION_TYPES.FETCH_ALERTS_RETRIEVE_BATCH]: explorerReducers.fetchItemsStreamBatchRetrieved,
  [ACTION_TYPES.FETCH_ALERTS_COMPLETED]: explorerReducers.fetchItemsStreamCompleted,
  [ACTION_TYPES.FETCH_ALERTS_ERROR]: explorerReducers.fetchItemsStreamError,
  [ACTION_TYPES.UPDATE_ALERT]: explorerReducers.updateItem,
  [ACTION_TYPES.DELETE_ALERT]: explorerReducers.deleteItem,
  [ACTION_TYPES.UPDATE_ALERT_FILTERS]: persistState(explorerReducers.updateFilter),
  [ACTION_TYPES.TOGGLE_FILTER_PANEL_ALERTS]: persistState(explorerReducers.toggleFilterPanel),
  [ACTION_TYPES.TOGGLE_ALERTS_CUSTOM_DATE_RESTRICTION]: persistState(explorerReducers.toggleCustomDateRestriction),
  [ACTION_TYPES.RESET_ALERT_FILTERS]: persistState(explorerReducers.resetFilters),
  [ACTION_TYPES.TOGGLE_FOCUS_ALERT]: explorerReducers.toggleFocusItem,
  [ACTION_TYPES.CLEAR_FOCUS_ALERT]: explorerReducers.clearFocusItem,
  [ACTION_TYPES.TOGGLE_ALERT_SELECTED]: explorerReducers.toggleSelectItem,
  [ACTION_TYPES.TOGGLE_SELECT_ALL_ALERTS]: explorerReducers.toggleSelectAll,
  [ACTION_TYPES.ALERT_SORT_BY]: persistState(explorerReducers.sortBy),
  [ACTION_TYPES.CREATE_INCIDENT]: (state, action) => (
    handle(state, action, {
      start: (s) => s.set('isTransactionUnderway', true),
      success: (s) => {
        const { payload: { data: { id }, request: { data: { associated } } } } = action;
        const alertIds = associated.map((association) => (association.id));
        return s.set('items', s.items.map((alert) => { // Update the alerts (items) that now have an associated incident
          if (alertIds.includes(alert.id)) {
            return { ...alert, incidentId: id, partOfIncident: true };
          }
          return alert;
        }));
      },
      failure: (s) => s,
      finish: (s) => s.set('isTransactionUnderway', false)
    })
  ),

  [ACTION_TYPES.ALERTS_ADD_TO_INCIDENT]: (state, action) => (
    handle(state, action, {
      success: (s) => {
        const { payload: { request: { data: { entity: { id }, associated } } } } = action;
        const alertIds = associated.map((association) => (association.id));
        return s.set('items', s.items.map((alert) => { // Update the alerts (items) that now have an associated incident
          if (alertIds.includes(alert.id)) {
            return { ...alert, incidentId: id, partOfIncident: true };
          }
          return alert;
        }));
      }
    })
  )

}, Immutable.from(initialState));

export default alertsReducers;