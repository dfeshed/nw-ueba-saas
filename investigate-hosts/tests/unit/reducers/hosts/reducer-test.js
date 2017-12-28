import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-hosts/reducers/hosts/reducer';
import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../helpers/make-pack-action';

module('Unit | Reducers | hosts');

const HOST_LIST = [
  {
    id: 1,
    showIcon: false,
    machine: {
      agentVersion: '1.1.0.0'
    }
  },
  {
    id: 2,
    showIcon: false,
    machine: {
      agentVersion: '1.1.0.0'
    }
  },
  {
    id: 3,
    showIcon: false,
    machine: {
      agentVersion: '1.1.0.0'
    }
  }
];
test('should return the initial state', function(assert) {
  const result = reducer(undefined, {});
  assert.deepEqual(result, {
    hostList: null,
    hostColumnSort: [],
    selectedHostList: [],
    hostFetchStatus: 'wait',
    loadMoreHostStatus: 'stopped',
    hostExportStatus: 'completed',
    hostExportLinkId: null,
    totalItems: null,
    pageNumber: null,
    listOfServices: null,
    hasNext: false
  });
});

test('The DESELECT_ALL_HOSTS reset the all host selections', function(assert) {
  const previous = Immutable.from({
    selectedHostList: [1, 2, 4]
  });
  const result = reducer(previous, { type: ACTION_TYPES.DESELECT_ALL_HOSTS });

  assert.deepEqual(result.selectedHostList.length, 0, 'Expecting to clear the selectedHostList');
});

test('The SELECT_ALL_HOSTS selects all the host', function(assert) {
  const previous = Immutable.from({
    hostList: HOST_LIST,
    selectedHostList: []
  });
  const result = reducer(previous, { type: ACTION_TYPES.SELECT_ALL_HOSTS });

  assert.deepEqual(result.selectedHostList.length, 3, 'Expecting to select all the host');
});

test('The TOGGLE_ICON_VISIBILITY action will toggles the icon visibility state', function(assert) {
  const previous = Immutable.from({
    hostList: HOST_LIST,
    selectedHostList: []
  });
  const result = reducer(previous, { type: ACTION_TYPES.TOGGLE_ICON_VISIBILITY, payload: { id: 1, flag: true } });
  assert.equal(result.hostList[0].showIcon, true);
  assert.equal(result.hostList[2].showIcon, false);
});

test('The TOGGLE_MACHINE_SELECTED action will toggles the machine selections', function(assert) {
  const previous = Immutable.from({
    selectedHostList: [{
      id: 1,
      version: '4.4.0.0'
    },
    {
      id: 2,
      version: '4.5.0.0'
    },
    {
      id: 3,
      version: '4.4.0.0'
    }]
  });
  const addResult = reducer(previous, { type: ACTION_TYPES.TOGGLE_MACHINE_SELECTED, payload: { id: 5, version: '1.1.0.0' } });
  assert.equal(addResult.selectedHostList.length, 4, 'expected to add the selected id in the list');

  const removeResult = reducer(previous, { type: ACTION_TYPES.TOGGLE_MACHINE_SELECTED, payload: { id: 1, version: '4.5.0.0' } });
  assert.equal(removeResult.selectedHostList.length, 2, 'expected to remove the id from the list');
});

test('The FETCH_AGENT_STATUS action will update the agent status', function(assert) {
  const previous = Immutable.from({
    hostList: HOST_LIST
  });
  const result = reducer(previous, { type: ACTION_TYPES.FETCH_AGENT_STATUS, payload: { 2: { agentId: 2, scanStatus: 'idle' } } });
  assert.equal(result.hostList[1].agentStatus.scanStatus, 'idle');
});

test('The USER_LEFT_HOST_LIST_PAGE action reset the export link', function(assert) {
  const previous = Immutable.from({
    hostExportLinkId: '123'
  });
  const result = reducer(previous, { type: ACTION_TYPES.USER_LEFT_HOST_LIST_PAGE });
  assert.equal(result.hostExportLinkId, null);
});

