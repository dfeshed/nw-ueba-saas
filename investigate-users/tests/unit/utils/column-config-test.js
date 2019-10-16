import { columnsDataForIndicatorTable, columnConfigForUsers, severityMap, columnDataForFavorites, sortOptions } from 'investigate-users/utils/column-config';
import { module, test } from 'qunit';

module('Unit | Utility | column-config', function() {

  test('test columnsDataForIndicatorTable have all columns', (assert) => {
    assert.ok(columnsDataForIndicatorTable.length === 4);
  });

  test('test columnConfigForUsers have all columns', (assert) => {
    assert.equal(columnConfigForUsers.length, 7);
  });

  test('test severityMap have all severity', (assert) => {
    assert.ok(severityMap.Critical === 'danger');
    assert.ok(severityMap.Medium === 'medium');
    assert.ok(severityMap.High === 'high');
    assert.ok(severityMap.Low === 'low');
  });

  test('test columnDataForFavorites have all columns', (assert) => {
    assert.ok(columnDataForFavorites.length === 1);
  });

  test('test sortOptions have all columns', (assert) => {
    assert.equal(sortOptions.length, 5);
  });
});