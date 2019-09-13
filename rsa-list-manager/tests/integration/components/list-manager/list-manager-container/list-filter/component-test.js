import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, click, find } from '@ember/test-helpers';
import { typeInSearch } from 'ember-power-select/test-support/helpers';
import { setupRenderingTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';

let setState;

module('Integration | Component | list filter', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  const originalList = [ { id: '1', name: 'foo' }, { id: '2', name: 'bar' }];
  const listLocation1 = 'listManager';
  const listName1 = 'List of Things';

  test('Filters list with default filtering', async function(assert) {
    new ReduxDataHelper(setState).listName(listName1).build();
    this.set('listLocation', listLocation1);
    this.set('originalList', originalList);
    this.set('updateFilteredList', (updatedList) => {
      this.set('filteredList', updatedList);
    });

    await render(hbs`{{list-manager/list-manager-container/list-filter
      listLocation=listLocation
      originalList=originalList
      updateFilteredList=updateFilteredList
    }}`);

    assert.ok(find('.list-filter'), 'list filter component found');
    assert.ok(find('.list-filter .rsa-icon-filter-2-filled'), 'filter icon found');
    assert.equal(find('.list-filter input').getAttribute('placeholder'), 'Filter list of things');

    await click(find('.list-filter input'));
    await typeInSearch('b');

    assert.equal(this.get('filteredList').length, 1, 'One of 2 items filtered out');
    assert.equal(this.get('filteredList')[0].name, 'bar');

    await click(find('.list-filter input'));
    await typeInSearch('bo');

    assert.equal(this.get('filteredList').length, 0, 'Everything filtered out');
  });

  test('clear filter resets the filter input, results, highlightedIndex', async function(assert) {
    new ReduxDataHelper(setState).listName(listName1).build();
    this.set('originalList', originalList);
    this.set('listLocation', listLocation1);
    this.set('updateFilteredList', (updatedList) => {
      this.set('filteredList', updatedList);
    });
    this.set('resetHighlightedIndex', () => {
      assert.ok(true, 'resetHighlightedIndex when clearing filter');
      this.set('highlightedIndex', -1);
    });

    await render(hbs`{{list-manager/list-manager-container/list-filter
      listLocation=listLocation
      originalList=originalList
      updateFilteredList=updateFilteredList
      resetHighlightedIndex=resetHighlightedIndex}}`);

    assert.notOk(find('.list-filter .clear-filter'), 'Clear filter not found when input is clear');

    const filterInput = find('.list-filter input');
    await click(filterInput);
    await typeInSearch('b');

    assert.equal(filterInput.value, 'b');
    assert.ok(find('.list-filter .clear-filter'), 'Clear filter option found when filter input has text');

    assert.equal(this.get('filteredList').length, 1, 'One of 2 items filtered out');

    await click(find('.list-filter .clear-filter button'));
    assert.equal(filterInput.value, '', 'Filter input cleared');
    assert.equal(this.get('filteredList').length, 2, 'filter cleared to render original list back');
    const state1 = this.owner.lookup('service:redux').getState();
    assert.equal(state1.listManager.highlightedIndex, -1, 'highlightedIndex shall be reset');
  });

  test('Filters list with custom filtering', async function(assert) {
    new ReduxDataHelper(setState).listName(listName1).build();
    assert.expect(3);
    this.set('originalList', originalList);
    this.set('listLocation', listLocation1);
    this.set('filterAction', (value) => {
      assert.ok(true, 'Custom function called when passed');
      return this.get('originalList').filter((item) => item.name.toLowerCase().includes(value.toLowerCase()));
    });
    this.set('updateFilteredList', (updatedList) => {
      this.set('filteredList', updatedList);
    });

    await render(hbs`{{list-manager/list-manager-container/list-filter
      listLocation=listLocation
      originalList=originalList
      filterAction=filterAction
      updateFilteredList=updateFilteredList
    }}`);

    await click(find('.list-filter input'));
    await typeInSearch('f');

    assert.equal(this.get('filteredList').length, 1, 'One of 2 items filtered out');
    assert.equal(this.get('filteredList')[0].name, 'foo');
  });

  test('highlightedIndex is reset when filter is in focus', async function(assert) {
    new ReduxDataHelper(setState).listName(listName1).build();
    this.set('originalList', originalList);
    this.set('listLocation', listLocation1);
    this.set('filterAction', (value) => {
      assert.ok(true, 'Custom function called when passed');
      return this.get('originalList').filter((item) => item.name.toLowerCase().includes(value.toLowerCase()));
    });
    this.set('updateFilteredList', (updatedList) => {
      this.set('filteredList', updatedList);
    });

    await render(hbs`{{list-manager/list-manager-container/list-filter
      listLocation=listLocation
      originalList=originalList
      resetHighlightedIndex=resetHighlightedIndex
      filterAction=filterAction
      updateFilteredList=updateFilteredList
    }}`);

    await click(find('.list-filter input'));
    const state1 = this.owner.lookup('service:redux').getState();
    assert.equal(state1.listManager.highlightedIndex, -1, 'highlightedIndex shall be reset');
  });

  test('highlightedIndex is reset when filterText changes', async function(assert) {
    new ReduxDataHelper(setState).listName(listName1).build();
    this.set('originalList', originalList);
    this.set('listLocation', listLocation1);
    this.set('filterAction', (value) => {
      assert.ok(true, 'Custom function called when passed');
      return this.get('originalList').filter((item) => item.name.toLowerCase().includes(value.toLowerCase()));
    });
    this.set('updateFilteredList', (updatedList) => {
      this.set('filteredList', updatedList);
    });

    await render(hbs`{{list-manager/list-manager-container/list-filter
      listLocation=listLocation
      originalList=originalList
      filterText=filterText
      resetHighlightedIndex=resetHighlightedIndex
      filterAction=filterAction
      updateFilteredList=updateFilteredList
    }}`);

    await click(find('.list-filter input'));
    const state1 = this.owner.lookup('service:redux').getState();
    assert.equal(state1.listManager.highlightedIndex, -1, 'highlightedIndex shall be reset');
    await typeInSearch('a');
    assert.equal(this.get('filterText'), 'a', 'filterText shall be set correctly');
    const state2 = this.owner.lookup('service:redux').getState();
    assert.equal(state2.listManager.highlightedIndex, -1, 'highlightedIndex shall be reset');
    await typeInSearch('an');
    assert.equal(this.get('filterText'), 'an', 'filterText shall be set correctly');
    const state3 = this.owner.lookup('service:redux').getState();
    assert.equal(state3.listManager.highlightedIndex, -1, 'highlightedIndex shall be reset');
  });
});
