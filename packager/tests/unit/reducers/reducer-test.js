import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'packager/reducers/packager';
import * as ACTION_TYPES from 'packager/actions/types';
import data from '../../data/subscriptions/packageconfig/get/data';
import fieldsData from '../../data/subscriptions/packageconfig/updateFields/data';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../helpers/make-pack-action';
module('Unit | Reducers | Packager');

const initialState = Immutable.from({
  defaultPackagerConfig: { packageConfig: {} },
  error: null,
  loading: true,
  downloadLink: null,
  updating: false,
  initialState: {
    packageConfig: {}
  },
  devices: {},
  selectedServerIP: null,
  endpointServerList: []
});

test('should return the initial state', function(assert) {
  const result = reducer(undefined, {});
  assert.deepEqual(result, initialState);
});


test('should reset the form, defaultPackagerConfig to previous state value', function(assert) {
  const result = reducer(undefined, { type: ACTION_TYPES.RESET_FORM });
  assert.deepEqual(result, initialState);
});

test('should retrieve failure', function(assert) {
  const previous = Immutable.from({
    error: false,
    loading: true
  });
  const result = reducer(previous, { type: ACTION_TYPES.RETRIEVE_FAILURE });
  assert.equal(result.error, true, 'error is set to true');
  assert.equal(result.loading, false, 'loading is set to false');
});

test('Get defaultPackagerConfig ', function(assert) {
  const previous = Immutable.from({
    defaultPackagerConfig: {},
    error: false,
    loading: false,
    initialState: {},
    selectedServerIP: '10.40.12.21'
  });
  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.GET_INFO,
    payload: { data }
  });

  const endState = reducer(previous, action);
  assert.deepEqual(endState.defaultPackagerConfig, { ...data, packageConfig: { ...data } });
  assert.deepEqual(endState.initialState, { ...data, packageConfig: { ...data } });
});

test('GET_INFO state for start', function(assert) {
  const previous = Immutable.from({
    error: true,
    loading: false
  });
  const action = makePackAction(LIFECYCLE.START, {
    type: ACTION_TYPES.GET_INFO,
    payload: { data }
  });

  const endState = reducer(previous, action);
  assert.deepEqual(endState.loading, true, 'loading is set to true');
  assert.deepEqual(endState.error, false, 'error is set to false');
});

test('GET_INFO state for failure', function(assert) {
  const previous = Immutable.from({
    error: false
  });
  const action = makePackAction(LIFECYCLE.FAILURE, {
    type: ACTION_TYPES.GET_INFO,
    payload: { data }
  });

  const endState = reducer(previous, action);
  assert.deepEqual(endState.error, true, 'error is set to false');
});

test('Update Redux state with UI state ', function(assert) {
  const previous = Immutable.from({
    defaultPackagerConfig: {}
  });
  const action = {
    type: ACTION_TYPES.UPDATE_FIELDS,
    payload: fieldsData
  };
  const endState = reducer(previous, action);
  assert.deepEqual(endState.defaultPackagerConfig, fieldsData);
});

test('DOWNLOAD_PACKAGE set state downloadLink', function(assert) {
  const previous = Immutable.from({
    downloadLink: null
  });
  const action = {
    type: ACTION_TYPES.DOWNLOAD_PACKAGE,
    payload: 'https:/download/agentConfig'
  };
  const endState = reducer(previous, action);
  assert.deepEqual(endState.downloadLink, 'https:/download/agentConfig');
});

test('set selected server ip check', function(assert) {
  const previous = Immutable.from({
    selectedServerIP: null
  });
  const action = {
    type: ACTION_TYPES.SET_SELECTED_SERVER_IP,
    payload: '10.30.12.1'
  };
  const endState = reducer(previous, action);
  assert.deepEqual(endState.selectedServerIP, '10.30.12.1');
});

test('set downloadLink state', function(assert) {
  const previous = Immutable.from({
    updating: true
  });

  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.SAVE_INFO
  });
  const endState = reducer(previous, action);
  assert.deepEqual(endState.updating, false, 'updating is set to false');
});

test('set downloadLink state for start', function(assert) {
  const previous = Immutable.from({
    updating: false,
    error: true
  });

  const action = makePackAction(LIFECYCLE.START, {
    type: ACTION_TYPES.SAVE_INFO
  });
  const endState = reducer(previous, action);
  assert.deepEqual(endState.updating, true, 'updating is set to true');
  assert.deepEqual(endState.error, false, 'error is set to false');
});

test('set downloadLink state for failure', function(assert) {
  const previous = Immutable.from({
    updating: false,
    error: true
  });

  const action = makePackAction(LIFECYCLE.FAILURE, {
    type: ACTION_TYPES.SAVE_INFO
  });
  const endState = reducer(previous, action);
  assert.deepEqual(endState.updating, false, 'on failure updating state is not changed');
  assert.deepEqual(endState.error, true, 'on failure error state is not changed');
});

test('get endpoint servers', function(assert) {
  const previous = Immutable.from({
    endpointServerList: []
  });
  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.GET_ENDPOINT_SERVERS,
    payload: { data: [
      {
        id: '2f3a0c01-a366-49a7-afb5-f04036fc7a97',
        name: 'endpoint-server',
        displayName: 'EPS1-Arya - Endpoint Server',
        host: '10.40.15.204',
        port: 7050,
        useTls: true,
        version: '11.3.1.0',
        family: 'launch',
        meta: {}
      },
      {
        id: 'ec8e6e1e-efa6-45d6-b577-6b39de544e00',
        name: 'endpoint-server',
        displayName: 'EPS2-Arya - Endpoint Server',
        host: '10.40.12.5',
        port: 7050,
        useTls: true,
        version: '11.3.0.0',
        family: 'launch',
        meta: {}
      }
    ] }
  });
  const endState = reducer(previous, action);
  assert.equal(endState.endpointServerList.length, 2, 'Endpoint server list updated');
});