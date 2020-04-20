import * as ACTION_TYPES from 'admin-source-management/actions/types';
import sourceAPI from 'admin-source-management/actions/api/source-api';
import { handleError } from './utils-creators';
import {
  selectedDeleteItems,
  selectedPublishItems
} from 'admin-source-management/reducers/usm/sources-selectors';

const callbacksDefault = { onSuccess() {}, onFailure() {} };

const initializeSources = () => {
  return (dispatch) => {
    dispatch(fetchSources());
    dispatch(fetchEndpointServers());
    dispatch(fetchLogServers());
  };
};

/**
 * Action creator that dispatches a set of actions for fetching sources (with or without filters) and sorted by one field.
 * @method fetchSources
 * @private
 * @returns {function(*, *)}
 */
const fetchSources = (callbacks = callbacksDefault) => {
  return (dispatch, getState) => {
    const pageNumber = 0;
    const { /* itemsFilters, */ sortField, isSortDescending } = getState().usm.sources;
    // const { systemFilter, sortField, isSortDescending, pageNumber } = getState().usm.sourcesFilter;
    const { expressionList } = getState().usm.sourcesFilter;
    dispatch({
      type: ACTION_TYPES.FETCH_SOURCES,
      promise: sourceAPI.fetchSources(pageNumber, { sortField, isSortDescending }, expressionList),
      meta: {
        onSuccess: (response) => {
          callbacks.onSuccess(response);
        },
        onFailure: (response) => {
          handleError(ACTION_TYPES.FETCH_SOURCES, response);
          callbacks.onFailure(response);
        }
      }
    });
  };
};

const deleteSources = (callbacks = callbacksDefault) => {
  return (dispatch, getState) => {
    const items = selectedDeleteItems(getState());
    dispatch({
      type: ACTION_TYPES.DELETE_SOURCES,
      promise: sourceAPI.deleteSources(items),
      meta: {
        onSuccess: (response) => {
          callbacks.onSuccess(response);
          dispatch(fetchSources());
        },
        onFailure: (response) => {
          handleError(ACTION_TYPES.DELETE_SOURCES, response);
          callbacks.onFailure(response);
        }
      }
    });
  };
};

const publishSources = (callbacks = callbacksDefault) => {
  return (dispatch, getState) => {
    const items = selectedPublishItems(getState());
    dispatch({
      type: ACTION_TYPES.PUBLISH_SOURCES,
      promise: sourceAPI.publishSources(items),
      meta: {
        onSuccess: (response) => {
          callbacks.onSuccess(response);
          dispatch(fetchSources());
        },
        onFailure: (response) => {
          handleError(ACTION_TYPES.PUBLISH_SOURCES, response);
          callbacks.onFailure(response);
        }
      }
    });
  };
};

const resetFilters = () => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.SOURCES_RESET_FILTERS
    });

    dispatch(fetchSources());
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
      type: ACTION_TYPES.SOURCES_UPDATE_FILTERS,
      payload: filters
    });

    dispatch(fetchSources());
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
      type: ACTION_TYPES.SOURCES_SORT_BY,
      payload: {
        sortField,
        isSortDescending
      }
    });

    dispatch(fetchSources());
  };
};

// ===================================================
// edrSource specific action creators
// ===================================================
const fetchEndpointServers = () => {
  return {
    type: ACTION_TYPES.FETCH_ENDPOINT_SERVERS,
    promise: sourceAPI.fetchEndpointServers()
  };
};

// ===================================================
// windowsLogSource specific action creators
// ===================================================
const fetchLogServers = () => {
  return {
    type: ACTION_TYPES.FETCH_LOG_SERVERS,
    promise: sourceAPI.fetchLogServers()
  };
};


const toggleFilterPanel = () => ({ type: ACTION_TYPES.SOURCES_TOGGLE_FILTER_PANEL });
const toggleItemSelected = (item) => ({ type: ACTION_TYPES.SOURCES_TOGGLE_SELECTED, payload: item });
const toggleFocusItem = (item) => ({ type: ACTION_TYPES.SOURCES_TOGGLE_FOCUS, payload: item });
const clearFocusItem = () => ({ type: ACTION_TYPES.SOURCES_CLEAR_FOCUS });
const toggleSelectAll = () => ({ type: ACTION_TYPES.SOURCES_TOGGLE_SELECT_ALL });

export {
  initializeSources,
  fetchSources,
  deleteSources,
  publishSources,
  resetFilters,
  updateFilter,
  sortBy,
  toggleFilterPanel,
  toggleItemSelected,
  toggleFocusItem,
  clearFocusItem,
  toggleSelectAll,
  // edrSource specific action creators
  fetchEndpointServers,
  // windowsLogSource specific action creators
  fetchLogServers
};