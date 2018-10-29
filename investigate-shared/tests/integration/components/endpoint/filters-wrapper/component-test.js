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
  },
  {
    type: 'date',
    name: 'machine.scanStartTime',
    label: 'Scan Start Time',
    timeframes: [
      { name: 'LAST_FIVE_MINUTES', value: 5, unit: 'Minutes' },
      { name: 'LAST_TEN_MINUTES', value: 10, unit: 'Minutes' }
    ],
    filterValue: {
      value: [5],
      unit: 'Minutes'
    }
  }
];
module('filters-wrapper', 'Integration | Component | Filter Wrapper', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('It renders the rsa-data-filters', async function(assert) {
    this.set('filterState', { filter: { }, expressionList: [] });
    this.set('filterTypes', FILTER_TYPE);
    await render(hbs`{{endpoint/filters-wrapper filterState=filterState filterTypes=filterTypes}}`);
    assert.equal(findAll('.rsa-data-filters').length, 1, 'Filters Rendered');
  });

  test('apply filter getting called', async function(assert) {
    assert.expect(1);
    this.set('showSaveFilterButton', true);
    this.set('filterState', { filter: {}, expressionList: [] });
    this.set('expressionList', [{}]);
    this.set('filterTypes', FILTER_TYPE);
    this.set('getFirstPageOfFiles', function() {
      assert.ok(true);
    });
    this.set('applyFilter', function(action, filters) {
      assert.equal(filters.length, 2);
    });
    await render(hbs`{{endpoint/filters-wrapper 
    filterState=filterState 
    expressionList=expressionList 
    filterTypes=filterTypes 
    applyFilters=(action applyFilter (action getFirstPageOfFiles)) 
    showSaveFilterButton=showSaveFilterButton}}`);
    await fillIn('.file-name-input  input', 'malware.exe');
    await triggerKeyEvent('.file-name-input  input', 'keyup', 13);
  });

  test('save filter is getting called with save as option', async function(assert) {
    assert.expect(4);
    this.set('showSaveFilterButton', true);
    this.set('createCustomSearch', function(action, filters) {
      assert.equal(filters.length, 2);
    });
    this.set('applyFilter', function(filters) {
      const [filter] = filters.filterBy('propertyName', 'machine.scanStartTime');
      assert.equal(filter.propertyValues[0].relativeValueType, 'Minutes', 'Added relativeValueType for date filter');
      assert.equal(filters.length, 2);
    });
    this.set('filterState', { filter: {}, expressionList: [] });
    this.set('filterTypes', FILTER_TYPE);
    await render(hbs`{{endpoint/filters-wrapper 
    filterState=filterState 
    applyFilters=(action applyFilter) 
    filterTypes=filterTypes createCustomSearch=(action createCustomSearch) 
    showSaveFilterButton=showSaveFilterButton}}`);
    await fillIn('.file-name-input  input', 'malware.exe');
    await triggerKeyEvent('.file-name-input  input', 'keyup', 13);
    await click(document.querySelector('.save-filter-button button'));
    assert.equal(document.querySelectorAll('#modalDestination .save-search').length, 1, 'Save Filter modal rendered');
    await fillIn('.custom-filter-name  input', 'test');
    await blur('.custom-filter-name  input');
    await click(document.querySelector('.save-filter button'));
  });


  test('reset filter getting called', async function(assert) {
    assert.expect(1);
    this.set('showSaveFilterButton', true);
    this.set('filterState', { filter: {}, expressionList: [] });
    this.set('expressionList', [{}]);
    this.set('filterTypes', FILTER_TYPE);
    this.set('filterType', 'FILE');
    this.set('getFirstPageOfFiles', function() {
      assert.ok(true);
    });
    this.set('resetFilters', function(type) {
      assert.equal(type, 'FILE');
    });
    this.set('applyFilter', () => {});

    await render(hbs`{{endpoint/filters-wrapper 
    filterState=filterState 
    expressionList=expressionList 
    filterTypes=filterTypes 
    filterType=filterType
    resetFilters=(action resetFilters) 
    applyFilters=(action applyFilter) 
    showSaveFilterButton=showSaveFilterButton}}`);
    await fillIn('.file-name-input  input', 'malware.exe');
    await click('.reset-filter-button  button');
  });

  test('save as filter is getting called', async function(assert) {
    assert.expect(4);
    this.set('showSaveFilterButton', true);
    this.set('createCustomSearch', function(action, filters) {
      assert.equal(filters.length, 2);
    });
    this.set('applyFilter', function(filters) {
      const [filter] = filters.filterBy('propertyName', 'machine.scanStartTime');
      assert.equal(filter.propertyValues[0].relativeValueType, 'Minutes', 'Added relativeValueType for date filter');
      assert.equal(filters.length, 2);
    });
    this.set('filterState', { filter: {}, expressionList: [] });
    this.set('filterTypes', FILTER_TYPE);
    await render(hbs`{{endpoint/filters-wrapper 
    filterState=filterState 
    applyFilters=(action applyFilter) 
    filterTypes=filterTypes createCustomSearch=(action createCustomSearch) 
    showSaveFilterButton=showSaveFilterButton}}`);
    await fillIn('.file-name-input  input', 'malware.exe');
    await triggerKeyEvent('.file-name-input  input', 'keyup', 13);
    await click(document.querySelector('.save-as-filter-button button'));
    assert.equal(document.querySelectorAll('#modalDestination .save-search').length, 1, 'Save Filter modal rendered');
    await fillIn('.custom-filter-name  input', 'test');
    await blur('.custom-filter-name  input');
    await click(document.querySelector('.save-filter button'));
  });

  test('clicking save updates the saved filter', async function(assert) {
    assert.expect(4);
    this.set('showSaveFilterButton', true);
    this.set('createCustomSearch', function(action, filters) {
      assert.equal(filters.length, 2);
    });
    this.set('applyFilter', function(filters) {
      const [filter] = filters.filterBy('propertyName', 'machine.scanStartTime');
      assert.equal(filter.propertyValues[0].relativeValueType, 'Minutes', 'Added relativeValueType for date filter');
      assert.equal(filters.length, 2);
    });
    this.set('filterState', {
      filter: {
      },
      selectedFilter: {
        name: 'test',
        id: 1234,
        criteria: {
          expressionList: [{ type: 'text', filterOnBlur: true, name: 'size', filterValue: { operator: 'LIKE', value: ['test'] } }]
        }
      },
      expressionList: [{ type: 'text', filterOnBlur: true, name: 'size', filterValue: { operator: 'LIKE', value: ['test'] } }]
    });
    this.set('filterTypes', FILTER_TYPE);
    await render(hbs`{{endpoint/filters-wrapper 
    filterState=filterState 
    applyFilters=(action applyFilter) 
    filterTypes=filterTypes createCustomSearch=(action createCustomSearch) 
    showSaveFilterButton=showSaveFilterButton}}`);
    await fillIn('.file-name-input  input', 'malware.exe');
    await triggerKeyEvent('.file-name-input  input', 'keyup', 13);
    await click(document.querySelector('.save-filter-button button'));
    assert.equal(document.querySelectorAll('#modalDestination .save-search').length, 0, 'Save Filter modal not rendered');
  });


});
