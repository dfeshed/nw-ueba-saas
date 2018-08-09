import { lookup } from 'ember-dependency-lookup';
import { Promise } from 'rsvp';
import * as ACTION_TYPES from './types';

const MAX_BATCH_SIZE = 10000;
const LOG_FETCH_INTERVAL = 1000;

const connect = (onConnected = () => {}) => {
  const transport = lookup('service:transport');
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.WS_CONNECT_START
    });
    transport.connect(
      () => {
        dispatch({ type: ACTION_TYPES.WS_CONNECT_FINISH });
        onConnected();
        dispatch(_getDeviceInfo());
      },
      (error) => dispatch({ type: ACTION_TYPES.WS_ERROR, payload: error })
    );
  };
};

const disconnect = () => {
  const transport = lookup('service:transport');
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.WS_DISCONNECT,
      promise: transport.disconnect()
    });
    dispatch(_clearTree());
  };
};

// `path` is absolute
const changeDirectory = (path) => {
  const transport = lookup('service:transport');
  return (dispatch, getState) => {
    dispatch(deselectOperation());
    dispatch(deselectStat());
    transport.stopStream(getState().treeMonitorStreamTid);
    const tid = transport.stream({
      path,
      message: {
        message: 'mon',
        params: {
          depth: '1'
        }
      },
      messageCallback: (updateMessage) => {
        dispatch(_updateTreeContents(updateMessage));
      },
      errorCallback: (errorMessage) => {
        throw new Error(errorMessage);
      }
    });
    dispatch({
      type: ACTION_TYPES.TREE_CHANGE_DIRECTORY,
      payload: {
        path,
        tid
      }
    });
    dispatch(_listContents(path));
    dispatch(_getOperations(path));
    dispatch(_getDescription(path));
  };
};

const selectOperation = (operationName) => {
  return (dispatch, getState) => {
    dispatch({
      type: ACTION_TYPES.TREE_SELECT_OPERATION,
      payload: operationName
    });
    dispatch(_getOperationParamHelp(getState().treePath, operationName));
    dispatch(_getOperationHelp(getState().treePath, operationName));
  };
};

const deselectOperation = () => {
  return {
    type: ACTION_TYPES.TREE_DESELECT_OPERATION
  };
};

const updateOperationParams = (newParams) => {
  return {
    type: ACTION_TYPES.TREE_UPDATE_OPERATION_PARAMS,
    payload: newParams
  };
};

const updateCustomParameter = (newParam) => {
  return {
    type: ACTION_TYPES.TREE_UPDATE_CUSTOM_PARAM,
    payload: newParam
  };
};

const sendOperation = (operationMessageObject) => {
  const transport = lookup('service:transport');
  return (dispatch, getState) => {
    dispatch({
      type: ACTION_TYPES.TREE_SEND_OPERATION,
      promise: transport.send(getState().treePath, operationMessageObject)
    });
  };
};

const setConfigValue = (newValue) => {
  const transport = lookup('service:transport');
  return (dispatch, getState) => {
    dispatch({
      type: ACTION_TYPES.TREE_SET_CONFIG_VALUE,
      promise: transport.send(getState().selectedNode.path, {
        message: 'set',
        params: {
          value: newValue
        }
      })
    });
  };
};

const changeActiveTab = (tab) => {
  return {
    type: ACTION_TYPES.APP_CHANGE_ACTIVE_TAB,
    payload: tab
  };
};

const selectNode = (node) => {
  const transport = lookup('service:transport');
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.TREE_GET_NODE_DESCRIPTION,
      promise: transport.send(node.path, {
        message: 'help',
        params: {
          op: 'description'
        }
      })
    });
    dispatch({
      type: ACTION_TYPES.TREE_SET_SELECTED_NODE,
      payload: node
    });
  };
};

const deselectStat = () => {
  return {
    type: ACTION_TYPES.TREE_DESELECT_SELECTED_NODE
  };
};

const _loadLogs = ({ params, dispatch, transport }) => {
  return transport.send('/logs', {
    message: 'download',
    params: {
      ...params,
      op: 'start'
    }
  })
  .then((response) => {
    dispatch({
      type: ACTION_TYPES.LOGS_ADD_NEW,
      payload: response.params
    });
    if (response.params.length === MAX_BATCH_SIZE) {
      // If our query exceeded the batch size, send another adjusted for the
      // responses we've received. Otherwise, return an instantly resolving
      // promise to keep the chaining moving
      if (params.id1 && params.id2 && (params.id2 - params.id1 > MAX_BATCH_SIZE)) {
        return _loadLogs({ params: {
          ...params,
          id1: (parseInt(params.id1, 10) + MAX_BATCH_SIZE).toString()
        }, dispatch, transport });
      } else if (params.time1 && params.time2) {
        // This can possibly produce overlapping results, because we can't know
        // how many logs exist at one posix time point. The reducer handles overlapping
        // results, so don't worry about it here.
        return _loadLogs({ params: {
          ...params,
          time1: response.params[response.params.length - 1].time
        }, dispatch, transport });
      } else {
        return new Promise((resolve) => {
          resolve();
        });
      }
    }
  });
};

