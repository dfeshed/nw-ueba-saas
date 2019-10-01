import { module, test, skip } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { find, findAll, render, click, fillIn, triggerEvent } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../../../../helpers/redux-data-helper';

let setState;

module('Integration | Component | Column Group form', function(hooks) {

  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  const DISPLAYED_COLUMNS = '.group-details > ul.column-list li';
  const AVAILABLE_META = '.add-details > ul.column-list li';

  test('it will render editable form for a new item', async function(assert) {
    assert.expect(4);

    this.set('columnGroup', null);
    this.set('editColumnGroup', (newGroup) => {
      assert.notOk(newGroup, 'editColumnGroup is not called if there is no change');
    });

    new ReduxDataHelper(setState).language().build();
    await render(hbs`{{events-table-container/header-container/column-groups/column-group-details/column-group-form
      columnGroup=columnGroup
      editColumnGroup=editColumnGroup}}`);

    const groupNameInput = '.group-name .value input';
    assert.ok(find(groupNameInput), 'input for group name');

    assert.equal(findAll(DISPLAYED_COLUMNS).length, 0, 'No columns present in displayed keys');
    assert.equal(find('.group-details p.message').textContent.trim(), 'Add a meta key from the list below',
      'Message displayed when no columns present in displayed keys');

    assert.equal(findAll(AVAILABLE_META).length, 20, '20 meta keys available');

  });

  test('it will add name', async function(assert) {
    assert.expect(2);

    this.set('columnGroup', null);
    this.set('editColumnGroup', (newGroup) => {
      assert.notOk(newGroup, 'editColumnGroup is called with null if group has name and no columns');
    });

    new ReduxDataHelper(setState).language().build();
    await render(hbs`{{events-table-container/header-container/column-groups/column-group-details/column-group-form
      columnGroup=columnGroup
      editColumnGroup=editColumnGroup}}`);

    const groupNameInput = '.group-name .value input';
    assert.ok(find(groupNameInput), 'input for group name');

    // simulate typeIn
    await fillIn(groupNameInput, 'A');
    await triggerEvent(groupNameInput, 'keyup');
  });

  test('it will add meta', async function(assert) {
    assert.expect(6);

    this.set('columnGroup', null);
    this.set('editColumnGroup', (newGroup) => {
      assert.notOk(newGroup, 'editColumnGroup is called with null if group has columns and no name');
    });

    new ReduxDataHelper(setState).language().build();
    await render(hbs`{{events-table-container/header-container/column-groups/column-group-details/column-group-form
      columnGroup=columnGroup
      editColumnGroup=editColumnGroup}}`);

    assert.equal(findAll(DISPLAYED_COLUMNS).length, 0, 'No columns present in displayed keys');
    assert.equal(findAll(AVAILABLE_META).length, 20, '20 meta keys available');

    const availableOptions = findAll(`${AVAILABLE_META} button`);
    // add candidate meta
    await click(availableOptions[3]);
    await click(availableOptions[9]);

    assert.equal(findAll(DISPLAYED_COLUMNS).length, 2, '2 columns present in displayed keys');
    assert.equal(findAll(AVAILABLE_META).length, 18, '18 meta keys available');
  });

  test('it will display a message if all meta are added', async function(assert) {

    this.set('columnGroup', null);
    this.set('editColumnGroup', () => {});

    new ReduxDataHelper(setState).language([{ metaName: 'foo', displayName: 'bar' }]).build();
    await render(hbs`{{events-table-container/header-container/column-groups/column-group-details/column-group-form
      columnGroup=columnGroup
      editColumnGroup=editColumnGroup}}`);

    const availableOptions = findAll(`${AVAILABLE_META} button`);
    // add candidate meta
    await click(availableOptions[0]);

    assert.equal(findAll(DISPLAYED_COLUMNS).length, 1, '1 column present in displayed keys');
    assert.equal(findAll(AVAILABLE_META).length, 0, 'No meta keys available');
    assert.equal(find('.group-details p.message').textContent.trim(), 'All meta keys have been added',
      'Message displayed when all meta added');
  });

  test('it will add meta and name', async function(assert) {
    assert.expect(4);

    this.set('columnGroup', null);
    this.set('editColumnGroup', (newGroup) => {
      if (newGroup) {
        assert.ok(newGroup, 'Edit is called with new group everytime group is updated to have name and column');
      }
    });

    new ReduxDataHelper(setState).language().build();

    await render(hbs`{{events-table-container/header-container/column-groups/column-group-details/column-group-form
      columnGroup=columnGroup
      editColumnGroup=editColumnGroup}}`);

    const groupNameInput = '.group-name .value input';
    assert.ok(find(groupNameInput), 'input for group name');

    // simulate typeIn
    await fillIn(groupNameInput, 'F');
    await triggerEvent(groupNameInput, 'keyup');

    const availableOptions = findAll(`${AVAILABLE_META} button`);
    // add candidate meta
    await click(availableOptions[3]);

    assert.equal(findAll(DISPLAYED_COLUMNS).length, 1, '1 column present in displayed keys');
    assert.equal(findAll(AVAILABLE_META).length, 19, '19 meta keys available');
  });

  test('it will remove meta', async function(assert) {
    assert.expect(5);

    this.set('columnGroup', null);
    this.set('editColumnGroup', (newGroup) => {
      assert.notOk(newGroup, 'edit is called when it adds and removes meta');
    });

    new ReduxDataHelper(setState).language().build();
    await render(hbs`{{events-table-container/header-container/column-groups/column-group-details/column-group-form
      columnGroup=columnGroup
      editColumnGroup=editColumnGroup}}`);

    const availableOptions = findAll(`${AVAILABLE_META} button`);
    // add candidate meta
    await click(availableOptions[3]);
    await click(availableOptions[9]);

    const selectedOptions = findAll(`${DISPLAYED_COLUMNS} button`);
    // remove column
    await click(selectedOptions[0]);

    assert.equal(findAll(DISPLAYED_COLUMNS).length, 1, '1 column present in displayed keys');
    assert.equal(findAll(AVAILABLE_META).length, 19, '19 meta keys available');
  });

  skip('it will filter text', async function(/* assert */) {
    // Tests to come once filtering implemented
  });
});
