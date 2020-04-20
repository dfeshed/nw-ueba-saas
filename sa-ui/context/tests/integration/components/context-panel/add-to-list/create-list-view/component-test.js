import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { click, find, findAll, fillIn, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

import listData from '../../../../../data/list';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { revertPatch } from '../../../../../helpers/patch-reducer';
import Immutable from 'seamless-immutable';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';

let setState;
module('Integration | Component | context-panel/add-to-list/create-list-view', function(hooks) {

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

  test('it renders', async function(assert) {

    new ReduxDataHelper(setState)
      .setListView(false)
      .build();

    await render(hbs`{{context-panel/add-to-list/create-list-view}}`);
    assert.equal(find('.rsa-context-tree-table__createList').textContent.replace(/\s/g, ''), 'CreateNewListListNameDescriptionCancelSave');
  });

  test('List name should not be empty', async function(assert) {
    new ReduxDataHelper(setState)
      .setListView(false)
      .build();

    await render(hbs`{{context-panel/add-to-list/create-list-view}}`);
    await fillIn('.rsa-context-tree-table__createList input[type=text]', '');
    await click(findAll('.rsa-form-button')[1]);

    assert.equal(find('.input-error').textContent.trim(), 'Enter valid list name (Max length is 255 characters).', 'list name should not be empty');
  });

  test('List name should not be more than 255 characters', async function(assert) {
    new ReduxDataHelper(setState)
      .setListView(false)
      .build();
    const input = 'abcfdsfoisfhoisghosdihudofhsdguhsgufghsoguhsgosfhgoughsgoufghoughsoguhfoughsfgouhsgouhfsogshgohgsghdsoghsdoghsogihsgoighsoihgsoighso' +
      'foughsfgouhsgouhsdgousghsoughgndnofhdsoufsogshgohgsghdsoghsdoghsogihsgoighsoihgsoighsofoughsfgouhsgouhsdgousghsoughgndnofhdsou';

    await render(hbs`{{context-panel/add-to-list/create-list-view model=model}}`);
    await fillIn('.rsa-context-tree-table__createList input[type=text]', input);
    await click(findAll('.rsa-form-button')[1]);
    assert.equal(find('.input-error').textContent.trim(), 'Enter valid list name (Max length is 255 characters).');
  });

  test('Duplicate List name should not allowed', async function(assert) {
    new ReduxDataHelper(setState)
      .setListView(false)
      .setListData(listData)
      .build();

    await render(hbs`{{context-panel/add-to-list/create-list-view}}`);
    await fillIn('.rsa-context-tree-table__createList input[type=text]', 'list4');
    assert.ok(findAll('.rsa-form-button')[1].disabled, 'save button should be disabled');
    assert.equal(find('.input-error').textContent.trim(), 'List name already exists!');
  });

  test('while creating list cancel button should cancel the list creation', async function(assert) {
    new ReduxDataHelper(setState)
      .setListView(false)
      .build();

    await render(hbs`{{context-panel/add-to-list/create-list-view}}`);
    await fillIn('.rsa-context-tree-table__createList input[type=text]', 'test-list3');
    await click(findAll('.rsa-form-button')[0]);
    assert.equal(find('.rsa-context-tree-table__createList input[type=text]').placeholder, 'Enter List Name', 'should reset the properties of list');
  });

  test('disabled button on errorMessage', async function(assert) {
    new ReduxDataHelper(setState)
      .setListView(false)
      .setErrorMessage('listValidName')
      .enableIsError(true)
      .build();

    await render(hbs`{{context-panel/add-to-list/create-list-view}}`);
    assert.equal(findAll('.rsa-context-tree-table__metaValue.is-error').length, 1, 'list name has error');
    assert.equal(findAll('.rsa-form-button-wrapper.is-disabled').length, 1, 'Save button is disabled');
    assert.equal(find('.input-error').textContent.trim(), 'Enter valid list name (Max length is 255 characters).');
  });
});
