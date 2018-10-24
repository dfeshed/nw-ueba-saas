import { columnsDataForIndicatorTable, columnConfigForUsers, severityMap, columnDataForFavorites, sortOptions, dateTimeFilterOptionsForAlerts } from 'investigate-users/utils/column-config';
import { module, test } from 'qunit';

module('Unit | Utility | column-config', function() {

  test('test columnsDataForIndicatorTable have all columns', (assert) => {
    assert.ok(columnsDataForIndicatorTable.length === 4);
  });

  test('test columnConfigForUsers have all columns', (assert) => {
    assert.ok(columnConfigForUsers.length === 5);
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
    assert.ok(sortOptions.length === 3);
  });

  test('test dateTimeFilterOptionsForAlerts have all columns', (assert) => {
    assert.ok(dateTimeFilterOptionsForAlerts.name === 'alertTimeRange');
    assert.ok(dateTimeFilterOptionsForAlerts.filterValue.value[0] === 3);
    assert.ok(dateTimeFilterOptionsForAlerts.filterValue.unit === 'Months');
  });
});