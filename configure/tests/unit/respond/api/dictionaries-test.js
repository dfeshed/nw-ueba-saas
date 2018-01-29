import { module, skip } from 'qunit';
import { patchSocket } from '../../../helpers/patch-socket';
import dictionaries from 'configure/actions/api/respond/dictionaries';

module('Unit | Utility | Dictionary APIs');

skip('it creates the proper query for the getAllCategories API function', function(assert) {
  assert.expect(3);
  patchSocket((method, modelName, query) => {
    assert.equal(method, 'findAll');
    assert.equal(modelName, 'category-tags');
    assert.deepEqual(query, {});
  });
  dictionaries.getAllCategories();
});