const loadLogs = (params) => {
  const transport = lookup('service:transport');
  return (dispatch) => {
    let top;
    const { latest, count } = params;

    // Start loading
    dispatch({
      type: ACTION_TYPES.LOGS_LOAD_START
    });

    if (params.logTypes === '') {
      // Occurs if the user has unchecked every type box
      // No need to send API call
      dispatch({
        type: ACTION_TYPES.LOGS_LOAD_FINISH
      });
      return;
    }

    new Promise((resolve) => {
      if (latest) {
        return transport.send('/logs/stats/last.id', {
          message: 'get'
        }).then((response) => {
          resolve(response);
        });
      } else {
        resolve();
      }
    })
    .then((response) => {
      if (latest) {
        top = parseInt(response.string, 10);
        params.id1 = Math.max(top - params.count + 1, 0).toString();
        params.id2 = (top + 1).toString();
        delete params.count;
        delete params.latest;
      }
      return _loadLogs({ params, dispatch, transport });
    }).then(() => {
      dispatch({
        type: ACTION_TYPES.LOGS_LOAD_FINISH
      });
      if (latest) {
        const intervalHandle = setInterval(() => {
          transport.send('/logs/stats/last.id', {
            message: 'get'
          }).then((response) => {
            const newTop = parseInt(response.string, 10);
            if (newTop > top) {
              transport.send('/logs', {
                message: 'download',
                params: {
                  id1: (top + 1).toString(),
                  id2: (newTop + 1).toString(),
                  op: 'start'
                }
              })
              .then((response) => {
                dispatch({
                  type: ACTION_TYPES.LOGS_UPDATE,
                  payload: {
                    count,
                    logs: response.params
                  }
                });
              });
              top = newTop;
            }
          });
        }, LOG_FETCH_INTERVAL);
        dispatch({
          type: ACTION_TYPES.LOGS_INTERVAL_HANDLE,
          payload: intervalHandle
        });
      }
    });
  };
};

const logsClearInterval = () => {
  return {
    type: ACTION_TYPES.LOGS_INTERVAL_HANDLE,
    payload: null
  };
};

const _listContents = (path) => {
  const transport = lookup('service:transport');
  return {
    type: ACTION_TYPES.TREE_LIST_CONTENTS,
    promise: transport.send(path, {
      message: 'ls'
    })
  };
};

const _getDeviceInfo = () => {
  const transport = lookup('service:transport');
  return {
    type: ACTION_TYPES.TREE_GET_DEVICE_INFO,
    promise: transport.send('/sys/stats', {
      message: 'ls'
    })
  };
};

const _updateTreeContents = (update) => {
  return {
    type: ACTION_TYPES.TREE_UPDATE_CONTENTS,
    payload: update
  };
};

const _getOperations = (path) => {
  const transport = lookup('service:transport');
  return {
    type: ACTION_TYPES.TREE_GET_OPERATIONS,
    promise: transport.send(path, {
      message: 'help',
      params: {
        op: 'messages'
      }
    })
  };
};

const _getDescription = (path) => {
  const transport = lookup('service:transport');
  return {
    type: ACTION_TYPES.TREE_GET_DESCRIPTION,
    promise: transport.send(path, {
      message: 'help',
      params: {
        op: 'description'
      }
    })
  };
};

const _getOperationHelp = (path, operation) => {
  const transport = lookup('service:transport');
  return {
    type: ACTION_TYPES.TREE_GET_OPERATION_HELP,
    promise: transport.send(path, {
      message: 'help',
      params: {
        msg: operation
      }
    })
  };
};

const _getOperationParamHelp = (path, operation) => {
  const transport = lookup('service:transport');
  return {
    type: ACTION_TYPES.TREE_GET_OPERATION_PARAM_HELP,
    promise: transport.send(path, {
      message: 'help',
      params: {
        msg: operation,
        op: 'parameters'
      }
    })
  };
};

const _clearTree = () => {
  return {
    type: ACTION_TYPES.TREE_CLEAR
  };
};

export {
  connect,
  disconnect,
  changeDirectory,
  selectOperation,
  deselectOperation,
  updateOperationParams,
  updateCustomParameter,
  sendOperation,
  changeActiveTab,
  selectNode,
  deselectStat,
  setConfigValue,
  loadLogs,
  logsClearInterval
};