import { module, test } from 'qunit';
import { click, find, findAll, render } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import { clickTrigger, selectChoose } from '../../../../helpers/ember-power-select';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | Respond Explorer Filters', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.lookup('service:dateFormat').set('selected', 'MM/dd/yyyy');
    this.owner.lookup('service:timeFormat').set('selected', 'HR12');
  });

  test('The Explorer Filters component renders to the DOM', async function(assert) {
    await render(hbs`{{rsa-explorer/explorer-filters}}`);
    assert.equal(findAll('.explorer-filters').length, 1, 'The Explorer Filters component should be found in the DOM');
    assert.equal(find('.ember-power-select-selected-item').textContent.trim(), 'All Data', 'The default is to show the time range dropdown with All Data selected');
    assert.equal(findAll('input.flatpickr-input').length, 0, 'When hasCustomDate is false, no date picker inputs appear');
  });

  test('Setting the timeframeFilter appropriately selects the time range from the dropdown', async function(assert) {
    this.set('timerange', { name: 'LAST_HOUR' });
    await render(hbs`{{rsa-explorer/explorer-filters timeframeFilter=timerange}}`);
    assert.equal(find('.ember-power-select-selected-item').textContent.trim(), 'Last Hour', 'When the timeframeFilter is supplied with a name, it selects the appropriate items from the dropdown');
  });

  test('Setting the hasCustomDate to true replaces the time range dropdown with two date inputs', async function(assert) {
    await render(hbs`{{rsa-explorer/explorer-filters hasCustomDate=true}}`);
    assert.equal(findAll('.ember-power-select-selected-item').length, 0, 'No power selected item appears when hasCustomDate is true');
    assert.equal(findAll('input.flatpickr-input').length, 2, 'When hasCustomDate is true, two inputs appear for the date picker');
  });

  test('Changing the time range dropdown fires an updateFilter action', async function(assert) {
    assert.expect(2);
    this.set('updateFilter', (selectedTimeRange) => {
      assert.ok(selectedTimeRange.created && selectedTimeRange.created.name);
    });
    await render(hbs`{{rsa-explorer/explorer-filters updateFilter=updateFilter}}`);
    const selector = '.filter-option.created-filter';
    clickTrigger(selector);
    assert.equal(findAll('.ember-power-select-options li.ember-power-select-option').length, 15, 'There are 15 time range options available in the dropdown');
    selectChoose(selector, '.ember-power-select-option', 1);
  });

  test('Dates appear as expected in the date picker inputs', async function(assert) {
    const start = 1427958061000; // Human time (GMT): Thursday, April 2, 2015 7:01:01 AM (12:01:01 AM Pacific)
    this.set('timerange', { start, end: null });
    await render(hbs`{{rsa-explorer/explorer-filters hasCustomDate=true timeframeFilter=timerange}}`);
    assert.equal(find('input.flatpickr-input:first-of-type').value, '04/02/2015 12:01:01 AM', 'The expected date appears in the start input');
  });

  test('Reset Filters executes as expected', async function(assert) {
    assert.expect(1);
    this.set('reset', () => {
      assert.ok(true);
    });
    await render(hbs`{{rsa-explorer/explorer-filters resetFilters=reset}}`);
    await click('footer button');
  });
});
