import * as ACTION_TYPES from '../types';
import { HostFiles } from '../api';
import Ember from 'ember';
const { Logger } = Ember;

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
        onSuccess: (response) => Logger.debug(ACTION_TYPES.GET_HOST_FILES, response),
        onFailure: (response) => _handleFilesError(ACTION_TYPES.GET_HOST_FILES, response)
      }
    });
  };
};

const setSelectedFile = (fileHash) => ({ type: ACTION_TYPES.SET_SELECTED_FILE, payload: fileHash });

const _handleFilesError = (type, response) => {
  Logger.error(type, response);
};

export {
  sortBy,
  getHostFiles,
  setSelectedFile
};
