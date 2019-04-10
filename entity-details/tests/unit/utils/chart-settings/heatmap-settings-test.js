import { module, test } from 'qunit';

import { setupTest } from 'ember-qunit';
import heatmapSettings from 'entity-details/utils/chart-settings/heatmap-settings';
module('Unit | Utils | heatmap-settings', (hooks) => {
  setupTest(hooks);

  test('it returns heatmapSettings', (assert) => {
    const settings = heatmapSettings('abnormal_logon_day_time');
    assert.equal(settings.chartSettings.type, 'heatmap');
    assert.deepEqual(settings.params, {
      feature: 'abnormal_logon_day_time',
      function: 'hourlyCountGroupByDayOfWeek'
    });
    assert.notOk(settings.sortData);
    assert.ok(settings.dataAdapter);
  });
});