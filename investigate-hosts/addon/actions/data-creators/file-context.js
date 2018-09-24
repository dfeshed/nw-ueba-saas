import { HostDetails } from '../api';
import * as ACTION_TYPES from '../types';
import { handleError } from '../creator-utils';
import { setFileStatus, getFileStatus } from 'investigate-shared/actions/api/file/file-status';
import HostFiles from '../api/files';

const callbacksDefault = { onSuccess() {}, onFailure() {} };

/**
 * Action creator for fetching all file context
 * @method getFileContext
 * @public
 * @returns {Object}
 */
const getFileContext = (name, categories) => {
  return (dispatch, getState) => {
    // Get selected agentId and scan time from the state
    const { endpoint: { detailsInput: { agentId, scanTime } } } = getState();
    const data = {
      agentId,
      scanTime,
      categories
    };
    dispatch({ type: ACTION_TYPES.RESET_CONTEXT_DATA, meta: { name } });
    dispatch({
      type: ACTION_TYPES.FETCH_FILE_CONTEXT,
      promise: HostDetails.getFileContextData(data),
      meta: {
        name,
        onFailure: (response) => handleError(ACTION_TYPES.FETCH_FILE_CONTEXT, response)
      }
    });
  };
};

const setRowSelection = (name, { id }) => ({ type: ACTION_TYPES.SET_FILE_CONTEXT_ROW_SELECTION, payload: { id }, meta: { name } });

const toggleRowSelection = (name, item) => ({ type: ACTION_TYPES.TOGGLE_FILE_CONTEXT_ROW_SELECTION, payload: item, meta: { name } });

const toggleAllSelection = (name) => ({ type: ACTION_TYPES.TOGGLE_FILE_CONTEXT_ALL_SELECTION, meta: { name } });

const setFileContextFileStatus = (name, checksums, data, callbacks = callbacksDefault) => ({
  type: ACTION_TYPES.SAVE_FILE_CONTEXT_FILE_STATUS,
  promise: setFileStatus({ ...data, checksums }),
  meta: {
    name,
    onSuccess: (response) => {
      callbacks.onSuccess(response);
    },
    onFailure: (response) => {
      callbacks.onFailure(response);
    }
  }
});

const getFileContextFileStatus = (name, selections) => ({
  type: ACTION_TYPES.GET_FILE_CONTEXT_FILE_STATUS,
  promise: getFileStatus(selections),
  meta: {
    name
  }
});

const setFileContextSort = (name, config) => ({ type: ACTION_TYPES.SET_FILE_CONTEXT_COLUMN_SORT, payload: config, meta: { name } });


const getPaginatedFileContext = () => {
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.INCREMENT_PAGE_NUMBER, meta: { name: 'FILE' } });
    dispatch(_fetchHostFiles());
  };
};

const _fetchHostFiles = () => {
  return (dispatch, getState) => {
    const { agentId, scanTime } = getState().endpoint.detailsInput;
    const { selectedTab } = getState().endpoint.explore;
    let checksumSha256 = null;
    if (selectedTab && selectedTab.tabName === 'FILES') {
      checksumSha256 = selectedTab.checksum;
    }
    const { sortField: key = 'fileName', isDescOrder: descending = false, pageNumber } = getState().endpoint.hostFiles;
    dispatch({
      type: ACTION_TYPES.FETCH_FILE_CONTEXT_PAGINATED,
      promise: HostFiles.getHostFiles(pageNumber, agentId, scanTime, checksumSha256, key, descending),
      meta: {
        name: 'FILE',
        onFailure: (response) => handleError(ACTION_TYPES.FETCH_FILE_CONTEXT_PAGINATED, response)
      }
    });
  };
};


export {
  getFileContext,
  setRowSelection,
  toggleRowSelection,
  toggleAllSelection,
  setFileContextFileStatus,
  getFileContextFileStatus,
  setFileContextSort,
  getPaginatedFileContext
};
