import { module, test } from 'qunit';
import $ from 'jquery';
import { setupTest } from 'ember-qunit';
import pieChartCreator from 'entity-details/utils/chart-creators/pie-chart-creator';
import singlePieSettings from 'entity-details/utils/chart-settings/single-pie-settings';
import chartDataAdapter from 'entity-details/utils/chart-data-adapter';
import chartData from '../../../data/presidio/indicator-count';
import { waitUntil } from '@ember/test-helpers';

module('Unit | Utils | pie-chart-creator', (hooks) => {
  setupTest(hooks);

  test('it returns chart for pie-chart-creator', (assert) => {
    $('body').append('<div id="chartdiv"></div>');
    assert.expect(1);
    const settings = singlePieSettings('account_management_change_anomaly');
    const updatedSettings = chartDataAdapter(settings, chartData.data);
    const chart = pieChartCreator(updatedSettings);
    return waitUntil(() => $('svg').length === 2).then(() => {
      assert.ok(chart);
    });
  });
});