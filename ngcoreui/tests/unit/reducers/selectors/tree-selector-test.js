import { test, module } from 'qunit';
import { setupTest } from 'ember-qunit';
import {
  currentDirectoryContents,
  isNotRoot,
  pathParent,
  pathToUrlSegment,
  pathParentToUrlSegment,
  operationNames,
  filteredOperationNames,
  selectedOperation,
  selectedOperationHelp,
  selectedOperationRoles,
  selectedOperationHasPermission,
  description,
  liveSelectedNode,
  configSetResult,
  selectedIsConfigNode,
  selectedIsStatNode,
  isDevelopmentBuild,
  isDecoder
} from 'ngcoreui/reducers/selectors';
import ReduxDataHelper from '../../../helpers/redux-data-helper';

module('Unit | Selectors | Tree', (hooks) => {

  setupTest(hooks);

  test('currentDirectoryContents gives the contents of the appropriate directory', (assert) => {
    const state = new ReduxDataHelper().currentDirectoryContents().build();
    assert.deepEqual(currentDirectoryContents(state), state.treePathContents.nodes,
      'should give the root directory when set at root');
  });

  test('isNotRoot correctly identifies when the tree is currently viewing root', (assert) => {
    let state = new ReduxDataHelper().isNotRoot().build();
    assert.strictEqual(isNotRoot(state), false, 'should report false when set at root');
    state = new ReduxDataHelper().isNotRoot().treePath('/sys').build();
    assert.strictEqual(isNotRoot(state), true, 'should report true when not at root');
    state = new ReduxDataHelper().isNotRoot().treePath('/sys/stats').build();
    assert.strictEqual(isNotRoot(state), true, 'should report true when not at root');
  });

  test('pathParent should correctly identify the parent of the current path', (assert) => {
    // Paths returned by the service will never have trailing slashes, so we
    // don't need to test for that
    let state = new ReduxDataHelper().pathParent().treePath('/sys').build();
    assert.strictEqual(pathParent(state), '/');
    state = new ReduxDataHelper().pathParent().treePath('/sys/stats').build();
    assert.strictEqual(pathParent(state), '/sys');
    state = new ReduxDataHelper().pathParent().treePath('/abc/def/xyz').build();
    assert.strictEqual(pathParent(state), '/abc/def');
  });

  test('pathToUrlSegment should map a path string to a url segment', (assert) => {
    let state = new ReduxDataHelper().pathToUrlSegment().treePath('/').build();
    assert.strictEqual(pathToUrlSegment(state), 'tree');
    state = new ReduxDataHelper().pathToUrlSegment().treePath('/sys').build();
    assert.strictEqual(pathToUrlSegment(state), 'sys');
    state = new ReduxDataHelper().pathToUrlSegment().treePath('/sys/stats').build();
    assert.strictEqual(pathToUrlSegment(state), 'sys/stats');
    state = new ReduxDataHelper().pathToUrlSegment().treePath('/abc/def/xyz').build();
    assert.strictEqual(pathToUrlSegment(state), 'abc/def/xyz');
  });

  test('pathParentToUrlSegment should map a path string to a url segment for its parent', (assert) => {
    let state = new ReduxDataHelper().pathParentToUrlSegment().treePath('/').build();
    assert.strictEqual(pathParentToUrlSegment(state), null);
    state = new ReduxDataHelper().pathParentToUrlSegment().treePath('/sys').build();
    assert.strictEqual(pathParentToUrlSegment(state), 'tree');
    state = new ReduxDataHelper().pathParentToUrlSegment().treePath('/sys/stats').build();
    assert.strictEqual(pathParentToUrlSegment(state), 'sys');
    state = new ReduxDataHelper().pathParentToUrlSegment().treePath('/abc/def/xyz').build();
    assert.strictEqual(pathParentToUrlSegment(state), 'abc/def');
  });

  test('operationNames should find just the names of the operations for the current path', (assert) => {
    const state = new ReduxDataHelper().operationNames().build();
    const rootOpNames = ['ls', 'mon', 'info', 'help', 'count', 'stopMon'];
    assert.deepEqual(operationNames(state), rootOpNames);
  });

  test('selectedOperation should return the full operation object currently selected', (assert) => {
    const state = new ReduxDataHelper()
      .selectedOperation()
      .treeSelectedOperationIndex(2)
      .build();
    const op = {
      name: 'info',
      params: []
    };
    assert.deepEqual(selectedOperation(state), op);
  });

  test('selectedOperation should return null with no operation selected', (assert) => {
    const state = new ReduxDataHelper()
      .selectedOperation()
      .build();
    assert.strictEqual(selectedOperation(state), null);
  });

  test('filteredOperationNames should return the list of operations without filtered names', (assert) => {
    const state = new ReduxDataHelper()
      .filteredOperationNames()
      .build();
    const expectedOpNames = [];
    assert.deepEqual(filteredOperationNames(state), expectedOpNames);
  });

  test('selectedOperationHelp should return the help text of the selected operation', (assert) => {
    const state = new ReduxDataHelper()
      .selectedOperationHelp()
      .treeSelectedOperationIndex(0)
      .build();
    assert.strictEqual(selectedOperationHelp(state), 'Test operation help text');
  });

  test('selectedOperationRoles should return the roles required to execute the selected operation', (assert) => {
    const state = new ReduxDataHelper()
      .selectedOperationRoles()
      .treeSelectedOperationIndex(0)
      .build();
    assert.deepEqual(selectedOperationRoles(state), ['sys.manage']);
  });

  test('selectedOperationRoles should return the roles required to execute the selected operation ("everyone" role)', (assert) => {
    const state = new ReduxDataHelper()
      .selectedOperationRoles()
      .treeSelectedOperationIndex(1)
      .build();
    assert.deepEqual(selectedOperationRoles(state), []);
    assert.strictEqual(selectedOperation(state).description, 'Test operation help text\nsecurity.roles: everyone\n');
  });

  test('selectedOperationRoles should return the roles required to execute the selected operation (more than one role)', (assert) => {
    const state = new ReduxDataHelper()
      .selectedOperationRoles()
      .treeSelectedOperationIndex(3)
      .build();
    assert.deepEqual(selectedOperationRoles(state), ['sys.manage', 'logs.manage']);
  });

  test('selectedOperationHasPermission should return false when a user does not have permission to execute the selected operation', (assert) => {
    const state = new ReduxDataHelper()
      .selectedOperationHasPermission()
      .treeSelectedOperationIndex(0)
      .availablePermissions(['sdk.meta', 'sdk.content', 'sdk.packets'])
      .build();
    assert.strictEqual(selectedOperationHasPermission(state), false);
  });

  test('selectedOperationHasPermission should return true when a user does have permission to execute the selected operation', (assert) => {
    const state = new ReduxDataHelper()
      .selectedOperationHasPermission()
      .treeSelectedOperationIndex(0)
      .availablePermissions(['sdk.meta', 'sdk.content', 'sys.manage', 'sdk.packets'])
      .build();
    assert.strictEqual(selectedOperationHasPermission(state), true);
  });

  test('description should return the description of the current path', (assert) => {
    const state = new ReduxDataHelper()
      .description()
      .build();
    assert.strictEqual(description(state), 'A container node for other node types');
  });

  test('liveSelectedNode returns the live updated version of the selected node', (assert) => {
    const state = new ReduxDataHelper()
      .liveSelectedNode()
      .selectedNode({
        path: '/decoder',
        handle: 267,
        parentHandle: 1,
        nodeType: 299067162755072,
        display: 'Not live info'
      })
      .build();
      // The non-live version has a different string for display
    assert.strictEqual(liveSelectedNode(state).display, '');
  });

  test('configSetResult returns the result of setting the config value', (assert) => {
    const state1 = new ReduxDataHelper()
      .configSetResult()
      .selectedNodeConfigSetResult(true)
      .build();

    assert.strictEqual(configSetResult(state1), true);

    const state2 = new ReduxDataHelper()
      .configSetResult()
      .selectedNodeConfigSetResult('error')
      .build();

    assert.strictEqual(configSetResult(state2), 'error');

    const state3 = new ReduxDataHelper()
      .configSetResult()
      .build();

    assert.strictEqual(configSetResult(state3), null);
  });

  test('selectedIsConfigNode detects config nodes correctly', (assert) => {
    const state1 = new ReduxDataHelper()
      .selectedIsConfigNode()
      .selectedNode({
        nodeType: 299067162755072 // Not config
      })
      .build();

    assert.strictEqual(selectedIsConfigNode(state1), false);

    const state2 = new ReduxDataHelper()
      .selectedIsConfigNode()
      .selectedNode({
        nodeType: 914793674309632 // Is config
      })
      .build();

    assert.strictEqual(selectedIsConfigNode(state2), true);
  });

  test('selectedIsStatNode detects config nodes correctly', (assert) => {
    const state1 = new ReduxDataHelper()
      .selectedIsStatNode()
      .selectedNode({
        nodeType: 299067162755072 // Not stat
      })
      .build();

    assert.strictEqual(selectedIsStatNode(state1), false);

    const state2 = new ReduxDataHelper()
      .selectedIsStatNode()
      .selectedNode({
        nodeType: 598134325510144 // Is stat
      })
      .build();

    assert.strictEqual(selectedIsStatNode(state2), true);
  });

  test('isDevelopmentBuild identifies a dev build from the release', (assert) => {
    const state1 = new ReduxDataHelper()
      .isDevelopmentBuild()
      .release('0.0.0')
      .build();

    assert.strictEqual(isDevelopmentBuild(state1), true);

    const state2 = new ReduxDataHelper()
      .isDevelopmentBuild()
      .release('3.4.5678ffff')
      .build();

    assert.strictEqual(isDevelopmentBuild(state2), false);
  });

  test('isDecoder identifies a decoder from the module', (assert) => {
    const state1 = new ReduxDataHelper()
      .isDecoder()
      .module('concentrator')
      .build();

    assert.strictEqual(isDecoder(state1), false);

    const state2 = new ReduxDataHelper()
      .isDecoder()
      .module('decoder')
      .build();

    assert.strictEqual(isDecoder(state2), true);
  });

});
