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
  });

  test('it dataAggregator function aggregates data', (assert) => {
    const settings = activityTimeAnomalySettings('high_number_of_successful_object_change_operations');
    const changedData = _.map(indicatorEvents.data[0].data, (data) => settings.dataAdapter(data, 'UTC', 'en'));
    assert.equal(changedData.length, 262);

    assert.deepEqual(changedData[0], {
      category: '12 Nov 15:00',
      color: '#A60808',
      originalCategory: '1542034800000',
      strokeColor: '#01579B',
      value: 2,
      radius: 5
    });
  });

  test('it dataAggregator function aggregates data', (assert) => {
    const settings = activityTimeAnomalySettings('high_number_of_successful_object_change_operations');
    const changedData = _.map(indicatorEvents.data[0].data, (data) => settings.dataAdapter(data, 'UTC', 'en', 'globalData-'));
    assert.equal(changedData.length, 262);

    assert.deepEqual(changedData[0], {
      category: '12 Nov 15:00',
      'globalData-color': '#A60808',
      'globalData-radius': 5,
      'globalData-value': 2,
      originalCategory: '1542034800000',
      strokeColor: '#01579B'
    });
  });
});