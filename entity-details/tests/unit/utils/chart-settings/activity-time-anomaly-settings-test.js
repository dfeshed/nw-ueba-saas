import { module, test } from 'qunit';

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
    assert.ok(settings.dataAdapter);
  });
});