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
      loading: false,
      error: true
    });

    const newAction = makePackAction(LIFECYCLE.START, {
      type: ACTION_TYPES.GET_RAR_INSTALLER_ID
    });
    const newEndState = reducer(previous, newAction);
    assert.equal(newEndState.loading, true);
    assert.equal(newEndState.error, false);
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

  test('The GET_RAR_INSTALLER_ID sets the error status to true', function(assert) {
    const previous = Immutable.from({
      error: false
    });

    const newAction = makePackAction(LIFECYCLE.FAILURE, {
      type: ACTION_TYPES.GET_RAR_INSTALLER_ID
    });
    const newEndState = reducer(previous, newAction);
    assert.equal(newEndState.error, true);
    assert.equal(newEndState.downloadLink, null);
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

});
