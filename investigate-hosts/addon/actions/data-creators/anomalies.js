import { HostDetails } from '../api';
import * as ACTION_TYPES from '../types';
import { handleError } from '../creator-utils';

/**
 * Action creator for fetching all hooks given host id and scan time
 * @method getFileContextHooks
 * @public
 * @returns {Object}
 */
const getFileContextHooks = () => {
  return (dispatch, getState) => {
    // Get selected agentId and scan time from the state
    const { endpoint: { detailsInput: { agentId, scanTime } } } = getState();
    const data = {
      agentId,
      scanTime,
      categories: ['IMAGE_HOOKS']
    };
    dispatch({
      type: ACTION_TYPES.FETCH_FILE_CONTEXT_IMAGE_HOOKS,
      promise: HostDetails.getFileContextData(data),
      meta: {
        onFailure: (response) => handleError(ACTION_TYPES.FETCH_FILE_CONTEXT_IMAGE_HOOKS, response)
      }
    });
  };
};

/**
 * Action creator for fetching all suspicious threads given host id and scan time
 * @method getFileContextThreads
 * @public
 * @returns {Object}
 */
const getFileContextThreads = () => {
  return (dispatch, getState) => {
    // Get selected agentId and scan time from the state
    const { endpoint: { detailsInput: { agentId, scanTime } } } = getState();
    const data = {
      agentId,
      scanTime,
      categories: ['THREADS']
    };
    dispatch({
      type: ACTION_TYPES.FETCH_FILE_CONTEXT_THREADS,
      promise: HostDetails.getFileContextData(data),
      meta: {
        onFailure: (response) => handleError(ACTION_TYPES.FETCH_FILE_CONTEXT_THREADS, response)
      }
    });
  };
};


/**
 * Action creator for fetching all Kernel Hooks given host id and scan time
 * @method getFileContextKernelHooks
 * @public
 * @returns {Object}
 */
const getFileContextKernelHooks = () => {
  return (dispatch, getState) => {
    // Get selected agentId and scan time from the state
    const { endpoint: { detailsInput: { agentId, scanTime } } } = getState();
    const data = {
      agentId,
      scanTime,
      categories: ['KERNEL_HOOKS']
    };
    dispatch({
      type: ACTION_TYPES.FETCH_FILE_CONTEXT_KERNEL_HOOKS,
      promise: HostDetails.getFileContextData(data),
      meta: {
        onFailure: (response) => handleError(ACTION_TYPES.FETCH_FILE_CONTEXT_KERNEL_HOOKS, response)
      }
    });
  };
};

const setSelectedRow = ({ id }) => ({ type: ACTION_TYPES.SET_ANOMALIES_SELECTED_ROW, payload: { id } });

export {
  getFileContextHooks,
  getFileContextThreads,
  getFileContextKernelHooks,
  setSelectedRow
};
