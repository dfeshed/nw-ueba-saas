import Immutable from 'seamless-immutable';
import { handle } from 'redux-pack';
import { handleActions } from 'redux-actions';
import * as ACTION_TYPES from '../actions/types';
import { operationNames, selectedOperation } from './selectors';
import parseOperationParams from './selectors/parse-operation-params';
import parseParamHelp from './selectors/parse-param-help';
import parseFlags from '../services/transport/parse-flags';

const initialState = Immutable.from({
  activeTab: null,
  wsConnecting: false,
  wsConnected: false,
  wsErr: null,
  treePath: '/',
  treePathContents: null,
  deviceInfo: {},
  username: null,
  availablePermissions: null,
  treeSelectedOperationIndex: -1,
  treeOperationParams: {},
  operationResponse: null,
  responseExpanded: false,
  responseAsJson: false,
  treeMonitorStreamTid: null,
  selectedNode: null,
  appStatNodes: {},
  logs: null,
  logsFilterChangePending: false,
  logsLoading: false,
  logsLastLoaded: '0'
});

const infoNodeNames = [
  'build.date', 'config.filename', 'hostname', 'module',
  'release', 'service.name', 'uuid', 'version'
];

const reducer = handleActions({

  [ACTION_TYPES.WS_CONNECT_START]: (state) => {
    return state.merge({
      wsConnecting: true,
      wsConnected: false,
      wsErr: null
    });
  },

  [ACTION_TYPES.WS_CONNECT_FINISH]: (state) => {
    return state.merge({ wsConnecting: false, wsConnected: true });
  },

  [ACTION_TYPES.WS_ERROR]: (state, action) => {
    return state.merge({
      wsConnecting: false,
      wsConnected: false,
      wsErr: action.payload
    });
  },

  [ACTION_TYPES.WS_DISCONNECT]: (state, action) => {
    return handle(state, action, {
      finish: (prevState) => prevState.merge(initialState)
    });
  },

  [ACTION_TYPES.TREE_LIST_CONTENTS]: (state, action) => {
    const { payload } = action;
    return handle(state, action, {
      finish: (prevState) => prevState.set('treePathContents', payload)
    });
  },

  [ACTION_TYPES.TREE_GET_DEVICE_INFO]: (state, action) => {
    return handle(state, action, {
      finish: (prevState) => {
        const { nodes } = action.payload;
        for (let i = 0; i < nodes.length; i++) {
          const node = nodes[i];
          if (infoNodeNames.includes(node.name)) {
            prevState = prevState.setIn(['deviceInfo', node.name.camelize()], node.value);
          }
        }
        return prevState;
      }
    });
  },

  [ACTION_TYPES.TREE_UPDATE_CONTENTS]: (state, { payload }) => {
    const { node } = payload;
    if (!node) {
      return state;
      // If payload has a `nodes` property instead of `node`,
      // it's because this is the initial message we recieve when
      // we start monitoring a path
    }
    let nodeList = state.treePathContents.nodes;
    switch (node.action) {
      case 'added':
        // TODO: Add at correct sorted position
        nodeList = nodeList.concat(node);
        break;
      case 'deleted':
        nodeList = nodeList.filter((childNode) => {
          return childNode.path !== node.path;
        });
        break;
      default:
        nodeList = nodeList.map((childNode) => {
          if (childNode.path === node.path) {
            return childNode.merge(node);
          } else {
            return childNode;
          }
        });
    }

    return state.setIn(
      [ 'treePathContents', 'nodes' ],
      nodeList
    );
  },

  [ACTION_TYPES.TREE_CHANGE_DIRECTORY]: (state, action) => {
    return state.merge({
      treePath: action.payload.path,
      treeMonitorStreamTid: action.payload.tid
    });
  },

  [ACTION_TYPES.TREE_UPDATE_RESPONSE]: (state, action) => {

    const flags = parseFlags(action.payload.flags);
    if (flags.statusUpdate && action.payload.params && action.payload.params.percent) {
      state = state.setIn(['operationResponse', 'progress'], action.payload.params.percent);
      if (action.payload.params.description) {
        state = state.setIn(['operationResponse', 'status'], action.payload.params.description);
      }
    }
    if (flags.complete || flags.error) {
      state = state.setIn(['operationResponse', 'complete'], true);
    }
    if (flags.error) {
      const error = action.payload.error.message.replace(/\n/g, '\n')
        .replace(/\t/g, '  ');
      state = state.setIn(['operationResponse', 'error'], error);
    }
    if (flags.dataType) {
      state = state.setIn(['operationResponse', 'dataType'], flags.dataType);
    }
    const raw = state.operationResponse.raw || [];
    state = state.setIn(['operationResponse', 'raw'], raw.concat(action.payload));

    return state;
  },

  [ACTION_TYPES.TREE_TOGGLE_OPERATION_RESPONSE]: (state) => {
    return state.set('responseExpanded', !state.responseExpanded);
  },

  [ACTION_TYPES.TREE_TOGGLE_RESPONSE_AS_JSON]: (state) => {
    return state.set('responseAsJson', !state.responseAsJson);
  },

  [ACTION_TYPES.TREE_SET_REQUEST]: (state, action) => {
    // new request, clear current request state
    return state.set('operationResponse', {
      requestId: action.tid,
      complete: false,
      error: null,
      progress: null,
      raw: []
    });
  },

  [ACTION_TYPES.TREE_CANCELLED_REQUEST]: (state) => {
    return state.setIn(['operationResponse', 'complete'], true);
  },

  [ACTION_TYPES.TREE_GET_OPERATIONS]: (state, action) => {
    return handle(state, action, {
      finish: (prevState) => prevState.setIn(
        [ 'treePathContents', 'operations' ],
        Object.keys(action.payload.params).map((key) => {
          return { name: key, params: parseOperationParams(action.payload.params[key]) };
        }).sort((a, b) => a.name.localeCompare(b.name))
      )
    });
  },

  [ACTION_TYPES.TREE_GET_OPERATION_HELP]: (state, action) => {
    return handle(state, action, {
      finish: (prevState) => {
        const [ operationName ] = Object.keys(action.payload.params);
        const operations = prevState.treePathContents.operations.map((op) => {
          if (op.name == operationName) {
            return op.set('description', action.payload.params[operationName]);
          } else {
            return op;
          }
        });
        return prevState.setIn(['treePathContents', 'operations'], operations);
      }
    });
  },

  [ACTION_TYPES.TREE_GET_OPERATION_PARAM_HELP]: (state, action) => {
    // All this does is add a `description` key:value pair to each parameter
    // under the currently selected operation which we got a description for
    return handle(state, action, {
      finish: (prevState) => {
        const { payload } = action;
        const paramDescriptionKeys = Object.keys(payload.params);
        const operation = selectedOperation(prevState);
        const params = operation.params.asMutable({ deep: true });
        for (let i = 0; i < paramDescriptionKeys.length; i++) {
          const key = paramDescriptionKeys[i];
          const paramIndex = params.findIndex((param) => {
            return key === param.name;
          });
          params[paramIndex] = parseParamHelp(params[paramIndex], payload.params[key]);
        }
        const operations = prevState.treePathContents.operations.asMutable({ deep: true });
        const operationIndex = operations.findIndex((op) => {
          return op.name === operation.name;
        });
        operations[operationIndex].params = params;
        return prevState.setIn(
          [ 'treePathContents', 'operations' ],
          operations
        );
      }
    });
  },

  [ACTION_TYPES.TREE_UPDATE_PARAM]: (state, action) => {
    const payload = Immutable.from(action.payload);
    const operation = selectedOperation(state);
    let newOperation;
    if (payload.method === 'add') {
      newOperation = operation.set('params', operation.params.concat(payload.without('method')));
    } else if (payload.method === 'hide') {
      newOperation = operation.set('params', operation.params.map((param) => {
        if (param.name === payload.name) {
          return { ...param, hidden: true };
        } else {
          return param;
        }
      }));
    } else if (payload.method === 'show') {
      newOperation = operation.set('params', operation.params.map((param) => {
        if (param.name === payload.name) {
          return { ...param, hidden: false };
        } else {
          return param;
        }
      }));
    } else if (payload.method === 'delete') {
      newOperation = operation.set('params', operation.params.filter((param) => {
        return param.name !== payload.name;
      }));
    }
    const operations = state.treePathContents.operations.map((op) => {
      if (op.name === newOperation.name) {
        return newOperation;
      } else {
        return op;
      }
    });
    return state.setIn(['treePathContents', 'operations'], operations);
  },

  [ACTION_TYPES.TREE_GET_DESCRIPTION]: (state, action) => {
    return handle(state, action, {
      finish: (prevState) => prevState.setIn(['treePathContents', 'description'], action.payload.params.description)
    });
  },

  [ACTION_TYPES.TREE_SET_SELECTED_NODE]: (state, action) => {
    return state.set('selectedNode', action.payload);
  },

  [ACTION_TYPES.TREE_DESELECT_SELECTED_NODE]: (state) => {
    return state.set('selectedNode', null);
  },

  [ACTION_TYPES.TREE_GET_NODE_DESCRIPTION]: (state, action) => {
    return handle(state, action, {
      finish: (prevState) => prevState.setIn(['selectedNode', 'description'], action.payload.params.description)
    });
  },

  [ACTION_TYPES.TREE_SELECT_OPERATION]: (state, action) => {
    return state.merge({
      'treeSelectedOperationIndex': operationNames(state).indexOf(action.payload),
      'operationResponse': null,
      'treeOperationParams': {}
    });
  },

  [ACTION_TYPES.TREE_DESELECT_OPERATION]: (state) => {
    return state.merge({
      'treeSelectedOperationIndex': -1,
      'operationResponse': null,
      'treeOperationParams': {}
    });
  },

  [ACTION_TYPES.TREE_UPDATE_OPERATION_PARAMS]: (state, action) => {
    return state.set('treeOperationParams', action.payload);
  },

  [ACTION_TYPES.TREE_SET_CONFIG_VALUE]: (state, action) => {
    return handle(state, action, {
      finish: (prevState) => {
        if (action.payload.string === 'Success') {
          return prevState.setIn(['selectedNode', 'configSetResult'], true);
        } else if (action.payload.error) {
          return prevState.setIn(['selectedNode', 'configSetResult'], action.payload.error.message);
        }
      }
    });
  },

  [ACTION_TYPES.TREE_CLEAR]: (state) => {
    return state.merge({ treePath: '/', treePathContents: null });
  },

  [ACTION_TYPES.APP_CHANGE_ACTIVE_TAB]: (state, action) => {
    return state.merge({
      'activeTab': action.payload,
      'responseExpanded': false
    });
  },

  [ACTION_TYPES.APP_GET_USER]: (state, action) => {
    const username = action.payload;
    return state.set('username', username);
  },

  [ACTION_TYPES.APP_GET_AVAILABLE_PERMISSIONS]: (state, action) => {
    return handle(state, action, {
      finish: (prevState) => {
        const permissions = action.payload;
        return prevState.set('availablePermissions', permissions);
      }
    });
  },

  [ACTION_TYPES.LOGS_LOAD_START]: (state) => {
    return state.merge({
      logs: [],
      logsLoading: true
    });
  },

  [ACTION_TYPES.LOGS_LOAD_FINISH]: (state) => {
    return state.set('logsLoading', false);
  },

  [ACTION_TYPES.LOGS_ADD_NEW]: (state, action) => {
    let logs = action.payload;
    if (logs.length > 0) {
      const prevHighestId = state.logs.length > 0 ? parseInt(state.logs[state.logs.length - 1].id, 10) : -1;
      if (parseInt(logs[0].id, 10) <= parseInt(prevHighestId, 10)) {
        // Remove duplicate logs
        logs = logs.slice(logs.findIndex((log) => {
          return parseInt(log.id, 10) > prevHighestId;
        }));
      }
      return state.merge({
        logs: state.logs.concat(logs),
        logsLastLoaded: logs[logs.length - 1].id
      });
    } else {
      return state;
    }
  },

  [ACTION_TYPES.LOGS_FILTER_CHANGE]: (state) => {
    return state.set('logsFilterChangePending', true);
  },

  [ACTION_TYPES.LOGS_FILTER_CHANGE_DONE]: (state) => {
    return state.set('logsFilterChangePending', false);
  },

  [ACTION_TYPES.LOGS_UPDATE]: (state, action) => {
    const { payload: { count, logs } } = action;
    if (logs.length > 0) {
      if (logs.length + state.logs.length > count) {
        const over = state.logs.length + logs.length - count;
        return state.set('logs', state.logs.concat(logs).slice(over));
      } else {
        return state.set('logs', state.logs.concat(logs));
      }
    } else {
      return state;
    }
  }

}, initialState);

export default reducer;
