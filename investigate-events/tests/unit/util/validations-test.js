import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { hasUniqueName } from 'investigate-events/util/validations';

module('Unit | Util | Validations', function(hooks) {
  setupTest(hooks);


  const list = [
    { name: 'foo', id: 1 },
    { name: 'bar', id: 2 }
  ];

  test('hasUniqueName has correct result when newItem name is unique', function(assert) {
    const result = hasUniqueName('baz', undefined, list);
    assert.ok(result);
  });

  test('hasUniqueName has correct result when newItem name is not unique', function(assert) {
    const result = hasUniqueName('bar', undefined, list);
    assert.notOk(result);
  });

  test('hasUniqueName has correct result when editedItem name is unique', function(assert) {
    const result = hasUniqueName('baz', list[1].id, list);
    assert.ok(result);
  });

  test('hasUniqueName has correct result when editedItem name is unchanged', function(assert) {
    const result = hasUniqueName('bar', list[1].id, list);
    assert.ok(result);
  });

  test('hasUniqueName has correct result when editedItem name matched another item name', function(assert) {
    const result = hasUniqueName('foo', list[1].id, list);
    assert.notOk(result);
  });
});
