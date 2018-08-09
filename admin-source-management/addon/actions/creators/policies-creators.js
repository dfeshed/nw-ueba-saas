import * as ACTION_TYPES from 'admin-source-management/actions/types';
import policyAPI from 'admin-source-management/actions/api/policy-api';

const getPolicies = () => ({
  type: ACTION_TYPES.FETCH_POLICY_LIST,
  promise: policyAPI.fetchPolicy()
});

const resetFilters = () => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.POLICIES_RESET_FILTERS
    });

    dispatch(getPolicies());
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

    dispatch(getPolicies());
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

    dispatch(getPolicies());
  };
};

const toggleFilterPanel = () => ({ type: ACTION_TYPES.POLICIES_TOGGLE_FILTER_PANEL });
const toggleItemSelected = (item) => ({ type: ACTION_TYPES.POLICIES_TOGGLE_SELECTED, payload: item });
const toggleFocusItem = (item) => ({ type: ACTION_TYPES.POLICIES_TOGGLE_FOCUS, payload: item });
const clearFocusItem = () => ({ type: ACTION_TYPES.POLICIES_CLEAR_FOCUS });
const toggleSelectAll = () => ({ type: ACTION_TYPES.POLICIES_TOGGLE_SELECT_ALL });

export {
  getPolicies,
  resetFilters,
  updateFilter,
  sortBy,
  toggleFilterPanel,
  toggleItemSelected,
  toggleFocusItem,
  clearFocusItem,
  toggleSelectAll
};