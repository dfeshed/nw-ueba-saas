import Immutable from 'seamless-immutable';
import { module, test } from 'qunit';
import { LIFECYCLE } from 'redux-pack';
import usersReducer from 'respond/reducers/respond/users';
import ACTION_TYPES from 'respond/actions/types';
import makePackAction from '../../helpers/make-pack-action';

const initialState = Immutable.from({
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

