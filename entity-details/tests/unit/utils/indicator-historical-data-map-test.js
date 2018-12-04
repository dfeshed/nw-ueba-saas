import { module, test } from 'qunit';

import { setupTest } from 'ember-qunit';
import indicatorHistoricalDataMap from 'entity-details/utils/indicator-historical-data-map';
module('Unit | Utils | indicator-historical-data-map', (hooks) => {
  setupTest(hooks);

  test('it can test indicatorHistoricalDataMap', (assert) => {
    assert.deepEqual(indicatorHistoricalDataMap.high_number_of_successful_object_change_operations, {
      feature: 'high_number_of_successful_object_change_operations',
      function: 'distinctEventsByTime'
    });
    assert.deepEqual(indicatorHistoricalDataMap.abnormal_event_day_time, {
      feature: 'abnormal_event_day_time',
      function: 'hourlyCountGroupByDayOfWeek'
    });
    assert.deepEqual(indicatorHistoricalDataMap.abnormal_process_injects_into_windows_process, {
      feature: 'abnormal_process_injects_into_windows_process',
      function: 'Count'
    });
  });

});