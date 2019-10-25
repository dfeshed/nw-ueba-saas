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

  const columnGroup = {
    id: '2',
    name: 'foo',
    columns: [{ field: 'action', title: 'Action Event' }]
  };

  test('renders editable form for a new item', async function(assert) {
    assert.expect(4);
    this.set('columnGroup', null);
    this.set('editColumnGroup', () => {
      assert.ok(true, 'editColumnGroup is not called when a new item form is rendered');
    });

    new ReduxDataHelper(setState).metaKeyCache().build();
    await render(hbs`{{events-table-container/header-container/column-groups/column-group-details/column-group-form
      columnGroup=columnGroup
      editColumnGroup=editColumnGroup}}`);

    assert.ok(find(groupNameInput), 'input for group name');

    assert.equal(findAll(DISPLAYED_COLUMNS).length, 0, 'No columns present in displayed keys');
    assert.equal(find('.group-details p.message').textContent.trim(), 'Add a meta key from the list below',
      'Message displayed when no columns present in displayed keys');

    assert.equal(findAll(AVAILABLE_META).length, 93, '93/95 meta keys available');
  });

  test('renders form populated with details of item being edited', async function(assert) {

    assert.expect(5);
    this.set('columnGroup', columnGroup);
    this.set('editColumnGroup', () => {
      assert.ok(true, 'calls editColumnGroup when there are pre-populated values to be broadcasted');
    });

    new ReduxDataHelper(setState).metaKeyCache().build();
    await render(hbs`{{events-table-container/header-container/column-groups/column-group-details/column-group-form
      columnGroup=columnGroup
      editColumnGroup=editColumnGroup}}`);

    assert.ok(find(groupNameInput), 'input for group name');
    assert.ok(findAll(groupNameInput)[0].value, columnGroup.name, 'columngroup name rendered correctly');
    assert.equal(findAll(DISPLAYED_COLUMNS).length, 1, '1 column present in displayed keys');

    assert.equal(findAll(AVAILABLE_META).length, 92, '92/95 meta keys available, 2 hidden, 1 chosen');
  });

  test('it will add name', async function(assert) {
    assert.expect(3);
    this.set('columnGroup', null);
    this.set('editColumnGroup', () => {
      assert.ok(true, 'calls editColumnGroup when a change is broadcasted');
    });

    new ReduxDataHelper(setState).build();
    await render(hbs`{{events-table-container/header-container/column-groups/column-group-details/column-group-form
      columnGroup=columnGroup
      editColumnGroup=editColumnGroup}}`);

    assert.ok(find(groupNameInput), 'input for group name');

    // simulate typeIn
    await fillIn(groupNameInput, 'A');
    await triggerEvent(groupNameInput, 'keyup');

    assert.equal(findAll(groupNameInput)[0].value, 'A');
  });

  test('will update name', async function(assert) {
    assert.expect(5);
    this.set('columnGroup', columnGroup);
    this.set('editColumnGroup', () => {
      assert.ok(true, 'called once during initializeForm and then on updating the name');
    });

    new ReduxDataHelper(setState).build();
    await render(hbs`{{events-table-container/header-container/column-groups/column-group-details/column-group-form
      columnGroup=columnGroup
      editColumnGroup=editColumnGroup}}`);

    assert.ok(find(groupNameInput), 'input for group name');
    assert.equal(findAll(groupNameInput)[0].value, columnGroup.name);

    // simulate typeIn
    await fillIn(groupNameInput, 'A');
    await triggerEvent(groupNameInput, 'keyup');
    assert.equal(findAll(groupNameInput)[0].value, 'A');
  });

  test('it will add meta', async function(assert) {

    assert.expect(6);
    this.set('columnGroup', null);
    this.set('editColumnGroup', () => {
      assert.ok(true, 'called both the times meta is added');
    });

    new ReduxDataHelper(setState).metaKeyCache().build();
    await render(hbs`{{events-table-container/header-container/column-groups/column-group-details/column-group-form
      columnGroup=columnGroup
      editColumnGroup=editColumnGroup}}`);

    assert.equal(findAll(DISPLAYED_COLUMNS).length, 0, 'No columns present in displayed keys');
    assert.equal(findAll(AVAILABLE_META).length, 93, '93 meta keys available');

    const availableOptions = findAll(`${AVAILABLE_META} button`);
    // add candidate meta
    await click(availableOptions[3]);
    await click(availableOptions[9]);

    assert.equal(findAll(DISPLAYED_COLUMNS).length, 2, '2 columns present in displayed keys');
    assert.equal(findAll(AVAILABLE_META).length, 91, '91 meta keys available');
  });

  test('it will update meta', async function(assert) {

    assert.expect(6);
    this.set('columnGroup', columnGroup);
    this.set('editColumnGroup', () => {
      assert.ok(true, 'called once during initializeForm and then on adding meta');
    });

    new ReduxDataHelper(setState).metaKeyCache().build();
    await render(hbs`{{events-table-container/header-container/column-groups/column-group-details/column-group-form
      columnGroup=columnGroup
      editColumnGroup=editColumnGroup}}`);

    assert.equal(findAll(DISPLAYED_COLUMNS).length, 1, 'No columns present in displayed keys');
    assert.equal(findAll(AVAILABLE_META).length, 92, '93 meta keys available');

    const availableOptions = findAll(`${AVAILABLE_META} button`);
    // add candidate meta
    await click(availableOptions[3]);

    assert.equal(findAll(DISPLAYED_COLUMNS).length, 2, '2 columns present in displayed keys');
    assert.equal(findAll(AVAILABLE_META).length, 91, '91 meta keys available');
  });

  test('it will display a message if all meta are added', async function(assert) {

    this.set('columnGroup', null);
    this.set('editColumnGroup', () => {});

    new ReduxDataHelper(setState).metaKeyCache([{ metaName: 'foo', displayName: 'bar' }]).build();
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

  test('it will remove meta', async function(assert) {

    this.set('columnGroup', columnGroup);
    this.set('editColumnGroup', () => {});

    new ReduxDataHelper(setState).metaKeyCache().build();
    await render(hbs`{{events-table-container/header-container/column-groups/column-group-details/column-group-form
      columnGroup=columnGroup
      editColumnGroup=editColumnGroup}}`);

    const selectedOptions = findAll(`${DISPLAYED_COLUMNS} button`);
    // remove column
    await click(selectedOptions[0]);

    assert.equal(findAll(DISPLAYED_COLUMNS).length, 0, '1 column present in displayed keys');
    assert.equal(findAll(AVAILABLE_META).length, 93, '93 meta keys available');
  });

  test('will filter available meta', async function(assert) {
    assert.expect(2);

    this.set('columnGroup', null);
    this.set('editColumnGroup', () => {});

    new ReduxDataHelper(setState).metaKeyCache().build();
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

    new ReduxDataHelper(setState).metaKeyCache().build();
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
    await click(availableOptions[0]); // time (TIME), meta from test data

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

    new ReduxDataHelper(setState).metaKeyCache().build();
    await render(hbs`
      {{events-table-container/header-container/column-groups/column-group-details/column-group-form
        columnGroup=columnGroup
        editColumnGroup=editColumnGroup
      }}
    `);

    // add selected meta and filter it out
    const availableOptions = findAll(`${AVAILABLE_META} button`);
    await click(availableOptions[0]); // time (TIME), meta from test data
    await fillIn('.filter-group input', 'blahhhh');

    assert.ok(findAll('.columns-filtered').length === 1, 'proper message is displayed');
  });

  test('will show proper message when all available meta filtered away', async function(assert) {
    assert.expect(1);

    this.set('columnGroup', null);
    this.set('editColumnGroup', () => {});

    new ReduxDataHelper(setState).metaKeyCache().build();
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

    new ReduxDataHelper(setState).metaKeyCache().build();
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

    new ReduxDataHelper(setState).metaKeyCache().build();
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

    new ReduxDataHelper(setState).metaKeyCache().build();
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

  test('renders nameError if columnGroup name is not unique', async function(assert) {

    this.set('columnGroup', null);
    this.set('editColumnGroup', () => {});

    new ReduxDataHelper(setState).metaKeyCache().columnGroups().build();
    await render(hbs`
      {{events-table-container/header-container/column-groups/column-group-details/column-group-form
        columnGroup=columnGroup
        editColumnGroup=editColumnGroup
      }}
    `);

    // simulate typeIn
    await fillIn(groupNameInput, 'Custom 1');
    await triggerEvent(groupNameInput, 'keyup');
    assert.ok(find('.group-name .value.is-error'), 'element has error');

    // simulate typeIn
    await fillIn(groupNameInput, 'Custom');
    await triggerEvent(groupNameInput, 'keyup');
    assert.notOk(find('.group-name .value.is-error'), 'element does not have error');
  });

});
