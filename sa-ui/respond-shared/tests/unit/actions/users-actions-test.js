import Immutable from 'seamless-immutable';
import { module, test } from 'qunit';
import { LIFECYCLE } from 'redux-pack';
import usersReducer from 'respond-shared/reducers/create-incident/reducers';
import ACTION_TYPES from 'respond-shared/actions/types';
import makePackAction from '../../helpers/make-pack-action';

const initialState = Immutable.from({
  enabledUsers: [],
  enabledUsersStatus: null
});

module('Unit | Utility | Users Actions - Reducers');

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