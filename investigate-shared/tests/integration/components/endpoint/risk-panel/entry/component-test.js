import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | endpoint/risk-panel/entry', function(hooks) {
  setupRenderingTest(hooks);

  test('The risk panel populating with alerts', async function(assert) {
    this.set('entry', {
      incident: 'INC-1234',
      name: 'Autorun Unsigned In AppDataLocal Directory',
      numEvents: 1,
      risk_score: 70,
      source: 'Event Stream Analysis'
    });

    await render(hbs`{{endpoint/risk-panel/entry entry=entry}}`);
    assert.equal(findAll('.risk-properties-panel__entry__field').length, 4, '4 alert entries are displayed (except name)');
  });

  test('The risk panel populating with incident', async function(assert) {
    this.set('entry', {
      '_id': 'INC-18409',
      'name': 'RespondAlertsESA for user199',
      'priority': 'HIGH',
      'status': 'NEW',
      'alertCount': 10,
      'averageAlertRiskScore': 70,
      'assignee': 'Ian'
    });

    await render(hbs`{{endpoint/risk-panel/entry entry=entry}}`);
    assert.equal(findAll('.risk-properties-panel__entry__field').length, 6, '6 incident entries are displayed (except name)');
  });
});
