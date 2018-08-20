import * as ACTION_TYPES from 'admin-source-management/actions/types';
import groupsAPI from 'admin-source-management/actions/api/groups-api';
import { handleError } from './utils-creators';

const callbacksDefault = { onSuccess() {}, onFailure() {} };

const initializeGroups = () => {
  return (dispatch) => {
    dispatch(getGroups());
  };
};

const getGroups = () => {
  return (dispatch, getState) => {
    const { itemsFilters, sortField, isSortDescending } = getState().usm.groups;

    // Fetch all of the group items that meet the current filter criteria
    dispatch({
      type: ACTION_TYPES.FETCH_GROUPS,
      promise: groupsAPI.fetchGroups(itemsFilters, { sortField, isSortDescending })
    });
  };
};

const deleteGroups = (selectedItems, callbacks = callbacksDefault) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.DELETE_GROUPS,
      promise: groupsAPI.deleteGroups(selectedItems),
      meta: {
        onSuccess: (response) => {
          callbacks.onSuccess(response);
          dispatch(getGroups());
        },
        onFailure: (response) => {
          handleError(ACTION_TYPES.DELETE_GROUPS, response);
          callbacks.onFailure(response);
        }
      }
    });
  };
};

const publishGroups = (selectedItems, callbacks = callbacksDefault) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.DELETE_GROUPS,
      promise: groupsAPI.deleteGroups(selectedItems),
      meta: {
        onSuccess: (response) => {
          callbacks.onSuccess(response);
          dispatch(getGroups());
        },
        onFailure: (response) => {
          handleError(ACTION_TYPES.DELETE_GROUPS, response);
          callbacks.onFailure(response);
        }
      }
    });
  };
};

const resetFilters = () => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.GROUPS_RESET_FILTERS
    });

    dispatch(getGroups());
  };
};

/**
 * An action creator for dispatches a set of actions for updating incidents filter criteria and re-running fetch of the
 * incidents using that new criteria
 * @public
 * @method updateFilter
 * @param filters An object representing the filters to be applied
 * @returns {function(*)}
 */
const updateFilter = (filters) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.GROUPS_UPDATE_FILTERS,
      payload: filters
    });

    dispatch(getGroups());
  };
};

/**
 * An action creator for updating the sort-by information used in fetching incidents
 * @public
 * @method sortBy Object { id: [field name (string) to sort by], isDescending: [boolean] }
 * @param sortField
 * @param isSortDescending
 * @returns {function(*)}
 */
const sortBy = (sortField, isSortDescending) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.GROUPS_SORT_BY,
      payload: {
        sortField,
        isSortDescending
      }
    });

    dispatch(getGroups());
  };
};

const toggleFilterPanel = () => ({ type: ACTION_TYPES.GROUPS_TOGGLE_FILTER_PANEL });
const toggleItemSelected = (item) => ({ type: ACTION_TYPES.GROUPS_TOGGLE_SELECTED, payload: item });
const toggleFocusItem = (item) => ({ type: ACTION_TYPES.GROUPS_TOGGLE_FOCUS, payload: item });
const clearFocusItem = () => ({ type: ACTION_TYPES.GROUPS_CLEAR_FOCUS });
const toggleSelectAll = () => ({ type: ACTION_TYPES.GROUPS_TOGGLE_SELECT_ALL });


export {
  initializeGroups,
  getGroups,
  deleteGroups,
  publishGroups,
  resetFilters,
  updateFilter,
  sortBy,
  toggleFilterPanel,
  toggleItemSelected,
  toggleFocusItem,
  clearFocusItem,
  toggleSelectAll
  };


