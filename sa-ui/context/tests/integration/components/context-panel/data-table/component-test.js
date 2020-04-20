import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { click, findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import dataSourceDetails from 'context/config/im-incidents';

const contextData = { resultList: [
  {
    'id': 'INC-230',
    'alertCount': 5,
    'averageAlertRiskScore': 90,
    'created': {
      '$date': '2016-02-09T04:17:06.156Z'
    },
    'name': 'High Risk Alerts: Reporting Engine for 90.0',
    'priority': 'CRITICAL',
    'status': 'NEW'
  },
  {
    'id': 'INC-134',
    'alertCount': 240,
    'averageAlertRiskScore': 90,
    'created': {
      '$date': '2016-02-05T04:45:06.206Z'
    },
    'name': 'High Risk Alerts: Reporting Engine for 90.0',
    'priority': 'CRITICAL',
    'status': 'NEW'
  },
  {
    'id': 'INC-132',
    'alertCount': 600,
    'averageAlertRiskScore': 90,
    'created': {
      '$date': '2016-02-04T06:01:39.644Z'
    },
    'name': 'High Risk Alerts: Reporting Engine for 90.0',
    'priority': 'CRITICAL',
    'status': 'NEW'
  }
] };

module('Integration | Component | context-panel/data-table', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function(assert) {

    this.set('contextData', contextData);
    this.set('dataSourceDetails', dataSourceDetails);
    await render(hbs`{{context-panel/data-table contextData=contextData dataSourceDetails=dataSourceDetails}}`);

    assert.equal(findAll('.rsa-data-table-header-cell').length, 8, 'Testing count of incidents header cells');
  });

  test('sorting the data table column should display sorted data', async function(assert) {

    this.set('contextData', contextData);
    this.set('dataSourceDetails', dataSourceDetails);

    await render(hbs`{{context-panel/data-table contextData=contextData dataSourceDetails=dataSourceDetails}}`);

    assert.equal(findAll('.rsa-context-panel__context-data-table.link')[0].textContent.trim(), 'INC-230', 'before sorting in ascending order');

    await click(findAll('.rsa-data-table-header-cell .rsa-icon')[3]);

    assert.equal(findAll('.rsa-context-panel__context-data-table.link')[0].textContent.trim(), 'INC-132', 'after sorting in ascending order');
  });
});
