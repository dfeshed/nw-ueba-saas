import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

const DATA = {
  resultList: [
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
      risk_score: 80,
      source: 'Event Stream Analysis'
    }
  ],
  resultMeta: {
    'timeQuerySubmitted': 1528701812188,
    'sorting.sortOrder[1].propertyName': 'prioritySort',
    'sorting.sortOrder[1].direction': 'DESC',
    'sorting.sortOrder[0].direction': 'DESC',
    'timeFilter.timeUnit': 'DAY',
    'root': null,
    'timeFilter.propertyName': 'receivedTime',
    'timeFilter.timeUnitCount': 7,
    'limit': 50,
    'timeFilter.absoluteStartTime': true,
    'sorting.sortOrder[0].propertyName': 'created',
    'dataSourceCreatedBy': 'admin'
  }

};

module('Integration | Component | endpoint/risk-panel', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('The risk panel populating with alerts and incidents', async function(assert) {
    this.set('data', DATA);
    this.set('activePropertyTab', 'ALERT');
    await render(hbs`{{endpoint/risk-panel activeDataSourceTab=activePropertyTab data=data}}`);
    assert.equal(findAll('.risk-properties-panel__content__title').length, 2, '2 Incident entries are present');
    assert.equal(find('.risk-properties-panel__content__title h3').textContent.trim(), 'Autorun Unsigned In AppDataLocal Directory');
  });

  test('The risk panel populating with alerts and incidents with filter', async function(assert) {
    this.set('data', DATA);
    this.set('activePropertyTab', 'ALERT');
    this.set('range', [0, 70]);
    await render(hbs`{{endpoint/risk-panel activeDataSourceTab=activePropertyTab data=data filterRange=range}}`);
    assert.equal(findAll('.risk-properties-panel__content__title').length, 1, '1 Incident entry is present');
  });
});
