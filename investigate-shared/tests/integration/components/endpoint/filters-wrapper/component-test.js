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

  test('save filter is getting called', async function(assert) {
    assert.expect(4);
    this.set('filterState', { filter: { }, expressionList: [] });
    this.set('filterTypes', FILTER_TYPE);
    this.set('filterType', 'FILE');
    this.set('applySavedFilters', function() {
      assert.ok(true);
    });
    this.set('getFirstPageOfFiles', function() {
      assert.ok(true);
    });
    this.set('applyFilters', function() {
      assert.ok(true);
    });
    this.set('deleteFilter', function() {
      assert.ok(true);
    });
    this.set('resetFilters', function() {
      assert.ok(true);
    });
    this.set('createCustomSearch', function() {
      assert.ok(true);
    });

    this.set('showSaveFilterButton', true);
    this.set('filterState', { filter: {}, expressionList: [] });
    this.set('expressionList', [{}]);
    await render(hbs`
    {{endpoint/filters-wrapper
      filterState=filterState
      savedFilter=savedFilter
      savedFilters=filesFilters
      selectedFilterId=selectedFilterId
      filterType='FILE'
      filterTypes=filterTypes
      resetFilters=(action resetFilters)
      applyFilters=(action applyFilters (action getFirstPageOfFiles))
      applySavedFilters=(action applySavedFilters (action getFirstPageOfFiles))
      deleteFilter=(action deleteFilter)
      createCustomSearch=(action createCustomSearch)}}`);
    assert.equal(findAll('.rsa-data-filters').length, 1, 'Filters Rendered');
    await fillIn('.file-name-input  input', 'malware.exe');
    await triggerKeyEvent('.file-name-input  input', 'keyup', 13);
    await click(document.querySelector('.save-filter-button button'));
    assert.equal(document.querySelectorAll('#modalDestination .save-search').length, 1, 'Save Filter modal rendered');
    await fillIn('.custom-filter-name  input', 'test');
    await blur('.custom-filter-name  input');
    await click(document.querySelector('.save-filter button'));

  });

});
