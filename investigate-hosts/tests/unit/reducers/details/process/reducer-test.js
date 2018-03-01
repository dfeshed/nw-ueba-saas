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

module('Unit | Reducers | process');

const initialState = Immutable.from({
  processList: null,
  // In list view, process view can be sorted based on processName, pid. By default, we fetch based on processName in ascending order.
  sortField: 'name',
  isDescOrder: false,

  processTree: null,
  processDetails: null,
  processDetailsLoading: false,
  isProcessTreeLoading: false

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
