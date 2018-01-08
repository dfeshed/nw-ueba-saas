import * as ACTION_TYPES from '../types';
import { Process } from '../api';
import { handleError } from '../creator-utils';

const toggleProcessView = () => {
  return (dispatch, getState) => {
    dispatch({ type: ACTION_TYPES.TOGGLE_PROCESS_VIEW });
    const { process: { processTree, processList }, visuals: { isTreeView } } = getState().endpoint;
    if (isTreeView) {
      dispatch(getProcessDetails(processTree[0].pid));
    } else {
      dispatch(getProcessDetails(processList[0].pid));
    }
  };
};

const _getList = () => {
  return (dispatch, getState) => {
    const { sortField: key, isDescOrder: descending } = getState().endpoint.process;
    const { detailsInput: { agentId, scanTime }, visuals: { isTreeView } } = getState().endpoint;
    dispatch({
      type: ACTION_TYPES.GET_PROCESS_LIST,
      promise: Process.getProcessList({ agentId, scanTime }, { key, descending }),
      meta: {
        onSuccess: (response) => {
          if (!isTreeView && response.data.length) {
            dispatch(getProcessDetails(response.data[0].pid));
          }
        },
        onFailure: (response) => handleError(ACTION_TYPES.GET_PROCESS_LIST, response)
      }
    });
  };
};

const _getTree = () => {
  return (dispatch, getState) => {
    const { detailsInput: { agentId, scanTime }, visuals: { isTreeView } } = getState().endpoint;
    dispatch({
      type: ACTION_TYPES.GET_PROCESS_TREE,
      promise: Process.getProcessTree({ agentId, scanTime }),
      meta: {
        onSuccess: (response) => {
          if (isTreeView && response.data.length) {
            dispatch(getProcessDetails(response.data[0].pid));
          }
        },
        onFailure: (response) => handleError(ACTION_TYPES.GET_PROCESS_TREE, response)
      }
    });
  };
};

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
  return (dispatch) => {
    dispatch(_getTree());
    dispatch(_getList());
  };
};

const getProcessDetails = (processId) => {
  return (dispatch, getState) => {
    const { agentId, scanTime } = getState().endpoint.detailsInput;
    dispatch({
      type: ACTION_TYPES.GET_PROCESS,
      promise: Process.getProcess({ agentId, scanTime, pid: processId }),
      meta: {
        onSuccess: () => {
          dispatch(_getProcessFileContext(processId));
        },
        onFailure: (response) => handleError(ACTION_TYPES.GET_PROCESS, response)
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
        onFailure: (response) => handleError(ACTION_TYPES.GET_PROCESS_FILE_CONTEXT, response)
      }
    });
  };
};

export {
  sortBy,
  toggleProcessView,
  getProcessDetails,
  getAllProcess
};
