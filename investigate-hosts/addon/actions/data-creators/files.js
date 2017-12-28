import * as ACTION_TYPES from '../types';
import { HostFiles } from '../api';
import { toggleDetailsLoadingIndicator } from 'investigate-hosts/actions/ui-state-creators';
import { handleError } from '../creator-utils';

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
        onSuccess: () => {
          dispatch(toggleDetailsLoadingIndicator());
        },
        onFailure: (response) => handleError(ACTION_TYPES.GET_HOST_FILES, response)
      }
    });
  };
};

const setSelectedFile = (fileHash) => ({ type: ACTION_TYPES.SET_SELECTED_FILE, payload: fileHash });


export {
  sortBy,
  getHostFiles,
  setSelectedFile
};
