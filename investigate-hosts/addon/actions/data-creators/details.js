import { lookup } from 'ember-dependency-lookup';
import * as ACTION_TYPES from '../types';
import * as SHARED_ACTION_TYPES from 'investigate-shared/actions/types';
import { HostDetails } from '../api';
import { handleError } from '../creator-utils';
import { getAllProcess, toggleProcessView } from './process';
import { getFileContext } from './file-context';
import { fetchHostContext, getAllServices, setFocusedHost } from './host';
import { toggleExploreSearchResults, setSelectedHost } from 'investigate-hosts/actions/ui-state-creators';
import { debug } from '@ember/debug';
import { getServiceId } from 'investigate-shared/actions/data-creators/investigate-creators';
import { setSelectedMachineServerId } from 'investigate-shared/actions/data-creators/endpoint-server-creators';
import { toggleFileAnalysisView } from 'investigate-shared/actions/data-creators/file-analysis-creators';

const _getAllSnapShots = (agentId) => {
  return (dispatch, getState) => {
    const { serverId } = getState().endpointQuery;
    dispatch({
      type: ACTION_TYPES.FETCH_ALL_SNAP_SHOTS,
      promise: HostDetails.getAllSnapShots({ agentId }, serverId),
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
    const { serverId } = getState().endpointQuery;
    if (forceRefresh) {
      dispatch({
        type: ACTION_TYPES.FETCH_HOST_DETAILS,
        promise: HostDetails.getHostDetails({ agentId, scanTime }, serverId),
        meta: {
          onSuccess: (response) => {
            const { data } = response;
            dispatch({ type: ACTION_TYPES.RESET_HOST_DETAILS });
            const request = lookup('service:request');
            request.registerPersistentStreamOptions({ socketUrlPostfix: response.data.serviceId, requiredSocketUrl: 'endpoint/socket' });
            dispatch(setFocusedHost(data));
            dispatch(setSelectedMachineServerId(response.data.serviceId));
            dispatch(_fetchDataForSelectedTab());
            dispatch(_fetchPolicyDetails(agentId));
            dispatch(getAllServices());
            dispatch(getServiceId('MACHINE'));
            dispatch(fetchHostContext(data.machineIdentity.machineName));
            dispatch(setSelectedHost(data));
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
    const { endpoint: { drivers, autoruns, libraries, process, visuals } } = getState();
    const { activeHostDetailTab, activeAutorunTab, activeAnomaliesTab } = visuals;
    switch (activeHostDetailTab) {
      case 'ANOMALIES':
        if ((activeAnomaliesTab === 'IMAGEHOOKS')) {
          dispatch(getFileContext('IMAGEHOOK', ['IMAGE_HOOKS']));
        } else if ((activeAnomaliesTab === 'THREADS')) {
          dispatch(getFileContext('THREAD', ['THREADS']));
        } else if ((activeAnomaliesTab === 'KERNELHOOKS')) {
          dispatch(getFileContext('KERNELHOOK', ['KERNEL_HOOKS']));
        }
        break;
      case 'PROCESS':
        if (!process.processList) {
          dispatch(getAllProcess());
        }
        break;
      case 'AUTORUNS':
        if (activeAutorunTab === 'AUTORUNS' && !autoruns.autorun) {
          dispatch(getFileContext('AUTORUN', ['AUTORUNS']));
        } else if (activeAutorunTab === 'SERVICES' && !autoruns.service) {
          dispatch(getFileContext('SERVICE', ['SERVICES']));
        } else if (activeAutorunTab === 'TASKS' && !autoruns.task) {
          dispatch(getFileContext('TASK', ['TASKS']));
        }
        break;
      case 'FILES':
        dispatch(getFileContext('FILE'));
        break;
      case 'DRIVERS':
        if (!drivers.driver) {
          dispatch(getFileContext('DRIVER', ['DRIVERS']));
        }
        break;
      case 'LIBRARIES':
        if (!libraries.library) {
          dispatch(getFileContext('LIBRARY', ['LOADED_LIBRARIES']));
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
    dispatch(toggleFileAnalysisView(false));
    dispatch({ type: ACTION_TYPES.SET_DOWNLOAD_FILE_LINK, payload: null });
    dispatch({ type: ACTION_TYPES.CHANGE_DETAIL_TAB, payload: { tabName } });
    dispatch({ type: ACTION_TYPES.CLOSE_PROCESS_DETAILS });
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
    const { endpoint: { detailsInput: dataState, filter } } = getState();

    //  To fix the filter reload issue we need to set the applied filter as a saved filter
    if (!filter.selectedFilter || filter.selectedFilter.id === -1) {
      const savedFilter = { id: 1, criteria: { expressionList: filter.expressionList } };
      dispatch({ type: SHARED_ACTION_TYPES.SET_SAVED_FILTER, payload: savedFilter, meta: { belongsTo: 'MACHINE' } });
    }

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

const setAlertTab = (tabName) => ({ type: ACTION_TYPES.CHANGE_ALERT_TAB, payload: { tabName } });

const setPropertyPanelTabView = (tabName) => ({ type: ACTION_TYPES.CHANGE_PROPERTY_PANEL_TAB, payload: { tabName } });

const setHostDetailPropertyTab = (tabName) => ({ type: ACTION_TYPES.SET_HOST_DETAIL_PROPERTY_TAB, payload: { tabName } });

const saveLocalFileCopy = (selectedFile, callback) => {
  return (dispatch, getState) => {
    HostDetails.saveLocalFileCopy(selectedFile.checksumSha256)
      .then(({ data }) => {
        if (data.id) {
          const { serverId } = getState().endpointQuery;
          const url = serverId ? `/rsa/endpoint/${serverId}/file/download?id=${data.id}&filename=${selectedFile.fileName}.zip` : '';
          dispatch({ type: ACTION_TYPES.SET_DOWNLOAD_FILE_LINK, payload: url });
        }
      })
      .catch((response) => {
        const { meta: { message } } = response;
        callback.onFailure(message);
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
  setHostDetailsDataTableSortConfig,
  setAlertTab,
  setPropertyPanelTabView,
  setHostDetailPropertyTab,
  saveLocalFileCopy
};
