import * as ACTION_TYPES from '../types';
import { HostDetails } from '../api';
import { handleError } from '../creator-utils';
import { getAllProcess } from './process';
import { getFileContext } from './file-context';
import { debug } from '@ember/debug';
import { resetRiskContext, getRiskScoreContext, getRespondServerStatus } from 'investigate-shared/actions/data-creators/risk-creators';
import { getFirstPageOfDownloads } from './downloads';

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

const fetchDataForSelectedTab = () => {
  return (dispatch, getState) => {
    const { endpoint: { drivers, autoruns, libraries, visuals, detailsInput: { agentId } } } = getState();
    const { activeHostDetailTab, activeAutorunTab, activeAnomaliesTab } = visuals;
    switch (activeHostDetailTab) {
      case 'OVERVIEW':
        dispatch(getRespondServerStatus());
        dispatch(resetRiskContext());
        dispatch(getRiskScoreContext(agentId, 'HOST'));
        break;
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
        dispatch(getAllProcess());
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
      case 'DOWNLOADS':
        dispatch(getFirstPageOfDownloads());
        break;
    }
  };
};

const setHostPropertyTabView = (tabName) => ({ type: ACTION_TYPES.CHANGE_PROPERTY_TAB, payload: { tabName } });

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

const setPropertyPanelTabView = (tabName) => ({ type: ACTION_TYPES.CHANGE_PROPERTY_PANEL_TAB, payload: { tabName } });

const setHostDetailPropertyTab = (tabName) => ({ type: ACTION_TYPES.SET_HOST_DETAIL_PROPERTY_TAB, payload: { tabName } });

export {
  setScanTime,
  setHostPropertyTabView,
  setTransition,
  exportFileContext,
  setHostDetailsDataTableSortConfig,
  setPropertyPanelTabView,
  setHostDetailPropertyTab,
  fetchDataForSelectedTab
};
