import { HostDetails } from '../api';
import * as ACTION_TYPES from '../types';
import { handleError } from '../creator-utils';

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

export {
  getFileContextAutoruns,
  getFileContextServices,
  getFileContextTasks,
  setSelectedRow
};
