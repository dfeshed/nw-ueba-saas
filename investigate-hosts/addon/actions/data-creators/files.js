import * as ACTION_TYPES from '../types';
import { HostFiles } from '../api';
import { handleError } from '../creator-utils';
import { setFileStatus, getFileStatus } from 'investigate-shared/actions/api/file/file-status';

const callbacksDefault = { onSuccess() {}, onFailure() {} };

const sortBy = (sortOption) => {
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.SET_HOST_FILES_SORT_BY, payload: { sortOption } });
    dispatch({ type: ACTION_TYPES.RESET_HOST_FILES });
    dispatch(getHostFiles());
  };
};

const getHostFiles = () => {
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.INCREMENT_PAGE_NUMBER });
    dispatch(_fetchHostFiles());
  };
};

const getSavedFileStatus = (selections) => ({
  type: ACTION_TYPES.GET_FILE_STATUS,
  promise: getFileStatus(selections)
});

const _fetchHostFiles = () => {
  return (dispatch, getState) => {
    const { agentId, scanTime } = getState().endpoint.detailsInput;
    const { selectedTab } = getState().endpoint.explore;
    let checksumSha256 = null;
    if (selectedTab && selectedTab.tabName === 'FILES') {
      checksumSha256 = selectedTab.checksum;
    }
    const { sortField: key, isDescOrder: descending, pageNumber } = getState().endpoint.hostFiles;
    dispatch({
      type: ACTION_TYPES.GET_HOST_FILES,
      promise: HostFiles.getHostFiles(pageNumber, agentId, scanTime, checksumSha256, key, descending),
      meta: {
        onFailure: (response) => handleError(ACTION_TYPES.GET_HOST_FILES, response)
      }
    });
  };
};

const setSelectedFile = ({ id }) => ({ type: ACTION_TYPES.SET_SELECTED_FILE, payload: { id } });

const toggleFileSelection = (selectedFile) => ({ type: ACTION_TYPES.TOGGLE_SELECTED_FILE, payload: selectedFile });

const selectAllFiles = () => ({ type: ACTION_TYPES.SELECT_ALL_FILES });

const deSelectAllFiles = () => ({ type: ACTION_TYPES.DESELECT_ALL_FILES });

const saveFileStatus = (checksums, data, callbacks = callbacksDefault) => ({
  promise: setFileStatus({ ...data, checksums }),
  meta: {
    onSuccess: (response) => {
      callbacks.onSuccess(response);
    },
    onFailure: (response) => {
      callbacks.onFailure(response);
    }
  }
});

export {
  sortBy,
  getHostFiles,
  setSelectedFile,
  toggleFileSelection,
  selectAllFiles,
  deSelectAllFiles,
  saveFileStatus,
  getSavedFileStatus
};