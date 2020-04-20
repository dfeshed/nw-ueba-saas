import { module, test } from 'qunit';

import { setupTest } from 'ember-qunit';
import indicatorChartMap from 'entity-details/utils/indicator-chart-map';
module('Unit | Utils | indicator-chart-map', (hooks) => {
  setupTest(hooks);

  test('it can test indicatorChartMap has all chart details', (assert) => {
    assert.ok(indicatorChartMap.singlePie.anomalyTypeFieldName.includes('abnormal_event_day_time'), 'This should have Pie chart anomalies');
    assert.ok(indicatorChartMap.singleColumnDataRate.anomalyTypeFieldName.includes('data_bucket'), 'This should have Single column chart anomalies');
    assert.ok(indicatorChartMap.activityTimeAnomaly.anomalyTypeFieldName.includes('abnormal_file_day_time'), 'This should have Time chart anomalies');
    assert.ok(indicatorChartMap.geoLocation.anomalyTypeFieldName.includes('country'), 'This should have geoLocation chart anomalies');

    assert.ok(indicatorChartMap.timeAggregation.anomalyTypeFieldName.includes('high_number_of_successful_object_change_operations'), 'This should have timeAggregation chart anomalies');
    assert.ok(indicatorChartMap.geoLocationSequence.anomalyTypeFieldName.includes('vpn_geo_hopping'), 'This should have geoLocationSequence chart anomalies');
    assert.ok(indicatorChartMap.basicTwoHistogramsUser.anomalyTypeFieldName.includes('normalized_src_machine'), 'This should have basicTwoHistogramsUser chart anomalies');
    assert.ok(indicatorChartMap.singlePieHistogram.anomalyTypeFieldName.includes('abnormal_computer_accessed_remotely'), 'This should have singlePieHistogram chart anomalies');
    assert.ok(indicatorChartMap.aggregatedIndicatorWithTime.anomalyTypeFieldName.includes('AnomalyAggregatedEvent'), 'This should have aggregatedIndicatorWithTime chart anomalies');
    assert.ok(indicatorChartMap.lateralMovementIndicator.anomalyTypeFieldName.includes('VPN_user_lateral_movement'), 'This should have lateralMovementIndicator chart anomalies');
  });

});