import { test, module } from 'qunit';
import { setupTest } from 'ember-qunit';
import reducer from 'ngcoreui/reducers/index';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../helpers/make-pack-action';
import * as ACTION_TYPES from 'ngcoreui/actions/types';
import ReduxDataHelper, {
  sysResponse,
  sysStatsResponse,
  operationsResponse,
  updateResponseChange,
  updateResponseAdd,
  updateResponseDelete,
  paramHelpResponse
} from '../../helpers/redux-data-helper';

const dummyResponse = {
  message: 'Dummy text response'
};

const stat = {
  name: 'cpu',
  display: 'CPU',
  path: '/sys/stats/cpu',
  value: '42%'
};

module('Unit | Reducers | Tree', (hooks) => {

  setupTest(hooks);

  test('TREE_LIST_CONTENTS adds its contents to treeContents', (assert) => {
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.TREE_LIST_CONTENTS,
      payload: sysResponse
    });
    const state = new ReduxDataHelper().connected().treePathContentsStandard().build();
    const result = reducer(state, action);

    assert.strictEqual(result.treePath, '/'); // Default route, wasn't changed here
    assert.deepEqual(sysResponse, result.treePathContents);
  });

  test('TREE_GET_DEVICE_INFO adds its contents to deviceInfo', (assert) => {
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.TREE_GET_DEVICE_INFO,
      payload: sysStatsResponse
    });
    const state = new ReduxDataHelper().connected().treePathContentsStandard().build();
    const result = reducer(state, action);

    assert.strictEqual(result.treePath, '/');
    assert.strictEqual(result.deviceInfo.buildDate, 'Jun 11 2018');
    assert.strictEqual(result.deviceInfo.configFilename, 'NwDecoder.cfg');
    assert.notOk(result.deviceInfo.cpu);
    assert.notOk(result.deviceInfo.cpuProcess);
    assert.notOk(result.deviceInfo.currentTime);
    assert.strictEqual(result.deviceInfo.hostname, 'HOSTNAME');
    assert.notOk(result.deviceInfo.memoryProcess);
    assert.notOk(result.deviceInfo.memoryProcessMax);
    assert.notOk(result.deviceInfo.memorySystem);
    assert.notOk(result.deviceInfo.memoryTotal);
    assert.strictEqual(result.deviceInfo.module, 'decoder');
    assert.strictEqual(result.deviceInfo.release, '0.3.72e98db83');
    assert.notOk(result.deviceInfo.runningSince);
    assert.strictEqual(result.deviceInfo.serviceName, 'SERVICENAME');
    assert.notOk(result.deviceInfo.serviceStatus);
    assert.notOk(result.deviceInfo.systemInfo);
    assert.notOk(result.deviceInfo.uptime);
    assert.strictEqual(result.deviceInfo.uuid, '728bd27c-a5f7-450e-b8e4-977b5b419989');
    assert.strictEqual(result.deviceInfo.version, '11.2.0.0');
  });

  test('TREE_CHANGE_DIRECTORY changes the active path and stream tid', (assert) => {
    const action = {
      type: ACTION_TYPES.TREE_CHANGE_DIRECTORY,
      payload: {
        path: '/sys/stats',
        tid: '3'
      }
    };
    const state = new ReduxDataHelper().connected().treePathContentsStandard().build();
    assert.notStrictEqual(state, action.payload);

    const result = reducer(state, action);

    assert.strictEqual(result.treePath, '/sys/stats', 'treePath is /sys/stats');
    assert.strictEqual(result.treeMonitorStreamTid, '3', 'treePath is /sys/stats');
  });

  test('TREE_GET_OPERATIONS adds its operations to treeContents', (assert) => {
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.TREE_GET_OPERATIONS,
      payload: operationsResponse
    });
    const state = new ReduxDataHelper().connected().treePathContentsEmpty().build();
    const result = reducer(state, action);

    assert.strictEqual(result.treePath, '/');
    assert.strictEqual(result.treePathContents.operations.length, 6);
  });

  test('TREE_CLEAR empties the tree data', (assert) => {
    const action = {
      type: ACTION_TYPES.TREE_CLEAR
    };
    const state = new ReduxDataHelper().connected().treePathContentsStandard().build();
    assert.notDeepEqual(state.treeContents, {});

    const result = reducer(state, action);

    assert.strictEqual(result.treePath, '/', 'treePath is /');
    assert.deepEqual(result.treePathContents, null);
  });

  test('TREE_SELECT_OPERATION sets the index of the operation and clears operationResponse', (assert) => {
    const action = {
      type: ACTION_TYPES.TREE_SELECT_OPERATION,
      payload: 'ls'
    };
    const state = new ReduxDataHelper()
      .connected()
      .treePathContentsStandard()
      .operationResponse(dummyResponse)
      .build();

    const result = reducer(state, action);

    assert.strictEqual(result.treePath, '/', 'treePath is /');
    assert.strictEqual(result.treeSelectedOperationIndex, 0, 'treeSelectedOperationIndex is the correct value');
    assert.strictEqual(result.operationResponse, null, 'operationResponse is cleared');
  });

  test('TREE_DESELECT_OPERATION resets the index of the operation and clears operationResponse', (assert) => {
    const action = {
      type: ACTION_TYPES.TREE_DESELECT_OPERATION
    };
    const state = new ReduxDataHelper()
      .connected()
      .treePathContentsStandard()
      .treeSelectedOperationIndex(2)
      .operationResponse(dummyResponse)
      .build();

    const result = reducer(state, action);

    assert.strictEqual(result.treePath, '/', 'treePath is /');
    assert.strictEqual(result.treeSelectedOperationIndex, -1, 'treeSelectedOperationIndex is the correct value');
    assert.strictEqual(result.operationResponse, null, 'operationResponse is cleared');
  });

  test('TREE_UPDATE_RESPONSE sets operationResponse to its value', (assert) => {

    const action = {
      type: ACTION_TYPES.TREE_UPDATE_RESPONSE,
      payload: dummyResponse
    };
    let state = new ReduxDataHelper()
      .connected()
      .treePathContentsStandard()
      .build();

    // required initialization of state
    state = reducer(state, {
      type: ACTION_TYPES.TREE_SET_REQUEST,
      tid: 42
    });

    const result = reducer(state, action);

    assert.strictEqual(result.treePath, '/', 'treePath is /');
    assert.deepEqual(result.operationResponse.raw[0], dummyResponse, 'the response was added to state');
  });

  test('TREE_TOGGLE_OPERATION_RESPONSE sets responseExpanded to true when it is false', (assert) => {
    const action = {
      type: ACTION_TYPES.TREE_TOGGLE_OPERATION_RESPONSE
    };

    const state = new ReduxDataHelper()
      .connected()
      .treePathContentsStandard()
      .responseExpanded(false)
      .build();

    assert.strictEqual(state.responseExpanded, false);

    const result = reducer(state, action);

    assert.strictEqual(result.responseExpanded, true);
  });

  test('TREE_TOGGLE_OPERATION_RESPONSE sets responseExpanded to false when it is true', (assert) => {
    const action = {
      type: ACTION_TYPES.TREE_TOGGLE_OPERATION_RESPONSE
    };

    const state = new ReduxDataHelper()
      .connected()
      .treePathContentsStandard()
      .responseExpanded(true)
      .build();

    assert.strictEqual(state.responseExpanded, true);

    const result = reducer(state, action);

    assert.strictEqual(result.responseExpanded, false);
  });

  test('TREE_TOGGLE_RESPONSE_AS_JSON sets responseAsJson to true when it is false', (assert) => {
    const action = {
      type: ACTION_TYPES.TREE_TOGGLE_RESPONSE_AS_JSON
    };

    const state = new ReduxDataHelper()
      .connected()
      .treePathContentsStandard()
      .responseAsJson(false)
      .build();

    assert.strictEqual(state.responseAsJson, false);

    const result = reducer(state, action);

    assert.strictEqual(result.responseAsJson, true);
  });

  test('TREE_TOGGLE_RESPONSE_AS_JSON sets responseAsJson to false when it is true', (assert) => {
    const action = {
      type: ACTION_TYPES.TREE_TOGGLE_RESPONSE_AS_JSON
    };

    const state = new ReduxDataHelper()
      .connected()
      .treePathContentsStandard()
      .responseAsJson(true)
      .build();

    assert.strictEqual(state.responseAsJson, true);

    const result = reducer(state, action);

    assert.strictEqual(result.responseAsJson, false);
  });

  test('TREE_TOGGLE_MANUAL_VISIBILITY sets operationManualVisible to true when it is false', (assert) => {
    const action = {
      type: ACTION_TYPES.TREE_TOGGLE_MANUAL_VISIBILITY
    };

    const state = new ReduxDataHelper()
      .connected()
      .treePathContentsStandard()
      .operationManualVisible(false)
      .build();

    assert.strictEqual(state.operationManualVisible, false);

    const result = reducer(state, action);

    assert.strictEqual(result.operationManualVisible, true);
  });

  test('TREE_TOGGLE_MANUAL_VISIBILITY sets operationManualVisible to false when it is true', (assert) => {
    const action = {
      type: ACTION_TYPES.TREE_TOGGLE_MANUAL_VISIBILITY
    };

    const state = new ReduxDataHelper()
      .connected()
      .treePathContentsStandard()
      .operationManualVisible(true)
      .build();

    assert.strictEqual(state.operationManualVisible, true);

    const result = reducer(state, action);

    assert.strictEqual(result.operationManualVisible, false);
  });

  test('TREE_SET_REQUEST sets the transport stream id', (assert) => {
    const action1 = {
      type: ACTION_TYPES.TREE_SET_REQUEST,
      tid: 12
    };
    const action2 = {
      type: ACTION_TYPES.TREE_CANCELLED_REQUEST
    };
    const state = new ReduxDataHelper()
      .connected()
      .treePathContentsStandard()
      .build();

    assert.strictEqual(state.operationResponse, null);

    const result1 = reducer(state, action1);

    assert.strictEqual(result1.operationResponse.requestId, action1.tid);
    assert.strictEqual(result1.operationResponse.complete, false);

    const result2 = reducer(result1, action2);

    assert.strictEqual(result1.operationResponse.requestId, action1.tid);
    assert.strictEqual(result2.operationResponse.complete, true);
  });

  test('TREE_UPDATE_CONTENTS changes a current object', (assert) => {
    const action = {
      type: ACTION_TYPES.TREE_UPDATE_CONTENTS,
      payload: updateResponseChange
    };
    const state = new ReduxDataHelper()
      .connected()
      .treePathContentsStandard()
      .build();

    // Points to the decoder node in our mock data
    assert.strictEqual(state.treePathContents.nodes[3].name, 'decoder');

    const result = reducer(state, action);

    assert.strictEqual(result.treePath, '/', 'treePath is /');

    assert.strictEqual(result.treePathContents.nodes[3].name, 'notDecoderAnymore');
  });

  test('TREE_UPDATE_CONTENTS adds a new current object', (assert) => {
    const action = {
      type: ACTION_TYPES.TREE_UPDATE_CONTENTS,
      payload: updateResponseAdd
    };
    const state = new ReduxDataHelper()
      .connected()
      .treePathContentsStandard()
      .build();

    assert.strictEqual(state.treePathContents.nodes.length, 4);

    const result = reducer(state, action);

    assert.strictEqual(result.treePath, '/', 'treePath is /');

    assert.strictEqual(result.treePathContents.nodes[4].name, 'newBranchOffRoot');
    assert.strictEqual(result.treePathContents.nodes[4].path, '/newBranchOffRoot');
    assert.strictEqual(result.treePathContents.nodes.length, 5);
  });

  test('TREE_UPDATE_CONTENTS deletes a current object', (assert) => {
    const action = {
      type: ACTION_TYPES.TREE_UPDATE_CONTENTS,
      payload: updateResponseDelete
    };
    const state = new ReduxDataHelper()
      .connected()
      .treePathContentsStandard()
      .build();

    // The decoder node
    assert.ok(state.treePathContents.nodes[3]);

    const result = reducer(state, action);

    assert.strictEqual(result.treePath, '/', 'treePath is /');

    assert.strictEqual(result.treePathContents.nodes.length, 3);
    assert.notStrictEqual(result.treePathContents.nodes[0].name, 'decoder');
    assert.notStrictEqual(result.treePathContents.nodes[1].name, 'decoder');
    assert.notStrictEqual(result.treePathContents.nodes[2].name, 'decoder');
  });

  test('TREE_GET_OPERATION_HELP updates the description on the operation', (assert) => {
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.TREE_GET_OPERATION_HELP,
      payload: {
        params: {
          info: 'Returns detailed information about the node\nsecurity.roles: everyone\n'
        }
      }
    });
    const state = new ReduxDataHelper()
      .connected()
      .treePathContentsStandard()
      .build();

    const result = reducer(state, action);

    assert.strictEqual(result.treePath, '/', 'treePath is /');

    // Index 2 is the info operation
    const { operations } = result.treePathContents;
    const [,, info] = operations;
    const { description } = info;
    assert.strictEqual(description, 'Returns detailed information about the node\nsecurity.roles: everyone\n');
  });

  test('TREE_GET_OPERATION_PARAM_HELP updates the description on params', (assert) => {
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.TREE_GET_OPERATION_PARAM_HELP,
      payload: paramHelpResponse
    });
    const state = new ReduxDataHelper()
      .connected()
      .treePathContentsStandard()
      .treeSelectedOperationIndex(3)
      .build();

    const result = reducer(state, action);

    assert.strictEqual(result.treePath, '/', 'treePath is /');

    // Index 3 is the help operation
    const { operations } = result.treePathContents;
    const [,,, help] = operations;
    const { params } = help;
    assert.strictEqual(params[0].description, 'The name of the message to retrieve detailed help about (aliases are \'m\' or \'message\')');
    assert.strictEqual(params[1].description, 'The specific help operation to perform (e.g., op=manual would return a man page on this node or the specified message)');
    assert.strictEqual(params[2].description, 'The format of the response, default returns in a human friendly format');
  });

  test('TREE_UPDATE_OPERATION_PARAMS updates the user-entered params in state', (assert) => {
    const action = {
      type: ACTION_TYPES.TREE_UPDATE_OPERATION_PARAMS,
      payload: {
        foo: 1,
        bar: 'baz'
      }
    };

    const state = new ReduxDataHelper()
      .connected()
      .treePathContentsStandard()
      .build();

    assert.deepEqual(state.treeOperationParams, {});

    const result = reducer(state, action);

    assert.strictEqual(result.treePath, '/', 'treePath is /');

    assert.deepEqual(result.treeOperationParams, {
      foo: 1,
      bar: 'baz'
    });
  });

  test('TREE_UPDATE_PARAM adds and removes a parameter', (assert) => {
    const addAction = {
      type: ACTION_TYPES.TREE_UPDATE_PARAM,
      payload: {
        name: 'custom1',
        displayName: 'custom1',
        type: 'text',
        optional: false,
        custom: true,
        method: 'add'
      }
    };

    const state = new ReduxDataHelper()
      .connected()
      .treePathContentsStandard()
      .treeSelectedOperationIndex(2)
      .build();

    assert.strictEqual(state.treePathContents.operations[2].params.length, 0);

    let result = reducer(state, addAction);

    assert.strictEqual(result.treePathContents.operations[2].params.length, 1);
    assert.deepEqual(result.treePathContents.operations[2].params[0], {
      name: 'custom1',
      displayName: 'custom1',
      type: 'text',
      optional: false,
      custom: true
    });

    const deleteAction = {
      type: ACTION_TYPES.TREE_UPDATE_PARAM,
      payload: {
        name: 'custom1',
        method: 'delete'
      }
    };

    result = reducer(state, deleteAction);

    assert.strictEqual(result.treePathContents.operations[2].params.length, 0);
  });

  test('TREE_SET_SELECTED_NODE sets the selected stat node', (assert) => {
    const action = {
      type: ACTION_TYPES.TREE_SET_SELECTED_NODE,
      payload: stat
    };

    const state = new ReduxDataHelper()
      .connected()
      .treePathContentsStandard()
      .build();

    assert.notOk(state.selectedNode);

    const result = reducer(state, action);

    assert.deepEqual(result.selectedNode, stat);
  });

  test('TREE_DESELECT_SELECTED_NODE deselects a selected stat', (assert) => {
    const action = {
      type: ACTION_TYPES.TREE_DESELECT_SELECTED_NODE
    };

    const state = new ReduxDataHelper()
      .connected()
      .treePathContentsStandard()
      .selectedNode(stat)
      .build();

    assert.deepEqual(state.selectedNode, stat);

    const result = reducer(state, action);

    assert.notOk(result.selectedNode);
  });

  test('TREE_GET_NODE_DESCRIPTION sets the description of a selected stat', (assert) => {
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.TREE_GET_NODE_DESCRIPTION,
      payload: {
        params: {
          description: 'Descriptive text'
        }
      }
    });

    const state = new ReduxDataHelper()
      .connected()
      .treePathContentsStandard()
      .selectedNode(stat)
      .build();

    assert.deepEqual(state.selectedNode, stat);
    assert.notOk(state.selectedNode.description);

    const result = reducer(state, action);

    assert.strictEqual(result.selectedNode.description, 'Descriptive text');
  });

  test('TREE_SET_CONFIG_VALUE handles a successful config set', (assert) => {
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.TREE_SET_CONFIG_VALUE,
      payload: {
        string: 'Success'
      }
    });

    const state = new ReduxDataHelper()
      .connected()
      .treePathContentsStandard()
      .selectedNode(stat)
      .build();

    assert.deepEqual(state.selectedNode, stat);
    assert.notOk(state.selectedNode.configSetResult);

    const result = reducer(state, action);

    assert.strictEqual(result.selectedNode.configSetResult, true);
  });

  test('TREE_SET_CONFIG_VALUE handles an unsuccessful config set', (assert) => {
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.TREE_SET_CONFIG_VALUE,
      payload: {
        error: {
          message: 'Error text',
          code: 0
        }
      }
    });

    const state = new ReduxDataHelper()
      .connected()
      .treePathContentsStandard()
      .selectedNode(stat)
      .build();

    assert.deepEqual(state.selectedNode, stat);
    assert.notOk(state.selectedNode.configSetResult);

    const result = reducer(state, action);

    assert.strictEqual(result.selectedNode.configSetResult, 'Error text');
  });
});
