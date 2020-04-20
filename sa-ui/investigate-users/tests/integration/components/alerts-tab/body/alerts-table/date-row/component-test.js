import { findAll, render, click, waitUntil } from '@ember/test-helpers';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

const alerts = [{
  id: '5090a7fc-1218-4b74-b05a-6b197601d18d',
  name: 'abnormal_ad_changes',
  startDate: 1533380400000,
  endDate: 1533384000000,
  entityType: 'User',
  entityName: 'mixed_qa_1_2',
  entityId: 'a0979b0c-7214-4a53-8114-c1552aa0952c',
  evidenceSize: 17,
  score: 97,
  severityCode: 1,
  severity: 'High',
  status: 'Open',
  feedback: 'None',
  userScoreContribution: 15,
  userScoreContributionFlag: true,
  timeframe: 'Hourly',
  dataSourceAnomalyTypePair: null
}, {
  id: '5090a7fc-1218-4b74-b05a-6b197601a18d',
  name: 'abnormal_ad_changes1',
  startDate: 1533380400000,
  endDate: 1533384000000,
  entityType: 'User',
  entityName: 'mixed_qa_1_2',
  entityId: 'a0979b0c-7214-4a53-8114-c1552aa0952c',
  evidenceSize: 17,
  score: 97,
  severityCode: 1,
  severity: 'High',
  status: 'Open',
  feedback: 'None',
  userScoreContribution: 15,
  userScoreContributionFlag: true,
  timeframe: 'Hourly',
  dataSourceAnomalyTypePair: null
}, {
  id: '5090a7fc-1218-4b74-b05a-6b197601d14d',
  name: 'abnormal_ad_changes2',
  startDate: 1533380400000,
  endDate: 1533384000000,
  entityType: 'User',
  entityName: 'mixed_qa_1_2',
  entityId: 'a0979b0c-7214-4a53-8114-c1552aa0952c',
  evidenceSize: 17,
  score: 97,
  severityCode: 1,
  severity: 'High',
  status: 'Open',
  feedback: 'None',
  userScoreContribution: 15,
  userScoreContributionFlag: true,
  timeframe: 'Hourly',
  dataSourceAnomalyTypePair: null
}];

module('Integration | Component | alerts-tab/body/alerts-table/date-row', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-users')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('it should render grouped alerts', async function(assert) {
    this.set('timestamp', 'Aug 04 2018');
    this.set('alerts', [[], alerts, [], []]);
    await render(hbs`{{alerts-tab/body/alerts-table/date-row timestamp=timestamp alerts=alerts}}`);
    assert.equal(findAll('.alerts-tab_body_body-table_body_row_date').length, 1);
  });

  test('it should render grouped alerts and should show alerts on click', async function(assert) {
    this.set('timestamp', 'Aug 04 2018');
    this.set('alerts', [[], alerts, [], []]);
    const done = assert.async();
    await render(hbs`{{alerts-tab/body/alerts-table/date-row timestamp=timestamp alerts=alerts}}`);
    click('.alerts-tab_body_body-table_body_row_date');
    return waitUntil(() => document.querySelectorAll('.alerts-tab_body_body-table_body_row_alerts').length === 0, { timeout: 30000 }).then(async() => {
      assert.equal(findAll('.alerts-tab_body_body-table_body_row_alerts').length, 0);
      assert.equal(findAll('.alerts-tab_body_body-table_body_row_alerts_alert').length, 0);
      click('.alerts-tab_body_body-table_body_row_date');
      return waitUntil(() => document.querySelectorAll('.alerts-tab_body_body-table_body_row_alerts').length === 1, { timeout: 30000 }).then(async() => {
        assert.equal(findAll('.alerts-tab_body_body-table_body_row_alerts').length, 1);
        assert.equal(findAll('.alerts-tab_body_body-table_body_row_alerts_alert').length, 3);
        done();
      });
    });
  });
});