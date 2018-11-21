import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import listData from '../../../../../data/list';

module('Integration | Component | context-panel/add-to-list/list-view', function(hooks) {

  setupRenderingTest(hooks);

  test('Test to display list content in Add to List Window', async function(assert) {

    this.set('listModel', {});
    this.set('listModel', listData);
    this.set('createList', true);
    this.set('model', {});

    await this.render(hbs`{{context-panel/add-to-list/list-view createList=createList model=model entityId=entityId
    getFilteredList=listModel}}`);
    assert.equal(findAll('.rsa-form-checkbox.checked').length, 3, 'Number of lists selected');
    assert.equal(findAll('.rsa-form-checkbox ').length - this.$('.rsa-form-checkbox.checked').length, 1,
      'Number of lists unselected');
  });

  test('Test should check title for header message', async function(assert) {

    await this.render(hbs`{{context-panel/add-to-list/list-view createList=createList model=model entityId=entityId
    getFilteredList=listModel}}`);
    assert.equal(findAll('.rsa-context-tree-table__headerMessage')[0].title, 'Click on Save to update the list(s). Refresh the page to view the updates.', 'Number of lists selected');
  });
  test('Test should check for required css classes', async function(assert) {

    await this.render(hbs`{{context-panel/add-to-list/list-view createList=createList model=model entityId=entityId
    getFilteredList=listModel}}`);
    assert.equal(findAll('.rsa-context-tree-table__createList_tab')[0].innerText, 'ALL\nSELECTED\nUNSELECTED\n', 'Should display all tabs');
  });

  test('Test should check for Nav tabs title', async function(assert) {

    await this.render(hbs`{{context-panel/add-to-list/list-view createList=createList model=model entityId=entityId
    getFilteredList=listModel}}`);

    const tabSpans = findAll('.rsa-context-tree-table__createList_tab>hbox>vbox>hbox>div>span');
    assert.equal(tabSpans[0].title, 'All', 'Should have title for all tab');
    assert.equal(tabSpans[1].title, 'Selected', 'Should have title for selected tab');
    assert.equal(tabSpans[2].title, 'Unselected', 'Should have title for unselected tab');
  });
});