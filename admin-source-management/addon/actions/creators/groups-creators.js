import * as ACTION_TYPES from 'admin-source-management/actions/types';
import groupsAPI from 'admin-source-management/actions/api/groups-api';

// const callbacksDefault = { onSuccess() {}, onFailure() {} };


const initializeGroups = () => {
  return (dispatch) => {
    dispatch(getItems());
  };
};

const getItems = () => {
  return (dispatch, getState) => {
    const { itemsFilters, sortField, isSortDescending } = getState().usm.groups;

    // Fetch all of the group items that meet the current filter criteria
    dispatch({
      type: ACTION_TYPES.FETCH_GROUPS,
      promise: groupsAPI.fetchGroups(itemsFilters, { sortField, isSortDescending })
    });
  };
};


// /**
//  * Action creator that dispatches a set of actions for fetching incidents (with or without filters) and sorted by one field.
//  * @method getItems
//  * @public
//  * @returns {function(*, *)}
//  */
// const getItems = () => {
//   return (dispatch, getState) => {
//     const { itemsFilters, sortField, isSortDescending, stopItemsStream } = getState().respond.incidents;

//     // Fetch the total incident count for the current query
//     dispatch({
//       type: ACTION_TYPES.FETCH_INCIDENTS_TOTAL_COUNT,
//       promise: Incidents.getIncidentsCount(itemsFilters, { sortField, isSortDescending })
//     });

//     dispatch({ type: ACTION_TYPES.FETCH_INCIDENTS_STARTED });
//     // If we already have an incidents stream running, stop it. This prevents a previously started stream
//     // from continuing to deliver results at the same time as the new stream.
//     if (stopItemsStream) {
//       stopItemsStream();
//     }

//     Incidents.getIncidents(
//       itemsFilters,
//       { sortField, isSortDescending },
//       {
//         onInit: (stopStreamFn) => {
//           dispatch({ type: ACTION_TYPES.FETCH_INCIDENTS_STREAM_INITIALIZED, payload: stopStreamFn });
//         },
//         onCompleted: () => dispatch({ type: ACTION_TYPES.FETCH_INCIDENTS_COMPLETED }),
//         onResponse: (payload) => dispatch({ type: ACTION_TYPES.FETCH_INCIDENTS_RETRIEVE_BATCH, payload }),
//         onError: () => {
//           dispatch({ type: ACTION_TYPES.FETCH_INCIDENTS_ERROR });
//         }
//       }
//     );
//   };
// };

// const deleteItem = (entityId, callbacks = callbacksDefault) => {
//   return (dispatch) => {
//     const reloadItems = entityId.length >= 500; // deletions of more than 500 items should trigger subsequent refresh/reload

//     dispatch({
//       type: ACTION_TYPES.GROUPS_DELETE_GROUP,
//       promise: Incidents.delete(entityId),
//       meta: {
//         onSuccess: (response) => {
//           callbacks.onSuccess(response);
//           if (reloadItems) {
//             dispatch(getItems());
//           }
//         },
//         onFailure: (response) => {
//           callbacks.onFailure(response);
//         }
//       }
//     });
//   };
// };

const resetFilters = () => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.GROUPS_RESET_FILTERS
    });

    dispatch(getItems());
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

    dispatch(getItems());
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

    dispatch(getItems());
  };
};

const toggleFilterPanel = () => ({ type: ACTION_TYPES.GROUPS_TOGGLE_FILTER_PANEL });
const toggleItemSelected = (item) => ({ type: ACTION_TYPES.GROUPS_TOGGLE_SELECTED, payload: item });
const toggleFocusItem = (item) => ({ type: ACTION_TYPES.GROUPS_TOGGLE_FOCUS, payload: item });
const clearFocusItem = () => ({ type: ACTION_TYPES.GROUPS_CLEAR_FOCUS });
const toggleSelectAll = () => ({ type: ACTION_TYPES.GROUPS_TOGGLE_SELECT_ALL });


export {
  initializeGroups,
  getItems,
  // deleteItem,
  resetFilters,
  updateFilter,
  sortBy,
  toggleFilterPanel,
  toggleItemSelected,
  toggleFocusItem,
  clearFocusItem,
  toggleSelectAll
  };


