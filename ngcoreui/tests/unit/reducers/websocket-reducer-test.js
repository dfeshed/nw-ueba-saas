import { test, module } from 'qunit';
import { setupTest } from 'ember-qunit';
import reducer from 'ngcoreui/reducers/shared-reducers';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../helpers/make-pack-action';
import * as ACTION_TYPES from 'ngcoreui/actions/types';
import ReduxDataHelper from '../../helpers/redux-data-helper';

module('Unit | Reducers | WebSocket', (hooks) => {

  setupTest(hooks);

  test('WS_CONNECT_START changes state to connecting state (from empty state)', (assert) => {
    const action = {
      type: ACTION_TYPES.WS_CONNECT_START
    };
    const state = new ReduxDataHelper().disconnected().build().shared;
    const result = reducer(state, action);

    assert.strictEqual(result.wsConnecting, true, 'wsConnecting is true');
    assert.strictEqual(result.wsConnected, false, 'wsConnected is false');
    assert.strictEqual(result.wsErr, null, 'wsErr is null');
  });

  test('WS_CONNECT_START changes state to connecting state (from error state)', (assert) => {
    const action = {
      type: ACTION_TYPES.WS_CONNECT_START
    };
    const state = new ReduxDataHelper().wsErr().build().shared;
    const result = reducer(state, action);

    assert.strictEqual(result.wsConnecting, true, 'wsConnecting is true');
    assert.strictEqual(result.wsConnected, false, 'wsConnected is false');
    assert.strictEqual(result.wsErr, null, 'wsErr is null');
  });

  test('WS_CONNECT_FINISH changes state to connected state (from connecting state)', (assert) => {
    const action = {
      type: ACTION_TYPES.WS_CONNECT_FINISH
    };
    const state = new ReduxDataHelper().connecting().build().shared;
    const result = reducer(state, action);

    assert.strictEqual(result.wsConnecting, false, 'wsConnecting is false');
    assert.strictEqual(result.wsConnected, true, 'wsConnected is true');
    assert.strictEqual(result.wsErr, null, 'wsErr is null');
  });

  test('WS_ERR changes state to error state (from connecting state)', (assert) => {
    const action = {
      type: ACTION_TYPES.WS_ERROR,
      payload: 'WebSocket error'
    };
    const state = new ReduxDataHelper().connecting().build().shared;
    const result = reducer(state, action);

    assert.strictEqual(result.wsConnecting, false, 'wsConnecting is false');
    assert.strictEqual(result.wsConnected, false, 'wsConnected is false');
    assert.notStrictEqual(result.wsErr, null, 'wsErr is not null');
  });

  test('WS_ERR changes state to error state (from connected state)', (assert) => {
    const action = {
      type: ACTION_TYPES.WS_ERROR,
      payload: 'WebSocket error'
    };
    const state = new ReduxDataHelper().connected().build().shared;
    const result = reducer(state, action);

    assert.strictEqual(result.wsConnecting, false, 'wsConnecting is false');
    assert.strictEqual(result.wsConnected, false, 'wsConnected is false');
    assert.notStrictEqual(result.wsErr, null, 'wsErr is not null');
  });

  test('WS_DISCONNECT changes state to disconnected state (from connecting state)', (assert) => {
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.WS_DISCONNECT
    });
    const state = new ReduxDataHelper().connecting().build().shared;
    const result = reducer(state, action);

    assert.strictEqual(result.wsConnecting, false, 'wsConnecting is false');
    assert.strictEqual(result.wsConnected, false, 'wsConnected is false');
    assert.strictEqual(result.wsErr, null, 'wsErr is null');
  });

  test('WS_DISCONNECT changes state to disconnected state (from connected state)', (assert) => {
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.WS_DISCONNECT
    });
    const state = new ReduxDataHelper().connected().build().shared;
    const result = reducer(state, action);

    assert.strictEqual(result.wsConnecting, false, 'wsConnecting is false');
    assert.strictEqual(result.wsConnected, false, 'wsConnected is false');
    assert.strictEqual(result.wsErr, null, 'wsErr is null');
  });

  test('WS_DISCONNECT changes state to disconnected state (from error state)', (assert) => {
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.WS_DISCONNECT
    });
    const state = new ReduxDataHelper().wsErr().build().shared;
    const result = reducer(state, action);

    assert.strictEqual(result.wsConnecting, false, 'wsConnecting is false');
    assert.strictEqual(result.wsConnected, false, 'wsConnected is false');
    assert.strictEqual(result.wsErr, null, 'wsErr is null');
  });

});
