import Ember from 'ember';
import { module, test } from 'qunit';
import { LIFECYCLE } from 'redux-pack';
import usersReducer from 'respond/reducers/respond/users';
import ACTION_TYPES from 'respond/actions/types';
import makePackAction from '../../helpers/make-pack-action';

const { copy } = Ember;

const initialState = {
  users: [],
  usersStatus: null
};

module('Unit | Utility | Users Actions - Reducers');

test('When FETCH_ALL_USERS starts, the usersStatus changes to wait', function(assert) {
  const initState = copy(initialState);
  const usersStatus = 'wait';
  const expectedEndState = {
    ...initState,
    usersStatus
  };
  const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_ALL_USERS });
  const endState = usersReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When FETCH_ALL_USERS fails, the incidentsStatus changes to error', function(assert) {
  const initState = copy(initialState);
  const usersStatus = 'error';
  const expectedEndState = {
    ...initState,
    usersStatus
  };
  const action = makePackAction(LIFECYCLE.FAILURE, { type: ACTION_TYPES.FETCH_ALL_USERS });
  const endState = usersReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When FETCH_ALL_USERS succeeds, the incidents array and incidentsStatus update appropriately', function(assert) {
  const initState = copy(initialState);
  const users = [{ name: 'Ignatius Reilly' }];
  const usersStatus = 'completed';
  const expectedEndState = {
    ...initState,
    usersStatus,
    users
  };
  const action = makePackAction(LIFECYCLE.SUCCESS, { type: ACTION_TYPES.FETCH_ALL_USERS, payload: { data: users } });
  const endState = usersReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

