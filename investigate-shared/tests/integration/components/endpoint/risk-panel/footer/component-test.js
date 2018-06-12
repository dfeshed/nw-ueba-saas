import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, find } from '@ember/test-helpers';
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

  test('presence of footer Alert(s) (First 50 Results) in the risk panel', async function(assert) {
    this.set('data', data);
    this.set('activeDataSourceTab', 'ALERT');
    await render(hbs `{{endpoint/risk-panel/footer activeDataSourceTab=activeDataSourceTab data=data}}`);
    assert.equal(findAll('.risk-properties-panel__footer').length, 1, 'Risk panel footer is appearing.');
    assert.equal(find('.risk-properties-panel__footer').textContent.trim(), '0 Alert(s) (First 50 Results)', 'Footer shows the number of results.');
  });
});