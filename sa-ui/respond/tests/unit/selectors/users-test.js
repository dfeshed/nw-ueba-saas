import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import {
  getAllUsers,
  getAllUsersStatus
} from 'respond/selectors/users';

module('Unit | Utility | User Selectors');

const allUsers = [{ id: 'local' }, { id: 'admin' }];

const users = Immutable.from({
  allUsers,
  allUsersStatus: 'completed'
});

const state = {
  respond: {
    users
  }
};

test('Basic Users selectors', function(assert) {
  assert.equal(getAllUsers(state), users.allUsers, 'The returned value from the getAllUsers selector is as expected');
  assert.equal(getAllUsersStatus(state), 'completed', 'The returned value from the getAllUsersStatus selector is as expected');
});
