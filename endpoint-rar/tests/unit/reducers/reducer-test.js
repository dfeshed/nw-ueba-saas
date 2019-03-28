import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'endpoint-rar/reducers/rar';
import * as ACTION_TYPES from 'endpoint-rar/actions/types';
import makePackAction from '../../helpers/make-pack-action';
import { LIFECYCLE } from 'redux-pack';
import { setupTest } from 'ember-qunit';

module('Unit | Reducers | investigate', function(hooks) {
  setupTest(hooks);

  test('The GET_RAR_INSTALLER_ID sets the loading status to true', function(assert) {
    const previous = Immutable.from({
      loading: false
    });

    const newAction = makePackAction(LIFECYCLE.START, {
      type: ACTION_TYPES.GET_RAR_INSTALLER_ID
    });
    const newEndState = reducer(previous, newAction);
    assert.equal(newEndState.loading, true);
  });

  test('The GET_RAR_INSTALLER_ID sets the loading status to false', function(assert) {
    const previous = Immutable.from({
      loading: false,
      downloadId: null
    });

    const newAction = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.GET_RAR_INSTALLER_ID,
      payload: { data: { id: 'test_id' } }
    });
    const newEndState = reducer(previous, newAction);
    assert.equal(newEndState.loading, false);
    assert.equal(newEndState.downloadId, 'test_id');
  });

  test('The GET_RAR_INSTALLER_ID sets the downloadId value to null', function(assert) {
    const previous = Immutable.from({
      downloadId: '4f34rf4fds'
    });

    const newAction = makePackAction(LIFECYCLE.FAILURE, {
      type: ACTION_TYPES.GET_RAR_INSTALLER_ID
    });
    const newEndState = reducer(previous, newAction);
    assert.equal(newEndState.downloadId, null);
  });

  test('The SET_SERVER_ID sets the endpoint serverId', function(assert) {
    const previous = Immutable.from({
      serverId: null
    });

    const newAction = makePackAction(LIFECYCLE.START, {
      type: ACTION_TYPES.SET_SERVER_ID,
      payload: 'test_serverId'
    });

    const newEndState = reducer(previous, newAction);
    assert.equal(newEndState.serverId, 'test_serverId');
  });

  test('The GET_AND_SAVE_RAR_CONFIG gets the endpoint server config', function(assert) {
    const configData = {
      enabled: true,
      esh: 'esh-domain',
      servers: [
        {
          address: 'localhost',
          httpsPort: 443,
          httpsBeaconIntervalInSeconds: 900
        }
      ]
    };
    const expected = {
      defaultRARConfig: { rarConfig: { ...configData, ...configData.servers[0], httpsBeaconIntervalInSeconds: 15 } },
      initialRARConfig: { rarConfig: { ...configData, ...configData.servers[0], httpsBeaconIntervalInSeconds: 15 } }
    };

    const previous = Immutable.from({
      defaultRARConfig: { rarConfig: {} },
      initialRARConfig: { rarConfig: {} }
    });

    const newAction = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.GET_AND_SAVE_RAR_CONFIG,
      payload: { data: { ...configData } }
    });

    const newEndState = reducer(previous, newAction);
    assert.deepEqual(newEndState, expected);
  });

  test('The RESET_RAR_CONFIG resets the endpoint server config', function(assert) {
    const configData = {
      enabled: true,
      esh: 'esh-domain',
      servers: [
        {
          address: 'localhost',
          httpsPort: 443,
          httpsBeaconIntervalInSeconds: 900
        }
      ],
      address: 'localhost',
      httpsPort: 443,
      httpsBeaconIntervalInSeconds: 900
    };
    const expected = {
      defaultRARConfig: { rarConfig: { ...configData } },
      initialRARConfig: { rarConfig: { ...configData } }
    };

    const previous = Immutable.from({
      defaultRARConfig: { rarConfig: { ...configData, httpsPort: 4200 } },
      initialRARConfig: { rarConfig: { ...configData } }
    });

    const newAction = makePackAction(LIFECYCLE.START, {
      type: ACTION_TYPES.RESET_RAR_CONFIG
    });

    const newEndState = reducer(previous, newAction);
    assert.deepEqual(newEndState, expected);
  });

  test('The UPDATE_UI_STATE resets the endpoint server config', function(assert) {
    const configData = {
      enabled: true,
      esh: 'esh-domain',
      servers: [
        {
          address: 'localhost',
          httpsPort: 443,
          httpsBeaconIntervalInSeconds: 900
        }
      ],
      address: 'localhost',
      httpsPort: 443,
      httpsBeaconIntervalInSeconds: 900
    };
    const expected = {
      defaultRARConfig: { rarConfig: { ...configData, httpsPort: 4200 } },
      initialRARConfig: { rarConfig: { ...configData } }
    };

    const previous = Immutable.from({
      defaultRARConfig: { rarConfig: { ...configData } },
      initialRARConfig: { rarConfig: { ...configData } }
    });

    const newAction = makePackAction(LIFECYCLE.START, {
      type: ACTION_TYPES.UPDATE_UI_STATE,
      payload: { rarConfig: { ...configData, httpsPort: 4200 } }
    });

    const newEndState = reducer(previous, newAction);
    assert.deepEqual(newEndState, expected);
  });
});