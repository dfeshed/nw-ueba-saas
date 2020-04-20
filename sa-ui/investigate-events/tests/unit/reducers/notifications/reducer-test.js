// *******
// BEGIN - Should be moved with Download Manager
// *******
import { test, module } from 'qunit';
import reducer from 'investigate-events/reducers/investigate/notifications/reducer';
import * as ACTION_TYPES from 'investigate-events/actions/types';
import Immutable from 'seamless-immutable';

module('Unit | Reducers | Notifications | Investigate');

const initialState = Immutable.from({
  stopNotifications: null
});

const storedState = Immutable.from({
  stopNotifications: () => {}
});

test('INITIALIZE_INVESTIGATE should set stopNotification fn if it is there in state', function(assert) {
  const action = {
    type: ACTION_TYPES.INITIALIZE_INVESTIGATE
  };

  const result = reducer(storedState, action);
  assert.equal(typeof result.stopNotifications, 'function', 'returns a function');
});

test('INITIALIZE_INVESTIGATE should set stopNotification as null if it is not there in state', function(assert) {

  const action = {
    type: ACTION_TYPES.INITIALIZE_INVESTIGATE
  };

  const result = reducer(initialState, action);
  assert.equal(result.stopNotifications, null, 'returns null');
});

test('NOTIFICATION_INIT_SUCCESS should set stopNotification fn with the returned callback fn', function(assert) {

  const action = {
    type: ACTION_TYPES.NOTIFICATION_INIT_SUCCESS,
    payload: {
      cancelFn: () => {}
    }
  };

  const result = reducer(initialState, action);
  assert.equal(typeof result.stopNotifications, 'function', 'returns a function');
});

test('NOTIFICATION_TEARDOWN_SUCCESS should set stopNotification as null', function(assert) {

  const action = {
    type: ACTION_TYPES.NOTIFICATION_TEARDOWN_SUCCESS
  };

  const result = reducer(storedState, action);
  assert.equal(result.stopNotifications, null, 'return a null');
});
// *******
// END - Should be moved with Download Manager
// *******