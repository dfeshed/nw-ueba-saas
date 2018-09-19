import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, findAll, click, fillIn, blur, triggerKeyEvent } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
const FILTER_TYPE = [
  {
    'name': 'pe.resources.company',
    'label': 'investigateFiles.fields.companyName',
    'type': 'text'
  }
];
module('filters-wrapper', 'Integration | Component | Filter Wrapper', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('It renders the rsa-data-filters', async function(assert) {
    this.set('filterState', { filter: {} });
    this.set('filterTypes', FILTER_TYPE);
    await render(hbs`{{endpoint/filters-wrapper filterState=filterState filterTypes=filterTypes}}`);
    assert.equal(findAll('.rsa-data-filters').length, 1, 'Filters Rendered');
  });

  test('It shows save filter modal on clicking the save button', async function(assert) {
    this.set('showSaveFilterButton', true);
    this.set('filterState', { filter: {} });
    this.set('filterTypes', FILTER_TYPE);
    await render(hbs`{{endpoint/filters-wrapper filterState=filterState filterTypes=filterTypes showSaveFilterButton=showSaveFilterButton}}`);
    await click(document.querySelector('.save-filter-button button'));
    assert.equal(document.querySelectorAll('#modalDestination .save-search').length, 1, 'Save Filter modal rendered');
  });

  test('apply filter getting called', async function(assert) {
    assert.expect(1);
    this.set('showSaveFilterButton', true);
    this.set('filterState', { filter: {} });
    this.set('filterTypes', FILTER_TYPE);
    this.set('getFirstPageOfFiles', function() {
      assert.ok(true);
    });
    this.set('applyFilter', function(action, filters) {
      assert.equal(filters.length, 1);
    });
    await render(hbs`{{endpoint/filters-wrapper filterState=filterState filterTypes=filterTypes applyFilters=(action applyFilter (action getFirstPageOfFiles)) showSaveFilterButton=showSaveFilterButton}}`);
    await fillIn('.file-name-input  input', 'malware.exe');
    await blur('.file-name-input  input');
    await triggerKeyEvent('.file-name-input  input', 'keyup', 13);
  });

  test('save filter is getting called', async function(assert) {
    assert.expect(2);
    this.set('showSaveFilterButton', true);
    this.set('createCustomSearch', function(action, filters) {
      assert.equal(filters.length, 1);
    });
    this.set('applyFilter', function(filters) {
      assert.equal(filters.length, 1);
    });
    this.set('filterState', { filter: {} });
    this.set('filterTypes', FILTER_TYPE);
    await render(hbs`{{endpoint/filters-wrapper filterState=filterState applyFilters=(action applyFilter) filterTypes=filterTypes createCustomSearch=(action createCustomSearch) showSaveFilterButton=showSaveFilterButton}}`);
    await fillIn('.file-name-input  input', 'malware.exe');
    await blur('.file-name-input  input');
    await triggerKeyEvent('.file-name-input  input', 'keyup', 13);
    await click(document.querySelector('.save-filter-button button'));
    await fillIn('.custom-filter-name  input', 'test');
    await blur('.custom-filter-name  input');
    await click(document.querySelector('.save-filter button'));

  });

});
