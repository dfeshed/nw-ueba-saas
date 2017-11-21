import Immutable from 'seamless-immutable';
import { module, test } from 'qunit';
import { LIFECYCLE } from 'redux-pack';
import usersReducer from 'configure/reducers/respond/users/reducer';
import ACTION_TYPES from 'configure/actions/types/respond';
import makePackAction from '../../../helpers/make-pack-action';

const initialState = Immutable.from({
  enabledUsers: [],
  enabledUsersStatus: null,
  allUsers: [],
  allUsersStatus: null
});

module('Unit | Utility | Users Actions - Reducers');

test('When FETCH_ALL_USERS starts, the allUsersStatus changes to wait', function(assert) {
  const allUsersStatus = 'wait';
  const expectedEndState = {
    ...initialState,
    allUsersStatus
  };
  const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_ALL_USERS });
  const endState = usersReducer(initialState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When FETCH_ALL_USERS fails, the allUsersStatus changes to error', function(assert) {
  const allUsersStatus = 'error';
  const expectedEndState = {
    ...initialState,
    allUsersStatus
  };
  const action = makePackAction(LIFECYCLE.FAILURE, { type: ACTION_TYPES.FETCH_ALL_USERS });
  const endState = usersReducer(initialState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When FETCH_ALL_USERS succeeds, the allUsers array and allUsersStatus update appropriately', function(assert) {
  const allUsers = [{ name: 'Ignatius Reilly' }];
  const allUsersStatus = 'completed';
  const expectedEndState = {
    ...initialState,
    allUsersStatus,
    allUsers: [{ name: 'Ignatius Reilly', isInactive: false }]
  };
  const action = makePackAction(LIFECYCLE.SUCCESS, { type: ACTION_TYPES.FETCH_ALL_USERS, payload: { data: allUsers } });
  const endState = usersReducer(initialState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When FETCH_ALL_USERS succeeds, the disabled property on any user gets remapped to isInactive', function(assert) {
  const allUsers = [{ name: 'Ignatius Reilly', disabled: true }];
  const allUsersStatus = 'completed';
  const expectedEndState = {
    ...initialState,
    allUsersStatus,
    allUsers: [{ name: 'Ignatius Reilly', isInactive: true }]
  };
  const action = makePackAction(LIFECYCLE.SUCCESS, { type: ACTION_TYPES.FETCH_ALL_USERS, payload: { data: allUsers } });
  const endState = usersReducer(initialState, action);
  assert.deepEqual(endState, expectedEndState);
});


test('When FETCH_ALL_ENABLED_USERS starts, the enabledUsersStatus changes to wait', function(assert) {
  const enabledUsersStatus = 'wait';
  const expectedEndState = {
    ...initialState,
    enabledUsersStatus
  };
  const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_ALL_ENABLED_USERS });
  const endState = usersReducer(initialState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When FETCH_ALL_ENABLED_USERS fails, the enabledUsersStatus changes to error', function(assert) {
  const enabledUsersStatus = 'error';
  const expectedEndState = {
    ...initialState,
    enabledUsersStatus
  };
  const action = makePackAction(LIFECYCLE.FAILURE, { type: ACTION_TYPES.FETCH_ALL_ENABLED_USERS });
  const endState = usersReducer(initialState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When FETCH_ALL_ENABLED_USERS succeeds, the enabledUsers array and enabledUsersStatus update appropriately', function(assert) {
  const enabledUsers = [{ name: 'Ignatius Reilly' }];
  const enabledUsersStatus = 'completed';
  const expectedEndState = {
    ...initialState,
    enabledUsersStatus,
    enabledUsers: [{ name: 'Ignatius Reilly', isInactive: false }]
  };
  const action = makePackAction(LIFECYCLE.SUCCESS, { type: ACTION_TYPES.FETCH_ALL_ENABLED_USERS, payload: { data: enabledUsers } });
  const endState = usersReducer(initialState, action);
  assert.deepEqual(endState, expectedEndState);
});

