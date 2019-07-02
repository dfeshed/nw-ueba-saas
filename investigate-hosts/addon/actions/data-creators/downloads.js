/**
 * @hostdownloads Investigate Hosts Data Action Creators
 * Action creators for data retrieval on file downloads,
 * or for actions that have data side effects
 *
 * Building actions according to FSA spec:
 * https://github.com/acdlite/flux-standard-action
 *
 * @public
 */

import { debug } from '@ember/debug';
import * as ACTION_TYPES from '../types';
import * as SHARED_ACTION_TYPES from 'investigate-shared/actions/types';
import { Machines, HostDetails } from '../api';
import { next } from '@ember/runloop';
import { getFilter } from 'investigate-shared/actions/data-creators/filter-creators';

const callbacksDefault = { onSuccess() {}, onFailure() {} };

/**
 * Action creator that dispatches a set of actions for fetching files (with or without filters) and sorted by one field.
 * @method _fetchFiles
 * @private
 * @returns {function(*, *)}
 */
const _fetchFiles = (type) => {
  return (dispatch, getState) => {
    const state = getState();
    const { sortField, isSortDescending, pageNumber } = state.endpoint.hostDownloads.downloads;
    const { agentId } = state.endpoint.detailsInput;
    const { expressionList } = state.endpoint.hostDownloads.filter;
    // filtering downloads based on agent
    const agentFilter = {
      restrictionType: 'EQUAL',
      propertyValues: [
        {
          value: agentId
        }
      ],
      propertyName: 'agentId'
    };

    const updatedExpressionList = [...expressionList, agentFilter];

    dispatch({
      type,
      promise: Machines.getPageOfDownloadsApi(pageNumber, sortField, isSortDescending, updatedExpressionList),
      meta: {
        onSuccess: (response) => {
          const debugResponse = JSON.stringify(response);
          debug(`onSuccess: ${ACTION_TYPES.FETCH_NEXT_DOWNLOADED_FILES} ${debugResponse}`);
        }
      }
    });
  };
};

/**
 * Action Creator to retrieve the paged files. Increments the current page number and updates the state.
 * @return {function} redux-thunk
 * @public
 */
const getPageOfDownloads = () => {
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.INCREMENT_DOWNLOADED_FILES_PAGE_NUMBER });
    dispatch(_fetchFiles(ACTION_TYPES.FETCH_NEXT_DOWNLOADED_FILES));
  };
};

/**
 * Action Creator for fetching the first page of data. Before sending the request resets the state
 * @returns {function(*)}
 * @private
 */
const getFirstPageOfDownloads = () => {
  return (dispatch) => {
    dispatch(getFilter(() => {}, 'DOWNLOAD'));
    dispatch({ type: ACTION_TYPES.RESET_DOWNLOADED_FILES });
    dispatch({ type: ACTION_TYPES.INCREMENT_DOWNLOADED_FILES_PAGE_NUMBER });
    next(() => {
      dispatch(_fetchFiles(ACTION_TYPES.FETCH_ALL_DOWNLOADED_FILES));
    });
  };
};

/**
 * Action Creator to sort the files.
 * @return {function} redux-thunk
 * @public
 */
const sortBy = (sortField, isSortDescending) => {
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.SET_DOWNLOADED_FILES_SORT_BY, payload: { sortField, isSortDescending } });
    dispatch(getFirstPageOfDownloads());
  };
};

const toggleFileSelection = (selectedFile) => ({ type: ACTION_TYPES.TOGGLE_SELECTED_DOWNLOADED_FILE, payload: selectedFile });

const selectAllFiles = () => ({ type: ACTION_TYPES.SELECT_ALL_DOWNLOADED_FILES });

const deSelectAllFiles = () => ({ type: ACTION_TYPES.DESELECT_ALL_DOWNLOADED_FILES });

const onFileSelection = (item) => ({ type: ACTION_TYPES.SET_SELECTED_FILE, payload: item });

const setSelectedIndex = (index) => ({ type: ACTION_TYPES.SET_SELECTED_DOWNLOADED_FILE_INDEX, payload: index });

const _processSelectedDownloadedFiles = (selectedFiles) => {

  return selectedFiles.reduce((selectedFilesAndServerIds, item) => {
    const { serviceId } = item;
    if (!selectedFilesAndServerIds[serviceId]) {
      selectedFilesAndServerIds[serviceId] = [];
    }
    selectedFilesAndServerIds[serviceId].push(item.id);
    return selectedFilesAndServerIds;
  }, {});

};

/* This is to account for multiple EPSs.
The next ws call needs to be made only after the previous one has resolved and is successful */
const deleteSelectedFiles = (selectedFiles, callback = callbacksDefault) => {
  const selectedFilesAndServerIds = _processSelectedDownloadedFiles(selectedFiles);

  const serverIds = Object.keys(selectedFilesAndServerIds);
  return (dispatch) => {
    (async(dispatch) => {
      for (let i = 0; i < serverIds.length; i++) {
        const serverId = serverIds[i];
        try {
          await HostDetails.deleteSelectedFiles(serverId, selectedFilesAndServerIds[serverId]);
        } catch (e) {
          callback.onFailure(e);
          return;
        }
      }
      callback.onSuccess();
      dispatch(getFirstPageOfDownloads());
    })(dispatch);
  };
};

const saveLocalMFTCopy = (selectedFile, callback, serverId) => {
  const { id, fileName } = selectedFile;
  return (dispatch) => {
    HostDetails.saveLocalMFTCopy(serverId, id).then(({ data }) => {
      if (data.id) {
        const url = serverId ? `/rsa/endpoint/${serverId}/memory/download?id=${data.id}&filename=${fileName}.zip` : '';
        dispatch({ type: SHARED_ACTION_TYPES.SET_DOWNLOAD_FILE_LINK, payload: url });
      }
    }).catch((response) => {
      if (response && response.meta) {
        const { meta: { message } } = response;
        callback.onFailure(message);
      }
    });
  };
};
const toggleMftView = (selectedFile) => ({ type: ACTION_TYPES.TOGGLE_MFT_VIEW, payload: selectedFile });

export {
  getPageOfDownloads,
  sortBy,
  toggleFileSelection,
  selectAllFiles,
  deSelectAllFiles,
  getFirstPageOfDownloads,
  onFileSelection,
  setSelectedIndex,
  deleteSelectedFiles,
  saveLocalMFTCopy,
  toggleMftView
};
