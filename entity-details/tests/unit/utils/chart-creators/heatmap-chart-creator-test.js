import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { waitUntil } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import heatmapChartCreator from 'entity-details/utils/chart-creators/heatmap-chart-creator';
import heatmapSettings from 'entity-details/utils/chart-settings/heatmap-settings';
import chartDataAdapter from 'entity-details/utils/chart-data-adapter';
import chartData from '../../../data/presidio/indicator-hourlyCountGroupByDayOfWeek';
import { htmlStringToElement } from 'component-lib/utils/jquery-replacement';


module('Unit | Utils | heatmap-chart-creator', (hooks) => {
  setupTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('it returns chart for heatmap-chart-creator', (assert) => {
    document.querySelector('body').append(htmlStringToElement('<div id="chartComponentPlaceholder"></div>'));
    assert.expect(1);
    const settings = heatmapSettings('account_management_change_anomaly');
    const updatedSettings = chartDataAdapter(settings, chartData.data);
    const chart = heatmapChartCreator(updatedSettings);
    return waitUntil(() => document.querySelectorAll('svg').length === 2).then(() => {
      assert.ok(chart);
    });
  });
});