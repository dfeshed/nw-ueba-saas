import * as ACTION_TYPES from '../types';
import { HostDetails } from '../api';
import { handleError } from '../creator-utils';
import { getAllProcess, toggleProcessView } from './process';
import {
  getFileContextAutoruns,
  getFileContextServices,
  getFileContextTasks
} from './autoruns';
import { getFileContextDrivers } from './drivers';
import { getProcessAndLib } from './libraries';
import { getHostFiles } from './files';
import { toggleExploreSearchResults } from 'investigate-hosts/actions/ui-state-creators';

import Ember from 'ember';
const { Logger } = Ember;

const _getAllSnapShots = (agentId) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.FETCH_ALL_SNAP_SHOTS,
      promise: HostDetails.getAllSnapShots({ agentId }),
      meta: {
        onSuccess: (response) => {
          dispatch({ type: ACTION_TYPES.SET_SCAN_TIME, payload: response.data[0] });
          dispatch(_getHostDetails(true));
        },
        onFailure: (response) => Logger.error(ACTION_TYPES.FETCH_ALL_SNAP_SHOTS, response)
      }
    });
  };
};

/**
 * Action creator to fetch job id and download the file from server
 * @method exportFileContext
 * @public
 * @returns {Object}
 */
const exportFileContext = (data) => (
  {
    type: ACTION_TYPES.FETCH_DOWNLOAD_FILECONTEXT_JOB_ID,
    promise: HostDetails.exportFileContext(data),
    meta: {
      onSuccess: (response) => Logger.debug(ACTION_TYPES.FETCH_DOWNLOAD_FILECONTEXT_JOB_ID, response),
      onFailure: (response) => handleError(ACTION_TYPES.FETCH_DOWNLOAD_FILECONTEXT_JOB_ID, response)
    }
  }
);

const setScanTime = (time) => ({ type: ACTION_TYPES.SET_SCAN_TIME, payload: time });
const setTransition = (type) => ({ type: ACTION_TYPES.SET_ANIMATION, payload: type });

const _getHostDetails = (forceRefresh) => {
  return (dispatch, getState) => {
    const { agentId, scanTime } = getState().endpoint.detailsInput;
    if (forceRefresh) {
      dispatch({
        type: ACTION_TYPES.FETCH_HOST_DETAILS,
        promise: HostDetails.getHostDetails({ agentId, scanTime }),
        meta: {
          onSuccess: (response) => {
            dispatch({ type: ACTION_TYPES.RESET_HOST_DETAILS });
            dispatch(_fetchDataForSelectedTab());
            Logger.debug(ACTION_TYPES.FETCH_HOST_DETAILS, response);
          },
          onFailure: (response) => handleError(ACTION_TYPES.FETCH_HOST_DETAILS, response)
        }
      });
    } else {
      dispatch(_fetchDataForSelectedTab());
    }
  };
};

const _fetchDataForSelectedTab = () => {
  return (dispatch, getState) => {
    const { endpoint: { drivers, autoruns, libraries, hostFiles, process, visuals } } = getState();
    const { activeHostDetailTab, activeAutorunTab } = visuals;
    switch (activeHostDetailTab) {
      case 'PROCESS':
        if (!process.processList) {
          dispatch(getAllProcess());
        }
        break;
      case 'AUTORUNS':
        if (activeAutorunTab === 'AUTORUNS' && !autoruns.autorun) {
          dispatch(getFileContextAutoruns());
        } else if (activeAutorunTab === 'SERVICES' && !autoruns.service) {
          dispatch(getFileContextServices());
        } else if (activeAutorunTab === 'TASKS' && !autoruns.task) {
          dispatch(getFileContextTasks());
        }
        break;
      case 'FILES':
        if (!hostFiles.files.length) {
          dispatch(getHostFiles());
        }
        break;
      case 'DRIVERS':
        if (!drivers.driver) {
          dispatch(getFileContextDrivers());
        }
        break;
      case 'LIBRARIES':
        if (!libraries.library) {
          dispatch(getProcessAndLib());
        }
        break;

    }
  };
};
/**
 * An Action Creator for changing a detail view.
 *
 * Dispatches action to update visual indicators, then will
 * either fetch the data for the detail view or prepare the data
 * already in state.
 *
 * @param {object}
 * @returns {function} redux-thunk
 * @public
 */
const setNewTabView = (tabName) => {
  return (dispatch) => {
    dispatch(changeDetailTab(tabName));
    dispatch(_getHostDetails());
  };
};

/**
 * An Action Creator for changing the autoruns view.
 *
 * @param {object}
 * @returns {function} redux-thunk
 * @public
 */
const setAutorunsTabView = (tabName) => {
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.CHANGE_AUTORUNS_TAB, payload: { tabName } });
    dispatch(_fetchDataForSelectedTab());
  };
};

const changeDetailTab = (tabName) => {
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.CHANGE_DETAIL_TAB, payload: { tabName } });
  };
};

const loadDetailsWithExploreInput = (scanTime, tabName, secondaryTab) => {
  return (dispatch, getState) => {
    const { isTreeView } = getState().endpoint.visuals;
    dispatch(setScanTime(scanTime));
    dispatch(changeDetailTab(tabName));
    if (tabName === 'PROCESS' && !isTreeView) {
      dispatch(toggleProcessView());
    }
    if (secondaryTab) {
      dispatch(setAutorunsTabView(secondaryTab));
    } else {
      dispatch(_getHostDetails(true));
    }
    dispatch(toggleExploreSearchResults(false));
  };
};

const initializeAgentDetails = (input, loadSnapshot) => {
  return (dispatch, getState) => {
    const { agentId, scanTime } = input;
    const { endpoint: { detailsInput: dataState } } = getState();
    // If selected host/agentId is same as previously loaded then don't load the data as it already in the state
    if (dataState.agentId !== agentId || (scanTime && scanTime !== dataState.scanTime)) {
      dispatch({ type: ACTION_TYPES.INITIALIZE_DATA, payload: input });
      if (loadSnapshot) {
        dispatch(_getAllSnapShots(agentId));
      } else {
        dispatch(_getHostDetails(true));
      }
    }
  };
};

const setHostDetailsDataTableSortConfig = (sortConfig) => {
  return (dispatch, getState) => {
    const { endpoint: { visuals: { activeAutorunTab, activeHostDetailTab } } } = getState();
    dispatch({
      type: ACTION_TYPES.HOST_DETAILS_DATATABLE_SORT_CONFIG,
      payload: {
        ...sortConfig,
        tabName: activeHostDetailTab !== 'AUTORUNS' ? activeHostDetailTab : activeAutorunTab
      }
    });
  };
};

export {
  initializeAgentDetails,
  changeDetailTab,
  setScanTime,
  setNewTabView,
  setTransition,
  exportFileContext,
  loadDetailsWithExploreInput,
  setAutorunsTabView,
  setHostDetailsDataTableSortConfig
};
