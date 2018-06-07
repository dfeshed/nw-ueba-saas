import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | endpoint/risk-panel/entry', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    this.owner.lookup('service:dateFormat').set('selected', 'MM/dd/yyyy');
    this.owner.lookup('service:timeFormat').set('selected', 'HR12');
    this.owner.lookup('service:timezone').set('selected', { zoneId: 'UTC' });
  });

  test('The risk panel populating with alerts', async function(assert) {
    this.set('entry', {
      incident: 'INC-1234',
      timestamp: {
        '$date': '2018-05-17T04: 06: 22.000Z'
      },
      name: 'Autorun Unsigned In AppDataLocal Directory',
      numEvents: 1,
      risk_score: 70,
      source: 'Event Stream Analysis'
    });
    this.set('type', 'ALERT');
    await render(hbs`{{endpoint/risk-panel/entry entry=entry type=type}}`);
    assert.equal(findAll('.risk-properties-panel__entry__field').length, 5, '5 alert entries are displayed (except name)');
    assert.equal(find('.risk-properties-panel__entry__field:first-of-type .entry-label').textContent, 'Created', 'First field is Created');
  });

  test('The risk panel populating with incident', async function(assert) {
    this.set('entry', {
      '_id': 'INC-18409',
      'name': 'RespondAlertsESA for user199',
      'priority': 'HIGH',
      'status': 'NEW',
      'alertCount': 10,
      'averageAlertRiskScore': 70,
      'assignee': 'Ian',
      'created': {
        '$date': '2018-05-17T04: 06: 27.574Z'
      }
    });
    this.set('type', 'INCIDENT');
    await render(hbs`{{endpoint/risk-panel/entry entry=entry type=type}}`);
    assert.equal(findAll('.risk-properties-panel__entry__field').length, 7, '7 incident entries are displayed (except name)');
    assert.equal(find('.risk-properties-panel__entry__field:first-of-type .entry-label').textContent, 'Created', 'First field is Created');
  });
});
