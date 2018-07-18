import * as ACTION_TYPES from '../types';
import { HostDetails } from '../api';
import { handleError } from '../creator-utils';
import { getAllProcess, toggleProcessView } from './process';
import {
  getFileContextAutoruns,
  getFileContextServices,
  getFileContextTasks
} from './autoruns';
import { getFileContextHooks } from './anomalies';
import { getFileContextDrivers } from './drivers';
import { getProcessAndLib } from './libraries';
import { getHostFiles } from './files';
import { fetchHostContext } from './host';
import { toggleExploreSearchResults } from 'investigate-hosts/actions/ui-state-creators';
import { debug } from '@ember/debug';

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
        onFailure: (response) => {
          const debugResponse = JSON.stringify(response);
          debug(`onFailure: ${ACTION_TYPES.FETCH_ALL_SNAP_SHOTS} ${debugResponse}`);
        }
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
      onSuccess: (response) => {
        const debugResponse = JSON.stringify(response);
        debug(`onSuccess: ${ACTION_TYPES.FETCH_DOWNLOAD_FILECONTEXT_JOB_ID} ${debugResponse}`);
      },
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
            dispatch(_fetchPolicyDetails(agentId));
            dispatch(fetchHostContext(response.data.machine.machineName));
            const debugResponse = JSON.stringify(response);
            debug(`onSuccess: ${ACTION_TYPES.FETCH_HOST_DETAILS} ${debugResponse}`);
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
    const { endpoint: { drivers, autoruns, libraries, hostFiles, process, visuals, anomalies } } = getState();
    const { activeHostDetailTab, activeAutorunTab, activeAnomaliesTab } = visuals;
    switch (activeHostDetailTab) {
      case 'ANOMALIES':
        if ((activeAnomaliesTab === 'HOOKS') && (!anomalies.hooks)) {
          dispatch(getFileContextHooks());
        }
        break;
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

const _fetchPolicyDetails = (agentId) => (
  {
    type: ACTION_TYPES.FETCH_POLICY_DETAILS,
    promise: HostDetails.policyDetails({ agentId }),
    meta: {
      onSuccess: (response) => {
        const debugResponse = JSON.stringify(response);
        debug(`onSuccess: ${ACTION_TYPES.FETCH_POLICY_DETAILS} ${debugResponse}`);
      },
      onFailure: (response) => handleError(ACTION_TYPES.FETCH_POLICY_DETAILS, response)
    }
  }
);

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

const setHostPropertyTabView = (tabName) => ({ type: ACTION_TYPES.CHANGE_PROPERTY_TAB, payload: { tabName } });
const setDataSourceTab = (tabName) => ({ type: ACTION_TYPES.CHANGE_DATASOURCE_TAB, payload: { tabName } });

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

const setAnomaliesTabView = (tabName) => {
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.CHANGE_ANOMALIES_TAB, payload: { tabName } });
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
      if (tabName === 'AUTORUNS') {
        dispatch(setAutorunsTabView(secondaryTab));
      } else {
        dispatch(setAnomaliesTabView(secondaryTab));
      }
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
    const { endpoint: { visuals: { activeAutorunTab, activeAnomaliesTab, activeHostDetailTab } } } = getState();

    const subtabMapping = { 'AUTORUNS': activeAutorunTab, 'ANOMALIES': activeAnomaliesTab };

    dispatch({
      type: ACTION_TYPES.HOST_DETAILS_DATATABLE_SORT_CONFIG,
      payload: {
        ...sortConfig,
        tabName: subtabMapping[activeHostDetailTab] ? subtabMapping[activeHostDetailTab] : activeHostDetailTab
      }
    });
  };
};

export {
  initializeAgentDetails,
  changeDetailTab,
  setScanTime,
  setNewTabView,
  setHostPropertyTabView,
  setDataSourceTab,
  setTransition,
  exportFileContext,
  loadDetailsWithExploreInput,
  setAutorunsTabView,
  setAnomaliesTabView,
  setHostDetailsDataTableSortConfig
};
