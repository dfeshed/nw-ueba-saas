import * as ACTION_TYPES from 'admin-source-management/actions/types';
import policyAPI from 'admin-source-management/actions/api/policy-api';
import { handleError } from './utils-creators';
import {
  selectedDeleteItems,
  selectedPublishItems
} from 'admin-source-management/reducers/usm/policies-selectors';

const callbacksDefault = { onSuccess() {}, onFailure() {} };

const initializePolicies = () => {
  return (dispatch) => {
    dispatch(fetchPolicies());
  };
};

const fetchPolicies = () => {
  return (dispatch, getState) => {
    const { itemsFilters, sortField, isSortDescending } = getState().usm.policies;

    // Fetch all of the policies items that meet the current filter criteria
    dispatch({
      type: ACTION_TYPES.FETCH_POLICIES,
      promise: policyAPI.fetchPolicies(itemsFilters, { sortField, isSortDescending })
    });
  };
};

const deletePolicies = (callbacks = callbacksDefault) => {
  return (dispatch, getState) => {
    const items = selectedDeleteItems(getState());
    dispatch({
      type: ACTION_TYPES.DELETE_POLICIES,
      promise: policyAPI.deletePolicies(items),
      meta: {
        onSuccess: (response) => {
          callbacks.onSuccess(response);
          dispatch(fetchPolicies());
        },
        onFailure: (response) => {
          handleError(ACTION_TYPES.DELETE_POLICIES, response);
          callbacks.onFailure(response);
        }
      }
    });
  };
};

const publishPolicies = (callbacks = callbacksDefault) => {
  return (dispatch, getState) => {
    const items = selectedPublishItems(getState());
    dispatch({
      type: ACTION_TYPES.PUBLISH_POLICIES,
      promise: policyAPI.publishPolicies(items),
      meta: {
        onSuccess: (response) => {
          callbacks.onSuccess(response);
          dispatch(fetchPolicies());
        },
        onFailure: (response) => {
          handleError(ACTION_TYPES.PUBLISH_POLICIES, response);
          callbacks.onFailure(response);
        }
      }
    });
  };
};

const resetFilters = () => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.POLICIES_RESET_FILTERS
    });

    dispatch(fetchPolicies());
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
      type: ACTION_TYPES.POLICIES_UPDATE_FILTERS,
      payload: filters
    });

    dispatch(fetchPolicies());
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
      type: ACTION_TYPES.POLICIES_SORT_BY,
      payload: {
        sortField,
        isSortDescending
      }
    });

    dispatch(fetchPolicies());
  };
};

const toggleFilterPanel = () => ({ type: ACTION_TYPES.POLICIES_TOGGLE_FILTER_PANEL });
const toggleItemSelected = (item) => ({ type: ACTION_TYPES.POLICIES_TOGGLE_SELECTED, payload: item });
const toggleFocusItem = (item) => ({ type: ACTION_TYPES.POLICIES_TOGGLE_FOCUS, payload: item });
const clearFocusItem = () => ({ type: ACTION_TYPES.POLICIES_CLEAR_FOCUS });
const toggleSelectAll = () => ({ type: ACTION_TYPES.POLICIES_TOGGLE_SELECT_ALL });

export {
  initializePolicies,
  fetchPolicies,
  deletePolicies,
  publishPolicies,
  resetFilters,
  updateFilter,
  sortBy,
  toggleFilterPanel,
  toggleItemSelected,
  toggleFocusItem,
  clearFocusItem,
  toggleSelectAll
};