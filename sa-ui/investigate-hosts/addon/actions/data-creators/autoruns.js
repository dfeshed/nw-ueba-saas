import { HostDetails } from '../api';
import * as ACTION_TYPES from '../types';
import { handleError } from '../creator-utils';
import { setFileStatus, getFileStatus } from 'investigate-shared/actions/api/file/file-status';


const callbacksDefault = { onSuccess() {}, onFailure() {} };

/**
 * Action creator for fetching all autoruns given host id and scan time
 * @method getFileContextAutoruns
 * @public
 * @returns {Object}
 */
const getFileContextAutoruns = () => {
  return (dispatch, getState) => {
    // Get selected agentId and scan time from the state
    const { endpoint: { detailsInput: { agentId, scanTime } } } = getState();
    const data = {
      agentId,
      scanTime,
      categories: ['AUTORUNS']
    };
    dispatch({
      type: ACTION_TYPES.FETCH_FILE_CONTEXT_AUTORUNS,
      promise: HostDetails.getFileContextData(data),
      meta: {
        onFailure: (response) => handleError(ACTION_TYPES.FETCH_FILE_CONTEXT_AUTORUNS, response)
      }
    });
  };
};

/**
* Action creator for fetching all services given host id and scan time
* @method getFileContextServices
* @public
* @returns {Object}
*/
const getFileContextServices = () => {
  return (dispatch, getState) => {
    // Get selected agentId and scan time from the state
    const { endpoint: { detailsInput: { agentId, scanTime } } } = getState();
    const data = {
      agentId,
      scanTime,
      categories: ['SERVICES']
    };
    dispatch({
      type: ACTION_TYPES.FETCH_FILE_CONTEXT_SERVICES,
      promise: HostDetails.getFileContextData(data),
      meta: {
        onFailure: (response) => handleError(ACTION_TYPES.FETCH_FILE_CONTEXT_SERVICES, response)
      }
    });
  };
};

/**
 * Action creator for fetching all tasks given host id and scan time
 * @method getFileContextTasks
 * @public
 * @returns {Object}
 */
const getFileContextTasks = () => {
  return (dispatch, getState) => {
    // Get selected agentId and scan time from the state
    const { endpoint: { detailsInput: { agentId, scanTime } } } = getState();
    const data = {
      agentId,
      scanTime,
      categories: ['TASKS']
    };
    dispatch({
      type: ACTION_TYPES.FETCH_FILE_CONTEXT_TASKS,
      promise: HostDetails.getFileContextData(data),
      meta: {
        onFailure: (response) => handleError(ACTION_TYPES.FETCH_FILE_CONTEXT_TASKS, response)
      }
    });
  };
};

const setSelectedRow = ({ id }) => ({ type: ACTION_TYPES.SET_AUTORUN_SELECTED_ROW, payload: { id } });
const toggleAutorunSelection = (selectedAutorun) => ({ type: ACTION_TYPES.TOGGLE_SELECTED_AUTORUN, payload: selectedAutorun });
const toggleAllAutorunSelection = () => ({ type: ACTION_TYPES.TOGGLE_ALL_AUTORUN_SELECTION });

const saveAutorunStatus = (checksums, data, callbacks = callbacksDefault) => ({
  type: ACTION_TYPES.SAVE_AUTORUN_STATUS,
  promise: setFileStatus({ ...data, checksums }),
  meta: {
    onSuccess: (response) => {
      callbacks.onSuccess(response);
    },
    onFailure: (response) => {
      callbacks.onFailure(response);
    }
  }
});

const getSavedAutorunStatus = (selections) => ({
  type: ACTION_TYPES.GET_AUTORUN_STATUS,
  promise: getFileStatus(selections)
});

const toggleServiceSelection = (selectedService) => ({ type: ACTION_TYPES.TOGGLE_SELECTED_SERVICE, payload: selectedService });
const toggleAllServiceSelection = () => ({ type: ACTION_TYPES.TOGGLE_ALL_SERVICE_SELECTION });

const saveServiceStatus = (checksums, data, callbacks = callbacksDefault) => ({
  type: ACTION_TYPES.SAVE_SERVICE_STATUS,
  promise: setFileStatus({ ...data, checksums }),
  meta: {
    onSuccess: (response) => {
      callbacks.onSuccess(response);
    },
    onFailure: (response) => {
      callbacks.onFailure(response);
    }
  }
});

const getSavedServiceStatus = (selections) => ({
  type: ACTION_TYPES.GET_SERVICE_STATUS,
  promise: getFileStatus(selections)
});

const toggleTaskSelection = (selectedTask) => ({ type: ACTION_TYPES.TOGGLE_SELECTED_TASK, payload: selectedTask });
const toggleAllTaskSelection = () => ({ type: ACTION_TYPES.TOGGLE_ALL_TASK_SELECTION });

const saveTaskStatus = (checksums, data, callbacks = callbacksDefault) => ({
  type: ACTION_TYPES.SAVE_TASK_STATUS,
  promise: setFileStatus({ ...data, checksums }),
  meta: {
    onSuccess: (response) => {
      callbacks.onSuccess(response);
    },
    onFailure: (response) => {
      callbacks.onFailure(response);
    }
  }
});

const getSavedTaskStatus = (selections) => ({
  type: ACTION_TYPES.GET_TASK_STATUS,
  promise: getFileStatus(selections)
});

export {
  getFileContextAutoruns,
  getFileContextServices,
  getFileContextTasks,
  setSelectedRow,
  toggleAutorunSelection,
  toggleAllAutorunSelection,
  saveAutorunStatus,
  getSavedAutorunStatus,
  toggleAllServiceSelection,
  toggleServiceSelection,
  saveServiceStatus,
  getSavedServiceStatus,
  toggleAllTaskSelection,
  toggleTaskSelection,
  saveTaskStatus,
  getSavedTaskStatus
};
