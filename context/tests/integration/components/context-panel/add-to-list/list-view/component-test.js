import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import listData from '../../../../../data/list';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { revertPatch } from '../../../../../helpers/patch-reducer';
import Immutable from 'seamless-immutable';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';

let setState;
module('Integration | Component | context-panel/add-to-list/list-view', function(hooks) {

  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    initialize(this.owner);
  });

  hooks.afterEach(function() {
    revertPatch();
  });

  const entity = {
    type: 'IP',
    id: '10.10.10.10'
  };

  test('Test to display list content in Add to List Window', async function(assert) {
    this.set('filterStr', null);

    this.set('listModel', {});
    this.set('listModel', listData);
    this.set('createList', true);
    this.set('model', {});
    new ReduxDataHelper(setState)
      .setListData(listData)
      .setListView(true)
      .setEntityType(entity)
      .build();

    await render(hbs`{{context-panel/add-to-list/list-view filterStr=filterStr}}`);

    assert.equal(findAll('.rsa-form-checkbox.checked').length, 3, 'Number of lists selected');
    assert.equal(findAll('.rsa-form-checkbox ').length - this.$('.rsa-form-checkbox.checked').length, 1,
      'Number of lists unselected');
  });


  test('Test should check title for header message', async function(assert) {
    this.set('filterStr', null);

    new ReduxDataHelper(setState)
      .setListData(listData)
      .setListView(true)
      .setEntityType(entity)
      .build();

    await render(hbs`{{context-panel/add-to-list/list-view filterStr=filterStr}}`);
    assert.equal(findAll('.rsa-context-tree-table__headerMessage')[0].title, 'Click on Save to update the list(s). Refresh the page to view the updates.', 'Number of lists selected');
  });

  test('Test should check for required css classes', async function(assert) {
    this.set('filterStr', null);

    new ReduxDataHelper(setState)
      .setListData(listData)
      .setListView(true)
      .setEntityType(entity)
      .build();
    await render(hbs`{{context-panel/add-to-list/list-view filterStr=filterStr}}`);
    assert.equal(findAll('.rsa-context-tree-table__createList_tab')[0].innerText, 'ALL\nSELECTED\nUNSELECTED\n', 'Should display all tabs');
  });

  test('Test should check for Nav tabs title', async function(assert) {

    this.set('filterStr', null);

    new ReduxDataHelper(setState)
      .setListData(listData)
      .setListView(true)
      .setEntityType(entity)
      .build();
    await render(hbs`{{context-panel/add-to-list/list-view filterStr=filterStr}}`);

    const tabSpans = findAll('.rsa-context-tree-table__createList_tab>hbox>vbox>hbox>div>span');
    assert.equal(tabSpans[0].title, 'All', 'Should have title for all tab');
    assert.equal(tabSpans[1].title, 'Selected', 'Should have title for selected tab');
    assert.equal(tabSpans[2].title, 'Unselected', 'Should have title for unselected tab');
  });

  test('Filter results for list should filter lists based on list name', async function(assert) {
    this.set('filterStr', 'list4');

    new ReduxDataHelper(setState)
      .setListData(listData)
      .setListView(true)
      .setEntityType(entity)
      .build();
    await render(hbs`{{context-panel/add-to-list/list-view filterStr=filterStr}}`);

    assert.equal(findAll('.rsa-form-checkbox').length, 1, '1 filtered list is displayed');
  });

});