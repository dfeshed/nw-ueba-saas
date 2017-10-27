import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import { getAssigneeOptions, getEnabledUsers, getAllUsers, getAllUsersStatus, getEnabledUsersStatus } from 'respond/selectors/users';

module('Unit | Utility | User Selectors');

const enabledUsers = [{ id: 'admin' }];
const allUsers = [{ id: 'local' }, { id: 'admin' }];

const users = Immutable.from({
  enabledUsers,
  enabledUsersStatus: 'wait',
  allUsers,
  allUsersStatus: 'completed'
});

const state = {
  respond: {
    users
  }
};

test('Basic Users selectors', function(assert) {
  assert.equal(getEnabledUsers(state), users.enabledUsers, 'The returned value from the getEnabledUsers selector is as expected');
  assert.equal(getEnabledUsersStatus(state), 'wait', 'The returned value from the getEnabledUsersStatus selector is as expected');
  assert.equal(getAllUsers(state), users.allUsers, 'The returned value from the getAllUsers selector is as expected');
  assert.equal(getAllUsersStatus(state), 'completed', 'The returned value from the getAllUsersStatus selector is as expected');
  assert.deepEqual(getAssigneeOptions(state), [{ id: 'UNASSIGNED' }, { id: 'admin' }]);
});
