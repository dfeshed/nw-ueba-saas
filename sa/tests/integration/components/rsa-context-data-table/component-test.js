import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import DatasourceList from 'sa/context/datasource-list';


moduleForComponent('rsa-context-data-table', 'Integration | Component | rsa context data table', {
  integration: true
});

test('it renders', function(assert) {

  let columns = DatasourceList.pop();


  let incidents = [{ 'resultList': [
    {
      '_id': 'INC-230',
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
      '_id': 'INC-134',
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
      '_id': 'INC-132',
      'alertCount': 600,
      'averageAlertRiskScore': 90,
      'created': {
        '$date': '2016-02-04T06:01:39.644Z'
      },
      'name': 'High Risk Alerts: Reporting Engine for 90.0',
      'priority': 'CRITICA',
      'status': 'NEW'
    }
  ] }];

  this.set('incidents', incidents);
  this.set('columns', columns);
  this.render(hbs`  {{rsa-context-data-table data=incidents   title=(t columns.title ) modulesColumnListConfig=columns.columns headerdata=columns.header footerdata=columns.footer}}`);
  assert.equal(this.$('.rsa-data-table-header-cell').length, 8, 'Testing count of incidents header cells');


});
