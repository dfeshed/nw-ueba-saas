import { module, test } from 'qunit';

import { setupTest } from 'ember-qunit';
import singlePieSettings from 'entity-details/utils/chart-settings/single-pie-settings';
module('Unit | Utils | single-pie-settings', (hooks) => {
  setupTest(hooks);

  test('it returns singlePieSettings', (assert) => {
    const settings = singlePieSettings('abnormal_event_day_time');
    assert.equal(settings.chartSettings.type, 'pie');
    assert.deepEqual(settings.params, {
      feature: 'abnormal_event_day_time',
      function: 'Count'
    });
    assert.ok(settings.sortData);
    assert.ok(settings.dataAdapter);
  });
});