import { moduleFor, test } from 'ember-qunit';
import { patchSocket } from '../../../helpers/patch-socket';
import dictionaries from 'configure/actions/api/respond/dictionaries';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

moduleFor('service:request', {
  beforeEach() {
    initialize(this);
  }
});

test('it creates the proper query for the getAllCategories API function', function(assert) {
  assert.expect(3);
  patchSocket((method, modelName, query) => {
    assert.equal(method, 'findAll');
    assert.equal(modelName, 'category-tags');
    assert.deepEqual(query, {});
  });
  dictionaries.getAllCategories();
});
