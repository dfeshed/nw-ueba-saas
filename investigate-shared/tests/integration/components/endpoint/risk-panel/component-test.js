import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

const DATA = [
  {
    incident: 'INC-1234',
    name: 'Autorun Unsigned In AppDataLocal Directory',
    numEvents: 1,
    risk_score: 70,
    source: 'Event Stream Analysis'
  },
  {
    incident: 'INC-1234',
    name: 'Autorun Unsigned In AppDataLocal Directory',
    numEvents: 1,
    risk_score: 70,
    source: 'Event Stream Analysis'
  }
];

module('Integration | Component | endpoint/risk-panel', function(hooks) {
  setupRenderingTest(hooks);

  test('The risk panel populating with alerts and incidents', async function(assert) {
    this.set('data', DATA);
    this.set('activePropertyTab', 'ALERT');
    await render(hbs`{{endpoint/risk-panel activeDataSourceTab=activePropertyTab data=data}}`);
    assert.equal(findAll('.risk-properties-panel__title').length, 2, '2 Incident entries are present');
    assert.equal(find('.risk-properties-panel__title h3').textContent.trim(), 'Autorun Unsigned In AppDataLocal Directory');
  });
});
