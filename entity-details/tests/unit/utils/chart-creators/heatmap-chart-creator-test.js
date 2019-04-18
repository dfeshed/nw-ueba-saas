import { module, test } from 'qunit';
import $ from 'jquery';

import { setupTest } from 'ember-qunit';
import heatmapChartCreator from 'entity-details/utils/chart-creators/heatmap-chart-creator';
import heatmapSettings from 'entity-details/utils/chart-settings/heatmap-settings';
import chartDataAdapter from 'entity-details/utils/chart-data-adapter';
import chartData from '../../../data/presidio/indicator-hourlyCountGroupByDayOfWeek';
import { waitUntil } from '@ember/test-helpers';

module('Unit | Utils | heatmap-chart-creator', (hooks) => {
  setupTest(hooks);

  test('it returns chart for heatmap-chart-creator', (assert) => {
    $('body').append('<div id="chartComponentPlaceholder"></div>');
    assert.expect(1);
    const settings = heatmapSettings('account_management_change_anomaly');
    const updatedSettings = chartDataAdapter(settings, chartData.data);
    const chart = heatmapChartCreator(updatedSettings);
    return waitUntil(() => $('svg').length === 2).then(() => {
      assert.ok(chart);
    });
  });
});