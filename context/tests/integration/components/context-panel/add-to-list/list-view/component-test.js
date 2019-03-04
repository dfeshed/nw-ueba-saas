import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { find, findAll, render, triggerEvent } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import listData from '../../../../../data/list';


module('Integration | Component | context-panel/add-to-list/list-view', function(hooks) {

  setupRenderingTest(hooks);

  test('Test to display list content in Add to List Window', async function(assert) {

    this.set('listModel', listData);
    this.set('createList', true);
    this.set('model', {});

    await render(hbs`{{context-panel/add-to-list/list-view createList=createList model=model entityId=entityId
    getFilteredList=listModel}}`);

    assert.equal(findAll('.rsa-form-checkbox.checked').length, 3, 'Number of lists selected');
    assert.equal(findAll('.rsa-form-checkbox ').length - this.$('.rsa-form-checkbox.checked').length, 1,
      'Number of lists unselected');
  });

  test('Test should check title for header message', async function(assert) {

    await render(hbs`{{context-panel/add-to-list/list-view createList=createList model=model entityId=entityId
    getFilteredList=listModel}}`);
    assert.equal(findAll('.rsa-context-tree-table__headerMessage')[0].title, 'Click on Save to update the list(s). Refresh the page to view the updates.', 'Number of lists selected');
  });

  test('Test should check for required css classes', async function(assert) {

    await render(hbs`{{context-panel/add-to-list/list-view createList=createList model=model entityId=entityId
    getFilteredList=listModel}}`);
    assert.equal(findAll('.rsa-context-tree-table__createList_tab')[0].innerText, 'ALL\nSELECTED\nUNSELECTED\n', 'Should display all tabs');
  });

  test('Test should check for Nav tabs title', async function(assert) {

    await render(hbs`{{context-panel/add-to-list/list-view createList=createList model=model entityId=entityId
    getFilteredList=listModel}}`);

    const tabSpans = findAll('.rsa-context-tree-table__createList_tab>hbox>vbox>hbox>div>span');
    assert.equal(tabSpans[0].title, 'All', 'Should have title for all tab');
    assert.equal(tabSpans[1].title, 'Selected', 'Should have title for selected tab');
    assert.equal(tabSpans[2].title, 'Unselected', 'Should have title for unselected tab');
  });

  test('Filter results for list should filter lists based on list name', async function(assert) {
    const model = {
      'list': listData,
      'filterStr': 'list4'
    };

    this.set('model', model);

    await render(hbs`{{context-panel/add-to-list/list-view model=model}}`);

    assert.equal(findAll('.rsa-form-checkbox').length, 1, '1 filtered list is displayed');
  });

  test('Test should select list in list view', async function(assert) {
    const model = {
      'list': listData
    };

    this.set('model', model);

    await render(hbs`{{context-panel/add-to-list/list-view model=model}}`);

    assert.equal(findAll('.rsa-form-checkbox.checked').length, 3, 'Number of lists selected');

    const input = find('.rsa-context-tree-table__checkbox input');

    input.checked = true;

    await triggerEvent(input, 'change');

    assert.equal(findAll('.rsa-form-checkbox.checked').length, 4, 'Number of lists selected');

  });

});