test('Then SET_HOST_COLUMN_SORT will set the selected sort to state', function(assert) {
  const previous = Immutable.from({
    hostColumnSort: ['name']
  });
  const result = reducer(previous, { type: ACTION_TYPES.SET_HOST_COLUMN_SORT, payload: 'id' });
  assert.equal(result.hostColumnSort[0], 'id');
});

test('The SET_SELECTED_HOST action will sets the selected host to state', function(assert) {
  const previous = Immutable.from({
    selectedHostList: [{
      id: 1,
      version: '4.4.0.0'
    }]
  });
  const result = reducer(previous, { type: ACTION_TYPES.SET_SELECTED_HOST, payload: { id: 5, version: '4.5.0.0' } });
  assert.equal(result.selectedHostList[0].id, 5);
});

test('The FETCH_DOWNLOAD_JOB_ID action will sets the download id to state', function(assert) {
  const previous = Immutable.from({
    hostExportLinkId: null,
    hostExportStatus: 'completed'
  });

  const startAction = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_DOWNLOAD_JOB_ID });
  const endState = reducer(previous, startAction);

  assert.equal(endState.hostExportStatus, 'streaming');

  const successAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_DOWNLOAD_JOB_ID,
    payload: { data: { id: 111 } }
  });
  const newEndState = reducer(previous, successAction);
  assert.equal(newEndState.hostExportStatus, 'completed');
  assert.equal(newEndState.hostExportLinkId, 111);
});

test('The FETCH_ALL_SCHEMAS action start will reset the host list', function(assert) {
  const previous = Immutable.from({
    hostList: HOST_LIST
  });

  assert.equal(previous.hostList.length, 3);

  const startAction = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_ALL_SCHEMAS });
  const endState = reducer(previous, startAction);

  assert.equal(endState.hostList.length, 0);
});

test('The FETCH_ALL_MACHINES will sets machine api response to state', function(assert) {
  const previous = Immutable.from({
    hostList: [ { id: 1 }],
    selectedHostList: [1],
    hostFetchStatus: 'Completed'
  });
  const startAction = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_ALL_MACHINES });
  const endState = reducer(previous, startAction);

  assert.equal(endState.hostFetchStatus, 'wait');
  assert.equal(endState.hostList.length, 0);

  const successAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_ALL_MACHINES,
    payload: { data: { items: HOST_LIST } }
  });
  const newEndState = reducer(previous, successAction);

  assert.equal(newEndState.hostFetchStatus, 'completed');
  assert.equal(newEndState.hostList.length, 3);
});

test('The FETCH_NEXT_MACHINES will set the paged response to state', function(assert) {
  const previous = Immutable.from({
    hostList: HOST_LIST,
    loadMoreHostStatus: null
  });
  const startAction = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_NEXT_MACHINES });
  const endState = reducer(previous, startAction);

  assert.equal(endState.loadMoreHostStatus, 'streaming');

  const successAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_NEXT_MACHINES,
    payload: { data: { hasNext: false, totalItems: 1, items: [ { id: 11 }] } }
  });
  const newEndState = reducer(previous, successAction);

  assert.equal(newEndState.loadMoreHostStatus, 'completed');
  assert.equal(newEndState.hostList.length, 4);
});

test('The RESET_HOST_DOWNLOAD_LINK action reset the HOST download link', function(assert) {
  const previous = Immutable.from({
    hostExportLinkId: '1awsseeeq'
  });
  const result = reducer(previous, { type: ACTION_TYPES.RESET_HOST_DOWNLOAD_LINK });
  assert.equal(result.hostExportLinkId, null);
});

test('The GET_LIST_OF_SERVICES will set all the service to state', function(assert) {
  const previous = Immutable.from({
    listOfServices: null
  });
  const successAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.GET_LIST_OF_SERVICES,
    payload: {
      data: [
        { 'id': 'e90bd2a2-a768-4cb9-a19d-37cd9f47fdcc', 'displayName': 'loki-concentrator', 'name': 'CONCENTRATOR' },
        { 'id': 'e90bd2a2-a768-4cb9-a19d-37cd9f47fdcc', 'displayName': 'loki-broker', 'name': 'BROKER' }
      ]
    }
  });
  const newEndState = reducer(previous, successAction);
  assert.equal(newEndState.listOfServices.length, 2);
});
