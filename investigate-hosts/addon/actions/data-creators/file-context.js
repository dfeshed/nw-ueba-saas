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
const getFileContext = (belongsTo, categories) => {
  return (dispatch, getState) => {
    // Get selected agentId and scan time from the state
    const { endpoint: { detailsInput: { agentId, scanTime } } } = getState();
    const data = {
      agentId,
      scanTime,
      categories
    };
    dispatch({ type: ACTION_TYPES.RESET_CONTEXT_DATA, meta: { belongsTo } });
    dispatch({
      type: ACTION_TYPES.FETCH_FILE_CONTEXT,
      promise: HostDetails.getFileContextData(data),
      meta: {
        belongsTo,
        onFailure: (response) => handleError(ACTION_TYPES.FETCH_FILE_CONTEXT, response)
      }
    });
  };
};

const setRowSelection = (belongsTo, { id }) => ({ type: ACTION_TYPES.SET_FILE_CONTEXT_ROW_SELECTION, payload: { id }, meta: { belongsTo } });

const toggleRowSelection = (belongsTo, item) => ({ type: ACTION_TYPES.TOGGLE_FILE_CONTEXT_ROW_SELECTION, payload: item, meta: { belongsTo } });

const toggleAllSelection = (belongsTo) => ({ type: ACTION_TYPES.TOGGLE_FILE_CONTEXT_ALL_SELECTION, meta: { belongsTo } });

const resetSelection = (belongsTo) => ({ type: ACTION_TYPES.FILE_CONTEXT_RESET_SELECTION, meta: { belongsTo } });

const setFileContextFileStatus = (belongsTo, checksums, data, callbacks = callbacksDefault) => ({
  type: ACTION_TYPES.SAVE_FILE_CONTEXT_FILE_STATUS,
  promise: setFileStatus({ ...data, checksums }),
  meta: {
    belongsTo,
    onSuccess: (response) => {
      callbacks.onSuccess(response);
    },
    onFailure: (response) => {
      callbacks.onFailure(response);
    }
  }
});

const getFileContextFileStatus = (belongsTo, selections) => ({
  type: ACTION_TYPES.GET_FILE_CONTEXT_FILE_STATUS,
  promise: getFileStatus(selections),
  meta: {
    belongsTo
  }
});

const setFileContextSort = (belongsTo, config) => ({ type: ACTION_TYPES.SET_FILE_CONTEXT_COLUMN_SORT, payload: config, meta: { belongsTo } });


const getPaginatedFileContext = () => {
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.INCREMENT_PAGE_NUMBER, meta: { belongsTo: 'FILE' } });
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
        belongsTo: 'FILE',
        onFailure: (response) => handleError(ACTION_TYPES.FETCH_FILE_CONTEXT_PAGINATED, response)
      }
    });
  };
};

const retrieveRemediationStatus = (belongsTo, selections) => {
  const thumbprints = selections.mapBy('signature.thumbprint').compact();
  if (thumbprints && thumbprints.length) {
    return {
      type: ACTION_TYPES.FETCH_REMEDIATION_STATUS,
      promise: HostDetails.fetchRemediation({ thumbprints }),
      meta: { belongsTo }
    };
  }
};

const _getListOfFilesToDownload = (slectedFiles, agentId) => {
  const files = slectedFiles.map(({ checksumSha256, path, fileName }) => ({
    hash: checksumSha256,
    fileName,
    path
  }));

  return {
    agentId,
    files
  };
};

const downloadFilesToServer = (agentId, selectedFiles, callbacks) => {
  HostDetails.sendFileDownloadToServerRequest(_getListOfFilesToDownload(selectedFiles, agentId))
    .then(() => {
      callbacks.onSuccess();
    }).catch(({ meta: message }) => {
      callbacks.onFailure(message.message);
    });
};

export {
  getFileContext,
  setRowSelection,
  toggleRowSelection,
  toggleAllSelection,
  setFileContextFileStatus,
  getFileContextFileStatus,
  setFileContextSort,
  getPaginatedFileContext,
  retrieveRemediationStatus,
  resetSelection,
  downloadFilesToServer
};
