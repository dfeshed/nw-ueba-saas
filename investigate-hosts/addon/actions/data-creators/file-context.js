import { HostDetails } from '../api';
import * as ACTION_TYPES from '../types';
import { handleError } from '../creator-utils';
import { setFileStatus, getFileStatus } from 'investigate-shared/actions/api/file/file-status';
import { checksumsWithoutRestricted } from 'investigate-shared/utils/file-status-util';
import { resetRiskContext, getRiskScoreContext, getRespondServerStatus } from 'investigate-shared/actions/data-creators/risk-creators';
import { focusedRowChecksum } from 'investigate-hosts/reducers/details/file-context/selectors';

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

const setRowSelection = (belongsTo, id, index) => ({ type: ACTION_TYPES.SET_FILE_CONTEXT_ROW_SELECTION, payload: { id, index }, meta: { belongsTo } });

const onHostFileSelection = (belongsTo, storeName, { id }, index) => {
  return (dispatch, getState) => {
    dispatch(setRowSelection(belongsTo, id, index));
    dispatch(getRespondServerStatus());
    dispatch(resetRiskContext());
    dispatch(getRiskScoreContext(focusedRowChecksum(getState(), storeName), 'FILE', 'HOST'));
  };
};

const toggleRowSelection = (belongsTo, item) => ({ type: ACTION_TYPES.TOGGLE_FILE_CONTEXT_ROW_SELECTION, payload: item, meta: { belongsTo } });

const toggleAllSelection = (belongsTo) => ({ type: ACTION_TYPES.TOGGLE_FILE_CONTEXT_ALL_SELECTION, meta: { belongsTo } });

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

const fetchMachineCount = (checksums, tabName) => ({
  type: ACTION_TYPES.GET_AGENTS_COUNT_SAGA,
  payload: checksums,
  meta: {
    belongsTo: tabName
  }
});

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
  downloadFilesToServer,
  fetchMachineCount,
  setRowSelection
};
