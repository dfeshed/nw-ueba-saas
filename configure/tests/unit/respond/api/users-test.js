import { module, skip } from 'qunit';
import { patchSocket } from '../../../helpers/patch-socket';
import users from 'configure/actions/api/respond/users';

module('Unit | Utility | Users APIs');

skip('it creates the proper query for the getAllUsers API function', function(assert) {
  assert.expect(3);
  patchSocket((method, modelName, query) => {
    assert.equal(method, 'findAll');
    assert.equal(modelName, 'users');
    assert.deepEqual(query, {
      filter: [],
      sort: [
        {
          'descending': false,
          'field': 'name'
        }
      ]
    });
  });
  users.getAllUsers();
});

skip('it creates the proper query for the getAllEnabledUsers API function', function(assert) {
  assert.expect(3);
  patchSocket((method, modelName, query) => {
    assert.equal(method, 'findAll');
    assert.equal(modelName, 'users');
    assert.deepEqual(query, {
      filter: [
        {
          'field': 'status',
          'value': 'enabled'
        }
      ],
      sort: [
        {
          'descending': false,
          'field': 'name'
        }
      ]
    });
  });
  users.getAllEnabledUsers();
});
