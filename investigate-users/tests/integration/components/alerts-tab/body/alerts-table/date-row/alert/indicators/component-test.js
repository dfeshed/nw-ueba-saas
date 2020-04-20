import { findAll, render, click, find } from '@ember/test-helpers';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import waitForReduxStateChange from '../../../../../../../../helpers/redux-async-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

let redux;

const alert = {
  id: '5090a7fc-1218-4b74-b05a-6b197601d18d',
  name: 'abnormal_ad_changes',
  startDate: 1533380400000,
  endDate: 1533384000000,
  entityType: 'User',
  entityName: 'mixed_qa_1_2',
  entityId: 'a0979b0c-7214-4a53-8114-c1552aa0952c',
  evidences: [{
    id: '1d0ffbdc-392b-46b0-a386-30450917f446',
    entityType: 'User',
    entityTypeFieldName: null,
    entityName: 'ad_qa_1_9',
    startDate: 1534114800000,
    endDate: 1534114800000,
    retentionDate: null,
    anomalyType: 'Abnormal Active Directory Change Time',
    anomalyTypeFieldName: 'abnormal_active_directory_day_time_operation',
    name: 'Abnormal Active Directory Change Time',
    anomalyValue: '2018-08-12T23:00:00Z',
    dataEntitiesIds: [
      'active_directory'
    ],
    evidenceType: 'AnomalySingleEvent',
    score: 98,
    scoreContribution: 32,
    severity: 'Critical',
    top3eventsJsonStr: null,
    top3events: null,
    numOfEvents: 1,
    timeframe: 'Hourly',
    supportingInformation: null
  }, {
    id: '574c802c-e4fa-4686-a31e-a35380916ec8',
    entityType: 'User',
    entityTypeFieldName: null,
    entityName: 'ad_qa_1_9',
    startDate: 1534114800000,
    endDate: 1534118400000,
    retentionDate: null,
    anomalyType: 'Multiple Active Directory Object Changes',
    anomalyTypeFieldName: 'high_number_of_successful_object_change_operations',
    name: 'Multiple Active Directory Object Changes',
    anomalyValue: '16.0',
    dataEntitiesIds: [
      'active_directory'
    ],
    evidenceType: 'AnomalyAggregatedEvent',
    score: 54,
    scoreContribution: 16,
    severity: 'Critical',
    top3eventsJsonStr: null,
    top3events: null,
    numOfEvents: 16,
    timeframe: 'Hourly',
    supportingInformation: null
  }],
  evidenceSize: 2,
  score: 97,
  severityCode: 1,
  severity: 'High',
  status: 'Open',
  feedback: 'None',
  userScoreContribution: 15,
  userScoreContributionFlag: true,
  timeframe: 'Hourly',
  dataSourceAnomalyTypePair: null
};

module('Integration | Component | alerts-tab/body/alerts-table/date-row/alert/indicators', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-users')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    redux = this.owner.lookup('service:redux');
  });

  test('it should render indicators', async function(assert) {
    this.set('alert', alert);
    await render(hbs`{{alerts-tab/body/alerts-table/date-row/alert/indicators alert=alert}}`);
    assert.equal(findAll('.rsa-data-table-body-row').length, 2);
    assert.equal(findAll('.anomalyTypeFieldName').length, 2);
    assert.equal(find('.anomalyTypeFieldName').innerText, 'Abnormal Active Directory Change Time');
    assert.equal(findAll('.rsa-data-table-body-cell').length, 8);
  });
  test('it should intiate user on row click', async function(assert) {
    this.set('alert', alert);
    const done = assert.async();
    await render(hbs`{{alerts-tab/body/alerts-table/date-row/alert/indicators alert=alert}}`);
    click('.rsa-data-table-body-row');
    const select = waitForReduxStateChange(redux, 'user.indicatorId');
    return select.then(() => {
      const state = redux.getState();
      assert.equal(state.user.userId, 'a0979b0c-7214-4a53-8114-c1552aa0952c');
      assert.equal(state.user.alertId, '5090a7fc-1218-4b74-b05a-6b197601d18d');
      assert.equal(state.user.indicatorId, '1d0ffbdc-392b-46b0-a386-30450917f446');
      done();
    });
  });
});