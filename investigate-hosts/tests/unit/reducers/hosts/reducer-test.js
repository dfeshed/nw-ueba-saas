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
    },
    groupPolicy: {
      managed: true
    }
  },
  {
    id: 2,
    showIcon: false,
    machine: {
      agentVersion: '1.1.0.0'
    },
    groupPolicy: {
      managed: false
    }
  },
  {
    id: 3,
    showIcon: false,
    machine: {
      agentVersion: '1.1.0.0'
    },
    groupPolicy: {
      managed: true
    }
  }
];
test('should return the initial state', function(assert) {
  const result = reducer(undefined, {});
  assert.deepEqual(result, {
    hostList: null,
    hostColumnSort: [{ key: 'score', descending: true }],
    selectedHostList: [],
    focusedHost: null,
    focusedHostIndex: null,
    hostFetchStatus: 'wait',
    loadMoreHostStatus: 'stopped',
    hostExportStatus: 'completed',
    hostExportLinkId: null,
    totalItems: 0,
    pageNumber: null,
    listOfServices: null,
    activeHostListPropertyTab: 'HOST_DETAILS',
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
      version: '4.4.0.0',
      managed: true
    },
    {
      id: 2,
      version: '4.5.0.0',
      managed: false
    },
    {
      id: 3,
      version: '4.4.0.0',
      managed: false
    }]
  });
  const addResult = reducer(previous, { type: ACTION_TYPES.TOGGLE_MACHINE_SELECTED, payload: { id: 5, version: '1.1.0.0', managed: true } });
  assert.equal(addResult.selectedHostList.length, 4, 'expected to add the selected id in the list');

  const removeResult = reducer(previous, { type: ACTION_TYPES.TOGGLE_MACHINE_SELECTED, payload: { id: 1, version: '4.4.0.0', managed: true } });
  assert.equal(removeResult.selectedHostList.length, 2, 'expected to remove the id from the list');
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
      version: '4.4.0.0',
      managed: false
    }]
  });
  const result = reducer(previous, { type: ACTION_TYPES.SET_SELECTED_HOST, payload: { id: 5, version: '4.5.0.0', managed: true } });
  assert.deepEqual(result.selectedHostList[0], { id: 5, version: '4.5.0.0', managed: true });
});

test('The SET_FOCUSED_HOST action will sets the selected row data to state', function(assert) {
  const previous = Immutable.from({
    focusedHost: {
      id: 1,
      serviceId: 'serviceId',
      score: 40
    }
  });
  const result = reducer(previous, { type: ACTION_TYPES.SET_FOCUSED_HOST, payload: { id: 5, serviceId: 'serviceIdNew', score: 100 } });
  assert.deepEqual(result.focusedHost, { id: 5, serviceId: 'serviceIdNew', score: 100 });
});

test('The SET_FOCUSED_HOST_INDEX action will sets the selected row data to state', function(assert) {
  const previous = Immutable.from({
    focusedHostIndex: null
  });
  const result = reducer(previous, { type: ACTION_TYPES.SET_FOCUSED_HOST_INDEX, payload: 2 });
  assert.deepEqual(result.focusedHostIndex, 2);
});

test('The CHANGE_HOST_LIST_PROPERTY_TAB action sets the newly selected tab to state', function(assert) {
  const previous = Immutable.from({
    activeHostListPropertyTab: 'HOST_DETAILS'
  });

  const result = reducer(previous, { type: ACTION_TYPES.CHANGE_HOST_LIST_PROPERTY_TAB, payload: { tabName: 'RISK' } });
  assert.deepEqual(result.activeHostListPropertyTab, 'RISK');
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
    hostList: HOST_LIST,
    hostFetchStatus: 'completed',
    totalItems: 3

  });

  assert.equal(previous.hostList.length, 3);
  assert.equal(previous.hostFetchStatus, 'completed');
  assert.equal(previous.totalItems, 3);

  const startAction = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_ALL_SCHEMAS });
  const endState = reducer(previous, startAction);

  assert.equal(endState.hostList.length, 0);
  assert.equal(endState.hostFetchStatus, 'wait');
  assert.equal(endState.totalItems, 0);
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

test('The FETCH_NEXT_MACHINES sets load more state properly when totalItems = 5', function(assert) {
  const previous = Immutable.from({
    hostList: HOST_LIST,
    loadMoreHostStatus: null
  });
  const startAction = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_NEXT_MACHINES });
  const endState = reducer(previous, startAction);

  assert.equal(endState.loadMoreHostStatus, 'streaming');

  const successAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_NEXT_MACHINES,
    payload: { data: { hasNext: true, totalItems: 5, items: [ { id: 11 }] } }
  });
  const newEndState = reducer(previous, successAction);
  assert.equal(newEndState.loadMoreHostStatus, 'stopped', 'load more status is stopped when hasNext is true');

  const loadMoreAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_NEXT_MACHINES,
    payload: { data: { hasNext: false, totalItems: 5, items: [ { id: 12 }, { id: 13 }] } }
  });
  const newEndState1 = reducer(previous, loadMoreAction);
  assert.equal(newEndState1.loadMoreHostStatus, 'completed', 'load more status is completed when hasNext is false');
});

test('The FETCH_NEXT_MACHINES sets load more state properly when totalItems > 1000', function(assert) {
  const previous = Immutable.from({
    hostList: HOST_LIST,
    loadMoreHostStatus: null
  });
  const startAction = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_NEXT_MACHINES });
  const endState = reducer(previous, startAction);

  assert.equal(endState.loadMoreHostStatus, 'streaming');

  const successAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_NEXT_MACHINES,
    payload: { data: { hasNext: true, totalItems: 1500, items: [ { id: 11 }] } }
  });
  const newEndState = reducer(previous, successAction);
  assert.equal(newEndState.loadMoreHostStatus, 'stopped', 'load more status is stopped when hasNext is true');

  const loadMoreAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_NEXT_MACHINES,
    payload: { data: { hasNext: false, totalItems: 1500, items: [ { id: 12 }, { id: 13 }] } }
  });
  const newEndState1 = reducer(previous, loadMoreAction);
  assert.equal(newEndState1.loadMoreHostStatus, 'completed', 'load more status is stopped when hasNext is false');
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

test('FETCH_AGENT_STATUS', function(assert) {
  const payload = { data: [{ agentId: '123', scanStatus: 'idle' }, { agentId: '134', scanStatus: 'scanPending' }] };
  const previous1 = Immutable.from({ hostList: [] });
  const result1 = reducer(previous1, { type: ACTION_TYPES.FETCH_AGENT_STATUS, payload });

  assert.equal(result1.hostList.length, 0, 'hostList is empty');

  const previous2 = Immutable.from({ hostList: [{ id: '123', agentStatus: { agentId: '123', scanStatus: 'scanning' } }] });
  const result2 = reducer(previous2, { type: ACTION_TYPES.FETCH_AGENT_STATUS, payload });

  assert.equal(result2.hostList[0].agentStatus.scanStatus, 'idle');

  const previous3 = Immutable.from({ hostList: [{ id: '123', agentStatus: { agentId: '123', scanStatus: 'scanning' } }] });
  const result3 = reducer(previous3, { type: ACTION_TYPES.FETCH_AGENT_STATUS, payload: null });

  assert.equal(result3.hostList[0].agentStatus.scanStatus, 'scanning');
});