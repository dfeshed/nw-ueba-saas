import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

const data = {
  resultList: [],
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
module('Integration | Component | endpoint/risk-panel/entry', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('presence of header time window in the risk panel', async function(assert) {
    this.set('data', data);
    this.set('activeDataSourceTab', 'ALERT');
    await render(hbs `{{endpoint/risk-panel/header activeDataSourceTab=activeDataSourceTab data=data}}`);
    assert.equal(findAll('.risk-properties-panel__header').length, 1, 'Risk panel header is appearing.');
    assert.equal(findAll('.risk-properties-panel__header span')[0].textContent.trim(), 'Time Window: 7 DAYS', 'Header shows the time window.');
    assert.equal(findAll('.risk-properties-panel__header__lastUpdated').length, 1, 'Header show last updated information.');
  });
});