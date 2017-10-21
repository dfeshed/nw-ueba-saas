import * as ACTION_TYPES from '../types';
import { Process } from '../api';
import Ember from 'ember';
const { Logger } = Ember;

const toggleProcessView = () => ({ type: ACTION_TYPES.TOGGLE_PROCESS_VIEW });

/**
 * Action Creator to sorting the process.
 * @return {function} redux-thunk
 * @public
 */
const sortBy = (sortField, isDescOrder) => {
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.SET_SORT_BY, payload: { sortField, isDescOrder } });
    dispatch({ type: ACTION_TYPES.RESET_PROCESS_LIST });
    dispatch(_getList());
  };
};

/**
 * Get the process list based in tree or flat list
 * @param isTreeView
 * @returns {function(*, *)}
 * @public
 */
const getAllProcess = () => {
  return (dispatch, getState) => {
    const { endpoint: { visuals: { isTreeView } } } = getState();
    dispatch(_getTree(isTreeView));
    dispatch(_getList(!isTreeView));
  };
};

const getProcessDetails = (processId) => {
  return (dispatch) => {
    dispatch(_getProcess(processId));
    dispatch(_getProcessFileContext(processId));
  };
};


const _getProcess = (processId) => {
  return (dispatch, getState) => {
    const { agentId, scanTime } = getState().endpoint.detailsInput;
    dispatch({
      type: ACTION_TYPES.GET_PROCESS,
      promise: Process.getProcess({ agentId, scanTime, pid: processId }),
      meta: {
        onSuccess: (response) => {
          Logger.debug(ACTION_TYPES.GET_PROCESS, response);
          dispatch(_getProcessFileContext(processId));
        },
        onFailure: (response) => _handleProcessError(ACTION_TYPES.GET_PROCESS, response)
      }
    });
  };
};

const _getList = (shouldGetFirstRecord) => {
  return (dispatch, getState) => {
    const { sortField: key, isDescOrder: descending } = getState().endpoint.process;
    const { agentId, scanTime } = getState().endpoint.detailsInput;
    dispatch({
      type: ACTION_TYPES.GET_PROCESS_LIST,
      promise: Process.getProcessList({ agentId, scanTime }, { key, descending }),
      meta: {
        onSuccess: (response) => {
          if (shouldGetFirstRecord && response.data.length) {
            dispatch(getProcessDetails(response.data[0].pid));
          }
        },
        onFailure: (response) => _handleProcessError(ACTION_TYPES.GET_PROCESS_LIST, response)
      }
    });
  };
};

const _getTree = (shouldGetFirstRecord) => {
  return (dispatch, getState) => {
    const { agentId, scanTime } = getState().endpoint.detailsInput;
    dispatch({
      type: ACTION_TYPES.GET_PROCESS_TREE,
      promise: Process.getProcessTree({ agentId, scanTime }),
      meta: {
        onSuccess: (response) => {
          if (shouldGetFirstRecord && response.data.length) {
            dispatch(getProcessDetails(response.data[0].pid));
          }
        },
        onFailure: (response) => _handleProcessError(ACTION_TYPES.GET_PROCESS_TREE, response)
      }
    });
  };
};

const _getProcessFileContext = (processId) => {
  return (dispatch, getState) => {
    const { agentId, scanTime } = getState().endpoint.detailsInput;
    dispatch({
      type: ACTION_TYPES.GET_PROCESS_FILE_CONTEXT,
      promise: Process.getProcessFileContext({ agentId, scanTime, pid: processId, categories: [ 'LOADED_LIBRARIES' ] }),
      meta: {
        onSuccess: (response) => {
          Logger.debug(ACTION_TYPES.GET_PROCESS_FILE_CONTEXT, response);
        },
        onFailure: (response) => _handleProcessError(ACTION_TYPES.GET_PROCESS_FILE_CONTEXT, response)
      }
    });
  };
};

const _handleProcessError = (type, response) => {
  Logger.error(type, response);
};

export {
  sortBy,
  toggleProcessView,
  getProcessDetails,
  getAllProcess
};
