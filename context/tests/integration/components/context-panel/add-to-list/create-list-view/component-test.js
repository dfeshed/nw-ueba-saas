import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { click, find, findAll, fillIn, render, waitUntil } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

const model = {
  list: [
    { name: 'test-list1' },
    { name: 'test-list2' }
  ]
};

const timeout = 15000;

module('Integration | Component | context-panel/add-to-list/create-list-view', function(hooks) {

  setupRenderingTest(hooks);

  test('it renders', async function(assert) {

    await render(hbs`{{context-panel/add-to-list/create-list-view}}`);

    assert.equal(find('.rsa-context-tree-table__createList').textContent.replace(/\s/g, ''), 'CreateNewListListNameDescriptionCancelSave');
  });

  test('List name should not be empty', async function(assert) {

    this.set('model', model);

    await render(hbs`{{context-panel/add-to-list/create-list-view model=model}}`);

    await fillIn('.rsa-context-tree-table__createList input[type=text]', '');

    await click(findAll('.rsa-form-button')[1]);

    assert.equal(find('.input-error').textContent.trim(), 'Enter valid list name (Max length is 255 characters).', 'list name should not be empty');
  });

  test('List name should not be more than 255 characters', async function(assert) {

    const input = 'abcfdsfoisfhoisghosdihudofhsdguhsgufghsoguhsgosfhgoughsgoufghoughsoguhfoughsfgouhsgouhfsogshgohgsghdsoghsdoghsogihsgoighsoihgsoighso' +
      'foughsfgouhsgouhsdgousghsoughgndnofhdsoufsogshgohgsghdsoghsdoghsogihsgoighsoihgsoighsofoughsfgouhsgouhsdgousghsoughgndnofhdsou';

    this.set('model', model);

    await render(hbs`{{context-panel/add-to-list/create-list-view model=model}}`);

    await fillIn('.rsa-context-tree-table__createList input[type=text]', input);

    await click(findAll('.rsa-form-button')[1]);

    assert.equal(find('.input-error').textContent.trim(), 'Enter valid list name (Max length is 255 characters).');
  });

  test('Duplicate List name should not allowed', async function(assert) {

    this.set('model', model);

    await render(hbs`{{context-panel/add-to-list/create-list-view model=model}}`);

    await fillIn('.rsa-context-tree-table__createList input[type=text]', 'test-list1');

    assert.ok(findAll('.rsa-form-button')[1].disabled, 'save button should be disabled');

    assert.equal(find('.input-error').textContent.trim(), 'List name already exists!');
  });

  test('while creating list cancel button should cancel the list creation', async function(assert) {

    this.set('model', model);

    await render(hbs`{{context-panel/add-to-list/create-list-view model=model}}`);

    await fillIn('.rsa-context-tree-table__createList input[type=text]', 'test-list3');

    await click(findAll('.rsa-form-button')[0]);

    assert.equal(find('.rsa-context-tree-table__createList input[type=text]').placeholder, 'Enter List Name', 'should reset the properties of list');
  });

  test('while creating list save button should save list', async function(assert) {

    this.set('model', model);

    await render(hbs`{{context-panel/add-to-list/create-list-view model=model}}`);

    await fillIn('.rsa-context-tree-table__createList input[type=text]', 'test-list3');

    await click(findAll('.rsa-form-button')[1]);

    const list = this.get('model.list');

    await waitUntil(() => {
      return list.length === 3;
    }, { timeout });

    assert.equal(find('.rsa-context-tree-table__createList input[type=text]').placeholder, 'Enter List Name');
  });
});
