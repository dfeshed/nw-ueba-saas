import { module, test } from 'qunit';
import $ from 'jquery';

import { setupTest } from 'ember-qunit';
import columnChartCreator from 'entity-details/utils/chart-creators/column-chart-creator';
import activityTimeAnomalySettings from 'entity-details/utils/chart-settings/activity-time-anomaly-settings';
import chartDataAdapter from 'entity-details/utils/chart-data-adapter';
import chartData from '../../../data/presidio/indicator-hourlyCountGroupByDayOfWeek';
import { waitUntil } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Unit | Utils | column-chart-creator', (hooks) => {
  setupTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('it returns chart for column-chart-creator', (assert) => {
    $('body').append('<div id="chartComponentPlaceholder"></div>');
    assert.expect(1);
    const settings = activityTimeAnomalySettings('account_management_change_anomaly');
    const updatedSettings = chartDataAdapter(settings, chartData.data);
    const chart = columnChartCreator(updatedSettings);
    return waitUntil(() => $('svg').length === 2).then(() => {
      assert.ok(chart);
    });
  });
});