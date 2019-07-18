import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { waitUntil } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import pieChartCreator from 'entity-details/utils/chart-creators/pie-chart-creator';
import singlePieSettings from 'entity-details/utils/chart-settings/single-pie-settings';
import chartDataAdapter from 'entity-details/utils/chart-data-adapter';
import chartData from '../../../data/presidio/indicator-count';
import { htmlStringToElement } from 'component-lib/utils/jquery-replacement';

module('Unit | Utils | pie-chart-creator', (hooks) => {
  setupTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('it returns chart for pie-chart-creator', (assert) => {
    document.querySelector('body').append(htmlStringToElement('<div id="chartComponentPlaceholder"></div>'));
    assert.expect(1);
    const settings = singlePieSettings('account_management_change_anomaly');
    const updatedSettings = chartDataAdapter(settings, chartData.data);
    const chart = pieChartCreator(updatedSettings);
    return waitUntil(() => document.getElementById('chartComponentPlaceholderLegend') != null).then(() => {
      assert.ok(chart);
    });
  });
});