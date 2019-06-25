import { module, test } from 'qunit';
import indicatorEvents from '../../../data/presidio/indicator-distinctEventsByTime';
import _ from 'lodash';

import { setupTest } from 'ember-qunit';
import activityTimeAnomalySettings from 'entity-details/utils/chart-settings/activity-time-anomaly-settings';
module('Unit | Utils | activity-time-anomaly-settings', (hooks) => {
  setupTest(hooks);

  test('it returns activityTimeAnomalySettings', (assert) => {
    const settings = activityTimeAnomalySettings('high_number_of_successful_object_change_operations');
    assert.equal(settings.chartSettings.type, 'column');
    assert.deepEqual(settings.params, {
      feature: 'high_number_of_successful_object_change_operations',
      function: 'distinctEventsByTime'
    });
    assert.ok(settings.dataAggregator);
    assert.ok(settings.dataAdapter);
  });

  test('it dataAggregator function aggregates data', (assert) => {
    const settings = activityTimeAnomalySettings('high_number_of_successful_object_change_operations');
    let changedData = _.map(indicatorEvents.data, settings.dataAdapter);
    assert.equal(changedData.length, 262);
    assert.deepEqual(changedData[0], {
      category: '12 Nov',
      color: '#CC3300',
      originalCategory: '1542034800000',
      value: 2
    });
    changedData = settings.dataAggregator(changedData);
    assert.equal(changedData.length, 30);
    assert.deepEqual(changedData[0], {
      category: '12 Nov',
      color: '#CC3300',
      originalCategory: '1542034800000',
      value: 14
    });
  });
});