import { HostDetails, Process } from '../api';
import * as ACTION_TYPES from '../types';
import { handleError } from '../creator-utils';
import { setFileStatus, getFileStatus } from 'investigate-shared/actions/api/file/file-status';
import { checksumsWithoutRestricted } from 'investigate-shared/utils/file-status-util';
import { resetRiskContext, getHostFileScoreContext, getRespondServerStatus } from 'investigate-shared/actions/data-creators/risk-creators';
import { focusedRowChecksum } from 'investigate-hosts/reducers/details/file-context/selectors';
import { getProcessData } from 'investigate-hosts/reducers/details/process/selectors';

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
    const serviceId = getState().endpointQuery.serverId;
    const { endpoint: { visuals: { listAllFiles } } } = getState();
    const data = {
      agentId,
      categories
    };
    if (!listAllFiles || belongsTo !== 'FILE') {
      data.scanTime = scanTime;
    }
    dispatch({ type: ACTION_TYPES.RESET_CONTEXT_DATA, meta: { belongsTo } });
    dispatch({
      type: ACTION_TYPES.FETCH_FILE_CONTEXT,
      promise: HostDetails.getFileContextData(serviceId, data),
      meta: {
        belongsTo,
        onFailure: (response) => handleError(ACTION_TYPES.FETCH_FILE_CONTEXT, response)
      }
    });
  };
};

const setRowSelection = (belongsTo, id, index) => ({ type: ACTION_TYPES.SET_FILE_CONTEXT_ROW_SELECTION, payload: { id, index }, meta: { belongsTo } });

const onHostFileSelection = (belongsTo, storeName, { id }, index) => {
  return (dispatch, getState) => {
    const { endpoint: { detailsInput: { agentId } } } = getState();
    dispatch(setRowSelection(belongsTo, id, index));
    if (id) {
      dispatch(getRespondServerStatus());
      dispatch(resetRiskContext());
      dispatch(getHostFileScoreContext(focusedRowChecksum(getState(), storeName), agentId));
      dispatch(fetchHostNames(belongsTo, focusedRowChecksum(getState(), storeName)));
    }
  };
};

const toggleRowSelection = (belongsTo, item) => ({ type: ACTION_TYPES.TOGGLE_FILE_CONTEXT_ROW_SELECTION, payload: item, meta: { belongsTo } });

const toggleAllSelection = (belongsTo, listOfFiles) => ({ type: ACTION_TYPES.TOGGLE_FILE_CONTEXT_ALL_SELECTION, payload: listOfFiles, meta: { belongsTo } });

const deSelectAllSelection = (belongsTo) => ({ type: ACTION_TYPES.DESELECT_FILE_CONTEXT_ALL_SELECTION, meta: { belongsTo } });

const resetSelection = (belongsTo) => ({ type: ACTION_TYPES.FILE_CONTEXT_RESET_SELECTION, meta: { belongsTo } });

const setHostDetailPropertyTab = (belongsTo, tabName) => ({ type: ACTION_TYPES.SET_HOST_DETAIL_PROPERTY_TAB, payload: { tabName }, meta: { belongsTo } });

const setFileContextFileStatus = (belongsTo, checksums, selectedList, data, callbacks = callbacksDefault) => {
  return (dispatch, getState) => {
    const state = getState();
    const { fileStatus: { restrictedFileList } } = state;
    if (data.fileStatus === 'Whitelist') {
      checksums = checksumsWithoutRestricted(selectedList, restrictedFileList);
    }
    // Selecting top 100 checksums only for file status change.
    if (checksums && checksums.length > 100) {
      checksums = checksums.slice(0, 100);
    }
    dispatch({
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
  };
};

const getFileContextFileStatus = (belongsTo, selections) => ({
  type: ACTION_TYPES.GET_FILE_CONTEXT_FILE_STATUS,
  promise: getFileStatus(selections),
  meta: {
    belongsTo
  }
});

const setFileContextSort = (belongsTo, config) => ({ type: ACTION_TYPES.SET_FILE_CONTEXT_COLUMN_SORT, payload: config, meta: { belongsTo } });

const retrieveRemediationStatus = (belongsTo, selections) => {
  const thumbprints = selections.mapBy('signature.thumbprint').compact();
  return {
    type: ACTION_TYPES.FETCH_REMEDIATION_STATUS,
    promise: HostDetails.fetchRemediation({ thumbprints }),
    meta: { belongsTo }
  };
};

const _getListOfFilesToDownload = (selectedFiles, agentId) => {
  const files = selectedFiles.map(({ checksumSha256, path, fileName }) => ({
    hash: checksumSha256,
    fileName,
    path
  }));

  return {
    agentId,
    files
  };
};

const _getListOfProcessToDownload = (selectedFile, agentId, process) => {
  return {
    agentId,
    processId: process.pid,
    eprocess: process.eprocess,
    processCreateUtcTime: process.createUtcTime,
    hash: selectedFile.checksumSha256,
    fileName: selectedFile.fileName,
    path: selectedFile.path
  };
};

const downloadProcessDump = (agentId, selectedFiles, callbacks) => {
  return async(dispatch, getState) => {
    const [selectedFile] = selectedFiles;
    const { agentId, scanTime } = getState().endpoint.detailsInput;
    dispatch({
      type: ACTION_TYPES.GET_PROCESS,
      promise: Process.getProcess({ agentId, scanTime, pid: selectedFile.pid }),
      meta: {
        onSuccess: () => {
          const processDetails = getProcessData(getState());
          HostDetails.sendProcessDumpRequest(_getListOfProcessToDownload(selectedFile, agentId, processDetails.process))
            .then(() => {
              callbacks.onSuccess();
            }).catch(({ meta: message }) => {
              if (message) {
                callbacks.onFailure(message.message);
              }
            });
        },
        onFailure: (response) => handleError(ACTION_TYPES.GET_PROCESS, response)
      }
    });

  };
};

const downloadFilesToServer = (agentId, selectedFiles, serverId, callbacks) => {
  HostDetails.sendFileDownloadToServerRequest(_getListOfFilesToDownload(selectedFiles, agentId), serverId)
    .then(() => {
      callbacks.onSuccess();
    }).catch(({ meta: message }) => {
      if (message) {
        callbacks.onFailure(message.message);
      }
    });
};

const fetchMachineCount = (checksums, tabName) => ({
  type: ACTION_TYPES.GET_AGENTS_COUNT_SAGA,
  payload: checksums,
  meta: {
    belongsTo: tabName
  }
});

const fetchHostNames = (tabName, checksum) => {
  return (dispatch, getState) => {
    const serviceId = getState().endpointQuery.serverId;
    dispatch({
      type: ACTION_TYPES.SET_HOST_NAME_LIST,
      promise: HostDetails.getHostCount(serviceId, checksum),
      meta: {
        belongsTo: tabName
      }
    });
  };
};

const toggleAllFiles = (belongsTo) => {
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.TOGGLE_ALL_FILE });
    dispatch(getFileContext(belongsTo));
  };
};

export {
  getFileContext,
  onHostFileSelection,
  setHostDetailPropertyTab,
  toggleRowSelection,
  toggleAllSelection,
  setFileContextFileStatus,
  getFileContextFileStatus,
  setFileContextSort,
  retrieveRemediationStatus,
  resetSelection,
  downloadProcessDump,
  downloadFilesToServer,
  fetchMachineCount,
  setRowSelection,
  deSelectAllSelection,
  fetchHostNames,
  toggleAllFiles
};
