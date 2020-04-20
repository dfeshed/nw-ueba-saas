import * as ACTION_TYPES from '../types';
import { setFileStatus, getFileStatus } from 'investigate-shared/actions/api/file/file-status';
import { checksumsWithoutRestricted } from 'investigate-shared/utils/file-status-util';

const callbacksDefault = { onSuccess() {}, onFailure() {} };

const getSavedFileStatus = (selections) => ({
  type: ACTION_TYPES.GET_FILE_STATUS,
  promise: getFileStatus(selections)
});

const setSelectedFile = ({ id }) => ({ type: ACTION_TYPES.SET_SELECTED_FILE, payload: { id } });

const toggleFileSelection = (selectedFile) => ({ type: ACTION_TYPES.TOGGLE_SELECTED_FILE, payload: selectedFile });

const selectAllFiles = () => ({ type: ACTION_TYPES.SELECT_ALL_FILES });

const deSelectAllFiles = () => ({ type: ACTION_TYPES.DESELECT_ALL_FILES });

const saveFileStatus = (checksums, data, callbacks = callbacksDefault) => {
  return (dispatch, getState) => {
    const state = getState();
    const { fileStatus: { restrictedFileList }, files: { fileList: { selectedFileList } } } = state;
    if (data.fileStatus === 'Whitelist') {
      checksums = checksumsWithoutRestricted(selectedFileList, restrictedFileList);
    }

    dispatch({
      type: ACTION_TYPES.SAVE_FILE_STATUS,
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
  };
};

export {
  setSelectedFile,
  toggleFileSelection,
  selectAllFiles,
  deSelectAllFiles,
  saveFileStatus,
  getSavedFileStatus
};
