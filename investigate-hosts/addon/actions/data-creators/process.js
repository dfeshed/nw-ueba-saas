import * as ACTION_TYPES from '../types';
import { Process } from '../api';
import { handleError } from '../creator-utils';
import { resetRiskContext, getRiskScoreContext, getRespondServerStatus } from 'investigate-shared/actions/data-creators/risk-creators';

const toggleProcessView = () => {
  return (dispatch, getState) => {
    dispatch(setRowIndex(null));
    dispatch({ type: ACTION_TYPES.TOGGLE_PROCESS_VIEW });
    const { process: { processTree, processList }, visuals: { isTreeView } } = getState().endpoint;
    if (isTreeView) {
      dispatch(_getProcessDetails(processTree[0].pid));
    } else {
      dispatch(_getProcessDetails(processList[0].pid));
    }
  };
};

const toggleProcessDetailsView = (item, isOpen = false) => {
  return (dispatch) => {
    dispatch(deSelectAllProcess());
    if (item) {
      const { pid } = item;
      dispatch(_getProcessDetails(pid));
    }
    dispatch({ type: ACTION_TYPES.TOGGLE_PROCESS_DETAILS_VIEW, payload: { isOpen } });
  };
};

const toggleSelectedProcessDllRow = (item) => ({ type: ACTION_TYPES.TOGGLE_PROCESS_DETAILS_ROW, payload: item });

const setDllRowSelectedId = (rowId) => ({ type: ACTION_TYPES.SET_PROCESS_DLL_ROW_ID, payload: rowId });

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
            dispatch(_getProcessDetails(response.data[0].pid));
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
            dispatch(_getProcessDetails(response.data[0].pid));
          }
        },
        onFailure: (response) => handleError(ACTION_TYPES.GET_PROCESS_TREE, response)
      }
    });
  };
};

/**
 * Action Creator to sorting the process.
 */
const sortBy = (sortField, isDescOrder) => ({ type: ACTION_TYPES.SET_SORT_BY, payload: { sortField, isDescOrder } });

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

const _getProcessDetails = (processId) => {
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

const onProcessSelection = (processId, checksumSha256) => {
  return (dispatch) => {
    dispatch(getRespondServerStatus());
    dispatch(resetRiskContext());
    dispatch(_getProcessDetails(processId));
    dispatch(getRiskScoreContext(checksumSha256, 'FILE', 'HOST'));
  };
};

const _getProcessFileContext = (processId) => {
  return (dispatch, getState) => {
    const { agentId, scanTime } = getState().endpoint.detailsInput;
    dispatch(_setSelectedProcessId(processId));
    dispatch({
      type: ACTION_TYPES.GET_PROCESS_FILE_CONTEXT,
      promise: Process.getProcessFileContext({ agentId, scanTime, pid: processId, categories: [ 'LOADED_LIBRARIES', 'IMAGE_HOOKS', 'THREADS' ] }),
      meta: {
        onFailure: (response) => handleError(ACTION_TYPES.GET_PROCESS_FILE_CONTEXT, response)
      }
    });
  };
};

const _setSelectedProcessId = (processId) => ({ type: ACTION_TYPES.SET_SELECTED_PROCESS_ID, payload: processId });

const toggleProcessSelection = (process) => ({ type: ACTION_TYPES.SET_SELECTED_PROCESS, payload: process });

const selectAllProcess = () => ({ type: ACTION_TYPES.SELECT_ALL_PROCESS });

const deSelectAllProcess = () => ({ type: ACTION_TYPES.DESELECT_ALL_PROCESS });

const setRowIndex = (index) => ({ type: ACTION_TYPES.SET_ROW_INDEX, payload: index });

export {
  sortBy,
  toggleProcessView,
  onProcessSelection,
  getAllProcess,
  toggleProcessSelection,
  selectAllProcess,
  deSelectAllProcess,
  setRowIndex,
  toggleProcessDetailsView,
  toggleSelectedProcessDllRow,
  setDllRowSelectedId
};
