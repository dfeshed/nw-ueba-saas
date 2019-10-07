import { module, test } from 'qunit';
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
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  const DISPLAYED_COLUMNS = '.displayed-details > ul.column-list li';
  const AVAILABLE_META = '.add-details > ul.column-list li';
  const groupNameInput = '.group-name .value input';

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

    assert.ok(find(groupNameInput), 'input for group name');

    // simulate typeIn
    await fillIn(groupNameInput, 'F');
    await triggerEvent(groupNameInput, 'keyup');

    const availableOptions = findAll(`${AVAILABLE_META} button`);
    // add candidate meta
    await click(availableOptions[3]);

    assert.equal(findAll(DISPLAYED_COLUMNS).length, 1, '1 column present in displayed keys');
    assert.equal(findAll(AVAILABLE_META).length, 19, '19 meta keys available');

    // typing in spaces only will trigger editColumnGroup, but with null object
    // thus running the editColumnGroup assertion only once
    await fillIn(groupNameInput, ' ');
    await triggerEvent(groupNameInput, 'keyup');
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

  test('will filter available meta', async function(assert) {
    assert.expect(2);

    this.set('columnGroup', null);
    this.set('editColumnGroup', () => {});

    new ReduxDataHelper(setState).language().build();
    await render(hbs`
      {{events-table-container/header-container/column-groups/column-group-details/column-group-form
        columnGroup=columnGroup
        editColumnGroup=editColumnGroup
      }}
    `);

    let availableOptions = findAll(`${AVAILABLE_META} button`);
    assert.ok(availableOptions.length > 0, 'have meta in available options list');

    await fillIn('.filter-group input', 'blahhhh');

    availableOptions = findAll(`${AVAILABLE_META} button`);
    assert.ok(availableOptions.length === 0, 'meta filtered out');
  });

  test('will filter selected meta', async function(assert) {
    assert.expect(3);

    this.set('columnGroup', null);
    this.set('editColumnGroup', () => {});

    new ReduxDataHelper(setState).language().build();
    await render(hbs`
      {{events-table-container/header-container/column-groups/column-group-details/column-group-form
        columnGroup=columnGroup
        editColumnGroup=editColumnGroup
      }}
    `);

    let selectedOptions = findAll(`${DISPLAYED_COLUMNS} button`);
    assert.ok(selectedOptions.length === 0, 'no meta has been selected yet');

    // add selected meta
    const availableOptions = findAll(`${AVAILABLE_META} button`);
    await click(availableOptions[0]); // a (A), fake meta from test data

    selectedOptions = findAll(`${DISPLAYED_COLUMNS} button`);
    assert.ok(selectedOptions.length === 1, 'one meta selected yet');

    await fillIn('.filter-group input', 'blahhhh');

    selectedOptions = findAll(`${DISPLAYED_COLUMNS} button`);
    assert.ok(selectedOptions.length === 0, 'one meta now filtered out');
  });

  test('will show proper message when all selected meta filtered away', async function(assert) {
    assert.expect(1);

    this.set('columnGroup', null);
    this.set('editColumnGroup', () => {});

    new ReduxDataHelper(setState).language().build();
    await render(hbs`
      {{events-table-container/header-container/column-groups/column-group-details/column-group-form
        columnGroup=columnGroup
        editColumnGroup=editColumnGroup
      }}
    `);

    // add selected meta and filter it out
    const availableOptions = findAll(`${AVAILABLE_META} button`);
    await click(availableOptions[0]); // a (A), fake meta from test data
    await fillIn('.filter-group input', 'blahhhh');

    assert.ok(findAll('.columns-filtered').length === 1, 'proper message is displayed');
  });

  test('will show proper message when all available meta filtered away', async function(assert) {
    assert.expect(1);

    this.set('columnGroup', null);
    this.set('editColumnGroup', () => {});

    new ReduxDataHelper(setState).language().build();
    await render(hbs`
      {{events-table-container/header-container/column-groups/column-group-details/column-group-form
        columnGroup=columnGroup
        editColumnGroup=editColumnGroup
      }}
    `);

    // filter out all available meta
    await fillIn('.filter-group input', 'blahhhh');
    assert.ok(findAll('.meta-filtered').length === 1, 'proper message is displayed');
  });

  test('will show proper message filter applied but no meta selected', async function(assert) {
    assert.expect(1);

    this.set('columnGroup', null);
    this.set('editColumnGroup', () => {});

    new ReduxDataHelper(setState).language().build();
    await render(hbs`
      {{events-table-container/header-container/column-groups/column-group-details/column-group-form
        columnGroup=columnGroup
        editColumnGroup=editColumnGroup
      }}
    `);

    // add selected meta and filter it out
    await fillIn('.filter-group input', 'blahhhh');
    assert.ok(findAll('.no-columns').length === 1, 'proper message is displayed');
  });

  test('will show proper message when filter applied but all meta chosen', async function(assert) {
    assert.expect(1);

    this.set('columnGroup', null);
    this.set('editColumnGroup', () => {});

    new ReduxDataHelper(setState).language().build();
    await render(hbs`
      {{events-table-container/header-container/column-groups/column-group-details/column-group-form
        columnGroup=columnGroup
        editColumnGroup=editColumnGroup
      }}
    `);

    // filter out all available meta
    const availableOptions = findAll(`${AVAILABLE_META} button`);
    availableOptions.forEach(click);
    await fillIn('.filter-group input', 'blahhhh');
    assert.ok(findAll('.all-meta-added').length === 1, 'proper message is displayed');
  });

  test('upon adding/removing meta the text will be selected', async function(assert) {
    assert.expect(2);

    this.set('columnGroup', null);
    this.set('editColumnGroup', () => {});

    new ReduxDataHelper(setState).language().build();
    await render(hbs`
      {{events-table-container/header-container/column-groups/column-group-details/column-group-form
        columnGroup=columnGroup
        editColumnGroup=editColumnGroup
      }}
    `);

    await fillIn('.filter-group input', 'a');

    const availableOptions = findAll(`${AVAILABLE_META} button`);
    // add candidate meta
    await click(availableOptions[0]);

    let input = find('.filter-group input');
    let lengthOfSelection = input.selectionEnd - input.selectionStart;
    assert.ok(lengthOfSelection === 1, 'text in box is selected');

    const selectedOptions = findAll(`${DISPLAYED_COLUMNS} button`);
    // remove column
    await click(selectedOptions[0]);

    input = find('.filter-group input');
    lengthOfSelection = input.selectionEnd - input.selectionStart;
    assert.ok(lengthOfSelection === 1, 'text in box is selected');
  });

  test('editColumnGroup action is called with null if the new columnGroup does not have a unique name', async function(assert) {
    assert.expect(2);
    this.set('columnGroup', null);
    this.set('editColumnGroup', (newGroup) => {
      assert.notOk(newGroup, 'newGroup is null if all details added but name is not unique');
    });

    new ReduxDataHelper(setState).language().columnGroups().build();
    await render(hbs`
      {{events-table-container/header-container/column-groups/column-group-details/column-group-form
        columnGroup=columnGroup
        editColumnGroup=editColumnGroup
      }}
    `);

    // simulate typeIn
    await fillIn(groupNameInput, 'Custom 1');
    await triggerEvent(groupNameInput, 'keyup');

    const availableOptions = findAll(`${AVAILABLE_META} button`);
    // add candidate meta
    await click(availableOptions[3]);
  });

  test('editColumnGroup action is called with valid object if new Group has unique name and columns', async function(assert) {

    assert.expect(1);
    this.set('columnGroup', null);
    this.set('editColumnGroup', (newGroup) => {
      if (newGroup) {
        assert.ok(newGroup, 'newGroup is null if name is not unique');
      }
    });

    new ReduxDataHelper(setState).language().columnGroups().build();
    await render(hbs`
      {{events-table-container/header-container/column-groups/column-group-details/column-group-form
        columnGroup=columnGroup
        editColumnGroup=editColumnGroup
      }}
    `);

    // simulate typeIn
    await fillIn(groupNameInput, 'Some new name');
    await triggerEvent(groupNameInput, 'keyup');

    const availableOptions = findAll(`${AVAILABLE_META} button`);
    // add candidate meta
    await click(availableOptions[3]);
  });
});
