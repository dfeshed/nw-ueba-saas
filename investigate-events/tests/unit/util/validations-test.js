import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { hasUniqueName, isColumnGroupValid, isProfileValid } from 'investigate-events/util/validations';

module('Unit | Util | Validations', function(hooks) {
  setupTest(hooks);

  const columns = [
    { field: 'a', title: 'a' },
    { field: 'b', title: 'b' },
    { field: 'c', title: 'c' }
  ];
  const columnGroupsList = [
    { name: 'foo', id: 1, columns },
    { name: 'bar', id: 2, columns }
  ];

  const columnGroup = {};
  const metaGroup = {};
  const preQuery = 'PRE_QUERY';
  const profilesList = [
    { name: 'jaz', id: 1, columnGroup, metaGroup, preQuery },
    { name: 'taz', id: 2, columnGroup, metaGroup, preQuery }
  ];

  test('hasUniqueName has correct result when newItem name is unique', function(assert) {
    const result = hasUniqueName('baz', undefined, columnGroupsList);
    assert.ok(result);
  });

  test('hasUniqueName has correct result when newItem name is not unique', function(assert) {
    const result = hasUniqueName('bar', undefined, columnGroupsList);
    assert.notOk(result);
  });

  test('hasUniqueName has correct result when editedItem name is unique', function(assert) {
    const result = hasUniqueName('baz', columnGroupsList[1].id, columnGroupsList);
    assert.ok(result);
  });

  test('hasUniqueName has correct result when editedItem name is unchanged', function(assert) {
    const result = hasUniqueName('bar', columnGroupsList[1].id, columnGroupsList);
    assert.ok(result);
  });

  test('hasUniqueName has correct result when editedItem name matched another item name', function(assert) {
    const result = hasUniqueName('foo', columnGroupsList[1].id, columnGroupsList);
    assert.notOk(result);
  });

  test('isColumnGroupValid has correct result when editedItem has unique name and 3 columns', function(assert) {
    const columnGroup = {
      name: 'baz',
      columns
    };
    const result = isColumnGroupValid(columnGroup, columnGroupsList);
    assert.ok(result);
  });

  test('isColumnGroupValid has correct result when editedItem name is not unique', function(assert) {
    const columnGroup = {
      name: 'foo',
      columns
    };
    const result = isColumnGroupValid(columnGroup, columnGroupsList);
    assert.notOk(result);
  });

  test('isColumnGroupValid has correct result when editedItem has no name', function(assert) {
    const columnGroup = {
      name: '',
      columns
    };
    const result = isColumnGroupValid(columnGroup, columnGroupsList);
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
    const result = isColumnGroupValid(columnGroup, columnGroupsList);
    assert.notOk(result);
  });

  test('isProfileValid has correct result when editedItem has unique name and all the required parameters', function(assert) {
    const profile = {
      name: 'My Profile',
      columnGroup,
      metaGroup,
      preQuery
    };
    const result = isProfileValid(profile, profilesList);
    assert.ok(result);
  });

  test('isProfileValid has correct result when editedItem has no name', function(assert) {
    const profile = {
      name: '',
      columnGroup,
      metaGroup,
      preQuery
    };
    const result = isProfileValid(profile, profilesList);
    assert.notOk(result);
  });

  test('isProfileValid has correct result when editedItem does not have a unique name', function(assert) {
    const profile = {
      name: 'taz',
      columnGroup,
      metaGroup,
      preQuery
    };
    const result = isProfileValid(profile, profilesList);
    assert.notOk(result);
  });

  test('isProfileValid has correct result when editedItem does not have a columnGroup', function(assert) {
    const profile = {
      name: 'My Profile',
      metaGroup,
      preQuery
    };
    const result = isProfileValid(profile, profilesList);
    assert.notOk(result);
  });

  test('isProfileValid has correct result when editedItem does not have a metaGroup', function(assert) {
    const profile = {
      name: 'My Profile',
      columnGroup,
      preQuery
    };
    const result = isProfileValid(profile, profilesList);
    assert.notOk(result);
  });

  test('isProfileValid has correct result when editedItem does not have a preQuery', function(assert) {
    const profile = {
      name: 'My Profile',
      columnGroup,
      metaGroup
    };
    const result = isProfileValid(profile, profilesList);
    assert.notOk(result);
  });
});
