import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-hosts/reducers/details/process/reducer';
import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../../helpers/make-pack-action';
import {
  processListData,
  processTreeData,
  processDetailsData
} from '../../../state/state';

module('Unit | Reducers | process', function() {
  const initialState = Immutable.from({
    processList: null,
    // In list view, process view can be sorted based on processName, pid. By default, we fetch based on processName in ascending order.
    sortField: 'fileProperties.score',
    isDescOrder: true,

    processTree: null,
    processDetails: null,

    processDetailsLoading: false,
    isProcessTreeLoading: false,
    selectedProcessList: [],
    selectedRowIndex: null,
    selectedDllItem: null,
    selectedDllRowIndex: -1
  });

  test('should return the initial state', function(assert) {
    const result = reducer(undefined, {});
    assert.deepEqual(result, initialState);
  });

  test('The RESET_HOST_DETAILS will reset the state', function(assert) {
    const previous = Immutable.from({
      processList: [{ name: 'test' }],
      sortField: 'pid',
      isDescOrder: true,
      processTree: [ { parent: { child: [] } }],
      processDetails: {}
    });
    const result = reducer(previous, { type: ACTION_TYPES.RESET_HOST_DETAILS });

    assert.deepEqual(result, initialState);
  });

  test('The SET_SORT_BY will reset the state', function(assert) {
    const previous = Immutable.from({
      sortField: 'pid',
      isDescOrder: true
    });
    const result = reducer(
      previous,
      { type: ACTION_TYPES.SET_SORT_BY, payload: { isDescOrder: true, sortField: 'pid' } }
    );
    assert.deepEqual(result, { sortField: 'pid', isDescOrder: true });
  });

  test('The RESET_PROCESS_LIST will reset process list state', function(assert) {
    const previous = Immutable.from({
      processList: [{ name: 'test' }]
    });
    const result = reducer(previous, { type: ACTION_TYPES.RESET_PROCESS_LIST });
    assert.equal(result.processList, null);
  });

  test('The GET_PROCESS_LIST sets server response to the list', function(assert) {
    const previous = Immutable.from({
      processList: [],
      isProcessTreeLoading: null
    });

    const startAction = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.GET_PROCESS_LIST });
    const startEndState = reducer(previous, startAction);
    assert.deepEqual(startEndState.isProcessTreeLoading, true);

    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.GET_PROCESS_LIST,
      payload: { data: processListData }
    });
    const endState = reducer(previous, action);
    assert.equal(endState.processList.length, 4);
  });

  test('The GET_PROCESS_TREE sets server response to the list', function(assert) {
    const previous = Immutable.from({
      processTree: [],
      isProcessTreeLoading: false
    });

    const startAction = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.GET_PROCESS_TREE });
    const startEndState = reducer(previous, startAction);
    assert.deepEqual(startEndState.isProcessTreeLoading, true);

    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.GET_PROCESS_TREE,
      payload: { data: processTreeData }
    });
    const endState = reducer(previous, action);
    assert.equal(endState.processTree.length, 1);
  });

  test('The GET_PROCESS sets server response to the list', function(assert) {
    const previous = Immutable.from({
      processDetails: null,
      processDetailsLoading: false
    });

    const startAction = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.GET_PROCESS });
    const startEndState = reducer(previous, startAction);
    assert.deepEqual(startEndState.processDetailsLoading, true);

    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.GET_PROCESS,
      payload: { data: processDetailsData }
    });
    const endState = reducer(previous, action);
    assert.equal(endState.processDetails.path, '/usr/lib/systemd');
  });

  test('The GET_PROCESS_FILE_CONTEXT sets server response to the list', function(assert) {
    const previous = Immutable.from({
      dllList: []
    });

    const startAction = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.GET_PROCESS_FILE_CONTEXT });
    const startEndState = reducer(previous, startAction);
    assert.deepEqual(startEndState.dllList, null);

    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.GET_PROCESS_FILE_CONTEXT,
      payload: { data: new Array(10) }
    });
    const endState = reducer(previous, action);
    assert.equal(endState.dllList.length, 10);
  });

  test('The SET_ROW_INDEX will set the selected row index', function(assert) {
    const previous = Immutable.from({
      selectedRowIndex: null
    });
    const result = reducer(previous, { type: ACTION_TYPES.SET_ROW_INDEX, payload: 50 });
    assert.equal(result.selectedRowIndex, 50);
  });

  test('The CHANGE_DETAIL_TAB reset the row index', function(assert) {
    const previous = Immutable.from({
      selectedRowIndex: 50
    });
    const result = reducer(previous, { type: ACTION_TYPES.CHANGE_DETAIL_TAB });
    assert.equal(result.selectedRowIndex, null);
  });
  test('The SET_SELECTED_PROCESS sets the selectedProcesList', function(assert) {
    const previous = Immutable.from({
      selectedProcessList: []
    });
    const process = {
      name: 'p1',
      pid: 1,
      parentPid: 0,
      hasChild: true,
      path: 'C:/',
      vpid: 154354,
      fileProperties: {
        checksumSha256: '46965656dffsdf664',
        checksumMd5: '89676969',
        checksumSha1: '9798796986',
        downloadInfo: { status: true },
        signature: 'signed',
        size: 1234,
        firstFileName: 'p1',
        fileStatus: 'Neutral',
        score: 0
      }
    };
    const result = reducer(
      previous,
      { type: ACTION_TYPES.SET_SELECTED_PROCESS, payload: process }
    );
    const expectedResult = [
      {
        checksumSha256: '46965656dffsdf664',
        name: 'p1',
        fileName: 'p1',
        pid: 1,
        parentPid: 0,
        hasChild: true,
        vpid: 154354,
        checksumMd5: '89676969',
        checksumSha1: '9798796986',
        downloadInfo: { status: true },
        id: 1,
        path: 'C:/',
        signature: 'signed',
        size: 1234,
        fileStatus: 'Neutral',
        score: 0
      }
    ];
    assert.deepEqual(result.selectedProcessList, expectedResult);
  });

  test('The SELECT_ALL_PROCESS sets the selectedProcesList', function(assert) {
    const previous = Immutable.from({
      selectedProcessList: [],
      processList: [
        {
          name: 'p1',
          fileProperties: {
            signature: 'signed',
            size: 1234,
            fileStatus: 'Neutral',
            score: 0,
            checksumSha256: '46965656dffsdf664',
            checksumMd5: '89676969',
            checksumSha1: '9798796986',
            downloadInfo: { status: true }
          },
          pid: 1,
          parentPid: 0,
          hasChild: true,
          vpid: 154354,
          id: 1,
          path: 'C:/'
        },
        {
          name: 'p2',
          fileProperties: {
            signature: 'unsigned',
            size: 9234,
            fileStatus: 'Neutral',
            score: 0,
            checksumSha256: '89484fgfdgr546488',
            checksumMd5: '6789676969',
            checksumSha1: '789798796986',
            downloadInfo: { status: true }
          },
          pid: 2,
          parentPid: 1,
          hasChild: false,
          vpid: 98765,
          id: 2,
          path: 'D:/'
        }
      ]
    });
    const expectedResult = [
      {
        checksumSha256: '46965656dffsdf664',
        name: 'p1',
        fileName: 'p1',
        pid: 1,
        parentPid: 0,
        hasChild: true,
        vpid: 154354,
        checksumMd5: '89676969',
        checksumSha1: '9798796986',
        downloadInfo: { status: true },
        id: 1,
        path: 'C:/',
        signature: 'signed',
        size: 1234,
        fileStatus: 'Neutral',
        score: 0
      },

      {
        checksumSha256: '89484fgfdgr546488',
        name: 'p2',
        fileName: 'p2',
        pid: 2,
        parentPid: 1,
        hasChild: false,
        vpid: 98765,
        checksumMd5: '6789676969',
        checksumSha1: '789798796986',
        downloadInfo: { status: true },
        id: 2,
        path: 'D:/',
        signature: 'unsigned',
        size: 9234,
        fileStatus: 'Neutral',
        score: 0
      }];
    const result = reducer(
      previous,
      { type: ACTION_TYPES.SELECT_ALL_PROCESS }
    );
    assert.deepEqual(result.selectedProcessList, expectedResult);
  });

  test('The DESELECT_ALL_PROCESS sets the selectedProcesList', function(assert) {
    const previous = Immutable.from({
      selectedProcessList: [{ checksumSha256: '46965656dffsdf664', name: 'p1', pid: 1, parentPid: 0, hasChild: true, vpid: 154354 }]
    });
    const result = reducer(
      previous,
      { type: ACTION_TYPES.DESELECT_ALL_PROCESS }
    );
    assert.deepEqual(result.selectedProcessList, []);
  });
  test('The TOGGLE_PROCESS_DETAILS_ROW sets the selectedRow', function(assert) {
    const previous = Immutable.from({
      selectedDllItem: null
    });
    const result = reducer(
      previous,
      { type: ACTION_TYPES.TOGGLE_PROCESS_DETAILS_ROW, payload: { id: 'test' } }
    );
    assert.deepEqual(result.selectedDllItem.id, 'test');
  });

  test('The TOGGLE_PROCESS_VIEW sets the selectedRow', function(assert) {
    const previous = Immutable.from({
      selectedRowIndex: 1,
      selectedProcessList: [{}]
    });
    const result = reducer(
      previous,
      { type: ACTION_TYPES.TOGGLE_PROCESS_VIEW }
    );
    assert.deepEqual(result.selectedRowIndex, -1);
    assert.deepEqual(result.selectedProcessList.length, 0);
  });

  test('The SET_PROCESS_DLL_ROW_ID sets the selectedRow', function(assert) {
    const previous = Immutable.from({
      selectedDllItem: null
    });
    const result = reducer(
      previous,
      { type: ACTION_TYPES.SET_PROCESS_DLL_ROW_ID, payload: 1 }
    );
    assert.deepEqual(result.selectedDllRowIndex, 1);
  });

  test('The SAVE_FILE_CONTEXT_FILE_STATUS sets the processList in state, with the updated fileStatus', function(assert) {
    const previous = Immutable.from({
      processList: [
        {
          checksumSha256: '7b7f8973702f72655ece594ec9179a2228c62912a1f37d1c77d243476088271d',
          createTime: '2018-11-08T10:03:06.835+0000',
          launchArguments: '',
          name: 'winlogon.exe',
          parentPid: 456,
          path: 'C:',
          pid: 516,
          reputationStatus: 'Known Good',
          score: 0,
          serviceId: 'fef38f60-cf50-4d52-a4a9-7727c48f1a4b',
          serviceName: 'EPS1-server - Endpoint Server',
          fileProperties: {
            fileStatus: 'Blacklist'
          }
        },
        {
          checksumSha256: '9179a2228c62912a1f37d1c77d243476088271d7b7f8973702f72655ece594ec',
          createTime: '2018-11-08T10:03:06.835+0000',
          launchArguments: '',
          name: 'winlogon.exe',
          parentPid: 456,
          path: 'C:',
          pid: 516,
          reputationStatus: 'Known Good',
          score: 0,
          serviceId: 'fef38f60-cf50-4d52-a4a9-7727c48f1a4b',
          serviceName: 'EPS1-server - Endpoint Server',
          fileProperties: {
            fileStatus: 'Blacklist'
          }
        }
      ]
    });

    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.SAVE_FILE_CONTEXT_FILE_STATUS,
      payload: {
        request: {
          data: {
            checksums: ['7b7f8973702f72655ece594ec9179a2228c62912a1f37d1c77d243476088271d'],
            fileStatus: 'Graylist'
          }
        }
      }
    });
    const endState = reducer(previous, action);
    assert.deepEqual(endState.processList[0].fileProperties.fileStatus, 'Graylist', 'File status in processlist of corresponding file is updated.');
    assert.deepEqual(endState.processList[1].fileProperties.fileStatus, 'Blacklist', 'File status for the file, whose status hasnot been changes, remains unaffected in the state.');
  });
});
