import { module, test } from 'qunit';

import { setupTest } from 'ember-qunit';
import heatmapSettings from 'entity-details/utils/chart-settings/heatmap-settings';
import indicatorEvents from '../../../data/presidio/indicator-graph-activityTimeAnomaly';
import _ from 'lodash';

module('Unit | Utils | heatmap-settings', (hooks) => {
  setupTest(hooks);

  test('it returns heatmapSettings', (assert) => {
    const settings = heatmapSettings('abnormal_logon_day_time');
    assert.equal(settings.chartSettings.type, 'heatmap');
    assert.deepEqual(settings.params, {
      feature: 'abnormal_logon_day_time',
      function: 'hourlyCountGroupByDayOfWeek'
    });
    assert.ok(settings.dataAdapter);
    assert.ok(settings.dataAdapter);
  });

  test('it dataAggregator function aggregates data', (assert) => {
    const settings = heatmapSettings('abnormal_logon_day_time');
    let changedData = _.map(indicatorEvents.data, settings.dataAdapter);
    assert.equal(changedData.length, 60);
    assert.deepEqual(changedData[0], {
      color: '#CC3300',
      hour: 12,
      strokeColor: '#000000',
      value: 2,
      weekday: 'THURSDAY'
    });
    changedData = settings.dataAggregator(changedData);
    assert.equal(changedData.length, 168);
    assert.deepEqual(changedData[0], {
      color: '#202020',
      hour: 0,
      strokeColor: '#000000',
      value: 0,
      weekday: 'MONDAY'
    });
  });
});