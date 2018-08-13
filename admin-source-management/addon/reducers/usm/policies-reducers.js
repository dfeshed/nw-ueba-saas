import Immutable from 'seamless-immutable';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import * as ACTION_TYPES from 'admin-source-management/actions/types';
import { load, persist } from './util/local-storage';
import explorerInitialState from 'component-lib/utils/rsa-explorer/explorer-reducer-initial-state';
import explorerReducers from 'component-lib/utils/rsa-explorer/explorer-reducer-fns';

const localStorageKey = 'rsa::nw::usm::policies';


// Load local storage values and incorporate into initial state
const initialState = load(explorerInitialState, localStorageKey);

initialState.sortField = 'name';
delete initialState.defaultDateRangeTypeName;
delete initialState.defaultDateFilterField;
delete initialState.hasCustomDateRestriction;

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
    const { sortField, isSortDescending, itemsFilters, isFilterPanelOpen } = state;
    persist({ sortField, isSortDescending, itemsFilters, isFilterPanelOpen }, localStorageKey);
    return state;
  });
};

export default reduxActions.handleActions({

  [ACTION_TYPES.POLICIES_TOGGLE_FILTER_PANEL]: persistState(explorerReducers.toggleFilterPanel),
  [ACTION_TYPES.POLICIES_UPDATE_FILTERS]: persistState(explorerReducers.updateFilter),
  [ACTION_TYPES.POLICIES_RESET_FILTERS]: persistState(explorerReducers.resetFilters),
  [ACTION_TYPES.POLICIES_TOGGLE_FOCUS]: explorerReducers.toggleFocusItem,
  [ACTION_TYPES.POLICIES_TOGGLE_SELECTED]: explorerReducers.toggleSelectItem,
  [ACTION_TYPES.POLICIES_CLEAR_FOCUS]: explorerReducers.clearFocusItem,
  [ACTION_TYPES.POLICIES_TOGGLE_SELECT_ALL]: explorerReducers.toggleSelectAll,
  [ACTION_TYPES.POLICIES_SORT_BY]: persistState(explorerReducers.sortBy),

  [ACTION_TYPES.FETCH_POLICIES]: (state, action) => (
    handle(state, action, {
      start: (state) => {
        return state.merge({
          items: [],
          itemsStatus: 'wait'
        });
      },
      failure: (state) => {
        return state.set('itemsStatus', 'error');
      },
      success: (state) => {
        return state.merge({
          items: action.payload.data,
          itemsStatus: 'complete'
        });
      }
    })
  )

}, Immutable.from(initialState));

