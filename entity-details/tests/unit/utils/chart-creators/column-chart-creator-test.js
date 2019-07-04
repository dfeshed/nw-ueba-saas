import { module, test } from 'qunit';

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
    const divObj = document.createElement('div');
    divObj.id = 'chartComponentPlaceholder';
    document.getElementsByTagName('body')[0].appendChild(divObj);
    assert.expect(1);
    const settings = activityTimeAnomalySettings('account_management_change_anomaly');
    const updatedSettings = chartDataAdapter(settings, chartData.data);
    const entityObj = { entityType: 'User', entityName: 'Name1', dataEntitiesIds: 'File' };
    const chart = columnChartCreator(updatedSettings, entityObj, 'BrokerId');
    return waitUntil(() => document.getElementsByTagName('svg').length === 2).then(() => {
      assert.ok(chart);
    });
  });
});