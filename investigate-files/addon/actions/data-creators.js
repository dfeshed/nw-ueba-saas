/**
 * @file Investigate Files Data Action Creators
 * Action creators for data retrieval,
 * or for actions that have data side effects
 *
 * Building actions according to FSA spec:
 * https://github.com/acdlite/flux-standard-action
 *
 * @public
 */

import Ember from 'ember';

import * as ACTION_TYPES from './types';
import {
  Schema,
  File
} from './fetch';

const { Logger } = Ember;

/**
 * Action creator for fetching an files schema.
 * @method fetchSchemaInfo
 * @public
 * @returns {Object}
 */
const fetchSchemaInfo = () => {
  return {
    type: ACTION_TYPES.SCHEMA_RETRIEVE,
    promise: Schema.fetchSchema(),
    meta: {
      onSuccess: (response) => Logger.debug(ACTION_TYPES.SCHEMA_RETRIEVE, response)
    }
  };
};

/**
 * Action creator that dispatches a set of actions for fetching files (with or without filters) and sorted by one field.
 * @method _fetchFiles
 * @private
 * @returns {function(*, *)}
 */
const _fetchFiles = () => {
  return (dispatch, getState) => {
    const { systemFilter, sortField, isSortDescending, pageNumber } = getState().files.fileList;
    const { expressionList } = getState().files.filter;
    dispatch({
      type: ACTION_TYPES.FETCH_NEXT_FILES,
      promise: File.fetchFiles(pageNumber, { sortField, isSortDescending }, systemFilter || expressionList),
      meta: {
        onSuccess: (response) => Logger.debug(ACTION_TYPES.FETCH_NEXT_FILES, response)
      }
    });
  };
};

/**
 * An action creator for dispatches a set of actions for updating file filter criteria and re-running fetch of the
 * files using that new criteria
 * @public
 * @method updateFilter
 * @param filters list of expressions
 * @returns {function(*)}
 */
const updateFilter = (expression) => {
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.UPDATE_FILE_FILTER, payload: expression });
    dispatch(_getFirstPageOfFiles());
  };
};

/**
 * An action creator for getting the saved filter information
 * @returns {function(*)}
 * @public
 */
const getFilter = () => ({ type: ACTION_TYPES.GET_FILTER, payload: {} }); // Sending null as save filter functionality not implemented;

/**
 * Action creator for adding the expression to the expression list.
 * @param expression
 * @returns {function(*)}
 * @public
 */
const addFilter = (expression) => ({ type: ACTION_TYPES.ADD_FILE_FILTER, payload: expression });

/**
 * An action creator for removing the expression from the list of expression and also dispatch the action to re-fresh the file list
 * @param name
 * @returns {function(*)}
 * @public
 */
const removeFilter = (name) => {
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.REMOVE_FILE_FILTER, payload: name });
    dispatch(_getFirstPageOfFiles());
  };
};

/**
 * An action creator for resting the file filters.
 * @public
 * @returns {function(*)}
 */
const resetFilters = () => {
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.RESET_FILE_FILTERS });
    dispatch(_getFirstPageOfFiles());
  };
};

/**
 * Action Creator to sort the files.
 * @return {function} redux-thunk
 * @public
 */
const sortBy = (sortField, isSortDescending) => {
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.SET_SORT_BY, payload: { sortField, isSortDescending } });
    dispatch(_getFirstPageOfFiles());
  };
};

/**
 * Action Creator to retrieve the paged files. Increments the current page number and updates the state.
 * @return {function} redux-thunk
 * @public
 */
const getPageOfFiles = () => {
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.INCREMENT_PAGE_NUMBER });
    dispatch(_fetchFiles());
  };
};

/**
 * Action creator for adding the system filters (mac, linux and windows)
 * @param expression
 * @returns {function(*)}
 * @public
 */
const addSystemFilter = (expression) => {
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.ADD_SYSTEM_FILTER, payload: expression });
    dispatch(_getFirstPageOfFiles());
  };
};

/**
 * Action Creator for fetching the first page of data. Before sending the request resets the state
 * @returns {function(*)}
 * @private
 */
const _getFirstPageOfFiles = () => {
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.RESET_FILES });
    dispatch(getPageOfFiles());
  };
};

/**
 * Action creator for exporting files.
 * @returns {function(*)}
 * @public
 */
const exportFileAsCSV = () => {
  return (dispatch, getState) => {
    const { files } = getState();
    const { sortField, isSortDescending } = files.fileList;
    const { schema } = files.schema;
    const { expressionList } = files.filter;
    const columns = schema.filter((field) => field.defaultProjection)
      .map((field) => field.name);
    dispatch({
      type: ACTION_TYPES.DOWNLOAD_FILE_AS_CSV,
      promise: File.fileExport({ sortField, isSortDescending }, expressionList, columns),
      meta: {
        onSuccess: (response) => Logger.debug(ACTION_TYPES.DOWNLOAD_FILE_AS_CSV, response)
      }
    });
  };
};

/**
 * Action creator for setting the currently active filter.
 * @param filter
 * @returns {function(*)}
 * @public
 */
const setActiveFilter = (filter) => ({ type: ACTION_TYPES.SET_ACTIVE_FILTER, payload: filter });

/**
 * Action creator for updating the visible columns in schema
 * @param column
 * @returns {function(*)}
 * @public
 */
const updateColumnVisibility = (column) => ({ type: ACTION_TYPES.UPDATE_COLUMN_VISIBILITY, payload: column });

/**
 * Action creator for for resetting the download link.
 * @public
 */
const resetDownloadId = () => ({ type: ACTION_TYPES.RESET_DOWNLOAD_ID });

export {
  addSystemFilter,
  removeFilter,
  addFilter,
  getFilter,
  updateFilter,
  resetFilters,
  getPageOfFiles,
  sortBy,
  fetchSchemaInfo,
  setActiveFilter,
  exportFileAsCSV,
  updateColumnVisibility,
  resetDownloadId
};
