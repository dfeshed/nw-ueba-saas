import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { hasUniqueName, isColumnGroupValid } from 'investigate-events/util/validations';

module('Unit | Util | Validations', function(hooks) {
  setupTest(hooks);

  const columns = [
    { field: 'a', title: 'a' },
    { field: 'b', title: 'b' },
    { field: 'c', title: 'c' }
  ];
  const list = [
    { name: 'foo', id: 1, columns },
    { name: 'bar', id: 2, columns }
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

  test('isColumnGroupValid has correct result when editedItem has unique name and 3 columns', function(assert) {
    const columnGroup = {
      name: 'baz',
      columns
    };
    const result = isColumnGroupValid(columnGroup, list);
    assert.ok(result);
  });

  test('isColumnGroupValid has correct result when editedItem name is not unique', function(assert) {
    const columnGroup = {
      name: 'foo',
      columns
    };
    const result = isColumnGroupValid(columnGroup, list);
    assert.notOk(result);
  });

  test('isColumnGroupValid has correct result when editedItem has no name', function(assert) {
    const columnGroup = {
      name: '',
      columns
    };
    const result = isColumnGroupValid(columnGroup, list);
    assert.notOk(result);
  });

  test('isColumnGroupValid has correct result when editedItem has less than 3 columns', function(assert) {
    const columnGroup = {
      name: 'baz',
      columns: [
        { field: 'a', title: 'a' },
        { field: 'b', title: 'b' }
      ]
    };
    const result = isColumnGroupValid(columnGroup, list);
    assert.notOk(result);
  });
});
