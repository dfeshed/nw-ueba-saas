import * as ACTION_TYPES from 'admin-source-management/actions/types';
import groupsAPI from 'admin-source-management/actions/api/groups-api';
import { handleError } from './utils-creators';
import {
  selectedDeleteItems,
  selectedPublishItems
} from 'admin-source-management/reducers/usm/groups-selectors';

const callbacksDefault = { onSuccess() {}, onFailure() {} };

const initializeGroups = () => {
  return (dispatch) => {
    dispatch(fetchGroups());
  };
};

/**
 * Action creator that dispatches a set of actions for fetching groups (with or without filters) and sorted by one field.
 * @method fetchGroups
 * @private
 * @returns {function(*, *)}
 */
const fetchGroups = (callbacks = callbacksDefault) => {
  return (dispatch, getState) => {
    const pageNumber = 0;
    const expressionList = [];
    const { /* itemsFilters, */ sortField, isSortDescending } = getState().usm.groups;
    // const { systemFilter, sortField, isSortDescending, pageNumber } = getState().files.fileList;
    // const { expressionList } = getState().files.filter;
    dispatch({
      type: ACTION_TYPES.FETCH_GROUPS,
      promise: groupsAPI.fetchGroups(pageNumber, { sortField, isSortDescending }, expressionList),
      meta: {
        onSuccess: (response) => {
          callbacks.onSuccess(response);
        },
        onFailure: (response) => {
          handleError(ACTION_TYPES.FETCH_GROUPS, response);
          callbacks.onFailure(response);
        }
      }
    });
  };
};

const deleteGroups = (callbacks = callbacksDefault) => {
  return (dispatch, getState) => {
    const items = selectedDeleteItems(getState());
    dispatch({
      type: ACTION_TYPES.DELETE_GROUPS,
      promise: groupsAPI.deleteGroups(items),
      meta: {
        onSuccess: (response) => {
          callbacks.onSuccess(response);
          dispatch(fetchGroups());
        },
        onFailure: (response) => {
          handleError(ACTION_TYPES.DELETE_GROUPS, response);
          callbacks.onFailure(response);
        }
      }
    });
  };
};

const publishGroups = (callbacks = callbacksDefault) => {
  return (dispatch, getState) => {
    const items = selectedPublishItems(getState());
    dispatch({
      type: ACTION_TYPES.PUBLISH_GROUPS,
      promise: groupsAPI.publishGroups(items),
      meta: {
        onSuccess: (response) => {
          callbacks.onSuccess(response);
          dispatch(fetchGroups());
        },
        onFailure: (response) => {
          handleError(ACTION_TYPES.PUBLISH_GROUPS, response);
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

    dispatch(fetchGroups());
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

    dispatch(fetchGroups());
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

    dispatch(fetchGroups());
  };
};

const toggleFilterPanel = () => ({ type: ACTION_TYPES.GROUPS_TOGGLE_FILTER_PANEL });
const toggleItemSelected = (item) => ({ type: ACTION_TYPES.GROUPS_TOGGLE_SELECTED, payload: item });
const toggleFocusItem = (item) => ({ type: ACTION_TYPES.GROUPS_TOGGLE_FOCUS, payload: item });
const clearFocusItem = () => ({ type: ACTION_TYPES.GROUPS_CLEAR_FOCUS });
const toggleSelectAll = () => ({ type: ACTION_TYPES.GROUPS_TOGGLE_SELECT_ALL });


export {
  initializeGroups,
  fetchGroups,
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


