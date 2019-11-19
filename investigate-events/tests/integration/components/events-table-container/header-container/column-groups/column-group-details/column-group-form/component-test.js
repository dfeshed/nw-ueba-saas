import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { find, findAll, render, click, fillIn, triggerEvent } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../../../../helpers/redux-data-helper';
import METAKEYS from '../../../../../../../data/subscriptions/meta-key-cache/findAll/data';
import _ from 'lodash';

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

  const columnGroup = {
    id: '2',
    name: 'foo',
    columns: [{ field: 'action', title: 'Action Event' }]
  };

  // selectors
  const columnGroupForm = '.column-group-form';
  const addDetails = 'section.add-details';
  const displayedDetails = 'section.displayed-details';
  const scrollBox = '.column-group-details.scroll-box';
  const displayedColumns = `${displayedDetails} > ul.column-list li`;
  const availableMeta = `${addDetails} > ul.column-list li`;
  const groupName = '.item-name';
  const groupNameInput = '.item-name .value input';
  const allMetaAddedMessage = '.message.all-meta-added';
  const metaFilteredMessage = '.message.meta-filtered';
  const columnsFilteredMessage = '.columns-filtered-message';
  const noColumnsMessage = '.no-columns-message';

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
    assert.equal(findAll(displayedColumns).length, 0, 'No columns present in displayed keys');
    assert.equal(find(`${columnGroupForm} ${scrollBox} ${displayedDetails} ${noColumnsMessage}`).textContent.trim(), 'Add a meta key from the list below',
      'Message displayed when no columns present in displayed keys');

    assert.equal(findAll(availableMeta).length, 93, '93/95 meta keys available');
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
    assert.equal(findAll(displayedColumns).length, 1, '1 column present in displayed keys');

    assert.equal(findAll(availableMeta).length, 92, '92/95 meta keys available, 2 hidden, 1 chosen');
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

    assert.equal(findAll(displayedColumns).length, 0, 'No columns present in displayed keys');
    assert.equal(findAll(availableMeta).length, 93, '93 meta keys available');

    const availableOptions = findAll(`${availableMeta} button`);
    // add candidate meta
    await click(availableOptions[3]);
    await click(availableOptions[9]);

    assert.equal(findAll(displayedColumns).length, 2, '2 columns present in displayed keys');
    assert.equal(findAll(availableMeta).length, 91, '91 meta keys available');
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

    assert.equal(findAll(displayedColumns).length, 1, 'No columns present in displayed keys');
    assert.equal(findAll(availableMeta).length, 92, '93 meta keys available');

    const availableOptions = findAll(`${availableMeta} button`);
    // add candidate meta
    await click(availableOptions[3]);

    assert.equal(findAll(displayedColumns).length, 2, '2 columns present in displayed keys');
    assert.equal(findAll(availableMeta).length, 91, '91 meta keys available');
  });

  test('it will display a message if all meta are added', async function(assert) {

    this.set('columnGroup', null);
    this.set('editColumnGroup', () => {});

    new ReduxDataHelper(setState).metaKeyCache([{ metaName: 'foo', displayName: 'bar' }]).build();
    await render(hbs`{{events-table-container/header-container/column-groups/column-group-details/column-group-form
      columnGroup=columnGroup
      editColumnGroup=editColumnGroup}}`);

    const availableOptions = findAll(`${availableMeta} button`);
    // add candidate meta
    await click(availableOptions[0]);

    assert.equal(findAll(displayedColumns).length, 1, '1 column present in displayed keys');
    assert.equal(findAll(availableMeta).length, 0, 'No meta keys available');
    assert.equal(find(`${addDetails} ${allMetaAddedMessage}`).textContent.trim(), 'All meta keys have been added',
      'Message displayed when all meta added');
  });

  test('it will remove meta', async function(assert) {

    this.set('columnGroup', columnGroup);
    this.set('editColumnGroup', () => {});

    new ReduxDataHelper(setState).metaKeyCache().build();
    await render(hbs`{{events-table-container/header-container/column-groups/column-group-details/column-group-form
      columnGroup=columnGroup
      editColumnGroup=editColumnGroup}}`);

    const selectedOptions = findAll(`${displayedColumns} button`);
    // remove column
    await click(selectedOptions[0]);

    assert.equal(findAll(displayedColumns).length, 0, '1 column present in displayed keys');
    assert.equal(findAll(availableMeta).length, 93, '93 meta keys available');
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

    let availableOptions = findAll(`${availableMeta} button`);
    assert.ok(availableOptions.length > 0, 'have meta in available options list');

    await fillIn('.filter-group input', 'blahhhh');

    availableOptions = findAll(`${availableMeta} button`);
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

    let selectedOptions = findAll(`${displayedColumns} button`);
    assert.ok(selectedOptions.length === 0, 'no meta has been selected yet');

    // add selected meta
    const availableOptions = findAll(`${availableMeta} button`);
    await click(availableOptions[0]); // time (TIME), meta from test data

    selectedOptions = findAll(`${displayedColumns} button`);
    assert.ok(selectedOptions.length === 1, 'one meta selected yet');

    await fillIn('.filter-group input', 'blahhhh');

    selectedOptions = findAll(`${displayedColumns} button`);
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
    const availableOptions = findAll(`${availableMeta} button`);
    await click(availableOptions[0]); // time (TIME), meta from test data
    await fillIn('.filter-group input', 'blahhhh');

    assert.ok(findAll(columnsFilteredMessage).length === 1, 'proper message is displayed');
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
    assert.ok(findAll(metaFilteredMessage).length === 1, 'proper message is displayed');
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
    assert.ok(findAll(noColumnsMessage).length === 1, 'proper message is displayed');
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
    const availableOptions = findAll(`${availableMeta} button`);
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

    const availableOptions = findAll(`${availableMeta} button`);
    // add candidate meta
    await click(availableOptions[0]);

    let input = find('.filter-group input');
    let lengthOfSelection = input.selectionEnd - input.selectionStart;
    assert.ok(lengthOfSelection === 1, 'text in box is selected');

    const selectedOptions = findAll(`${displayedColumns} button`);
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
    assert.ok(find(`${groupName} .value.is-error`), 'element has error');

    // simulate typeIn
    await fillIn(groupNameInput, 'Custom');
    await triggerEvent(groupNameInput, 'keyup');
    assert.notOk(find(`${groupName} .value.is-error`), 'element does not have error');
  });

  test('limits number of meta keys user can add to a column group', async function(assert) {
    // array of 74 columns created from metaKeyCache
    const columns = _.cloneDeep(METAKEYS).splice(1, 74).map((meta) => {
      return {
        field: meta.metaName,
        title: meta.displayName
      };
    });
    const customColumnGroup = {
      id: '2',
      name: 'foo',
      columns
    };

    const translation = this.owner.lookup('service:i18n');

    this.set('columnGroup', customColumnGroup);
    this.set('editColumnGroup', () => {});

    new ReduxDataHelper(setState).metaKeyCache().columnGroups().build();
    await render(hbs`
      {{events-table-container/header-container/column-groups/column-group-details/column-group-form
        columnGroup=columnGroup
        editColumnGroup=editColumnGroup
      }}
    `);

    assert.equal(findAll(displayedColumns).length, 74, 'shall find correct number of displayed columns');
    assert.equal(findAll(availableMeta).length, 19);

    const availableOptions = findAll(`${availableMeta} button:not(disabled)`);
    // add candidate meta
    await click(availableOptions[0]);
    assert.equal(findAll(displayedColumns).length, 75, 'shall find correct number of displayed columns');
    assert.equal(findAll(availableMeta).length, 18);
    assert.equal(findAll(`${availableMeta} button[disabled]`).length, 18, 'Meta available can not be added beyond 50');
    assert.equal(findAll(`${availableMeta} .is-disabled`)[0].title, translation.t('investigate.events.columnGroups.selectionThresholdMessage'));

    const selectedOptions = findAll(`${displayedColumns} button`);
    // remove a selected meta
    await click(selectedOptions[0]);
    assert.equal(findAll(availableMeta).length, 19, 'shall find correct number of available meta');
    assert.equal(findAll(`${availableMeta} button[disabled]`).length, 0, 'Enabled available meta to add');
  });

  test('columns beyond 13th are not visible', async function(assert) {
    assert.expect(4);
    // array of 13 columns created from metaKeyCache
    const columns = _.cloneDeep(METAKEYS).splice(1, 13).map((meta) => {
      return {
        field: meta.metaName,
        title: meta.displayName
      };
    });
    const customColumnGroup = {
      id: '2',
      name: 'foo',
      columns
    };

    this.set('columnGroup', customColumnGroup);
    this.set('editColumnGroup', (group) => {
      if (group.columns.length == 14) {
        assert.ok(group.columns[12].visible);
        assert.notOk(group.columns[13].visible);
      }
    });

    new ReduxDataHelper(setState).metaKeyCache().columnGroups().build();
    await render(hbs`
      {{events-table-container/header-container/column-groups/column-group-details/column-group-form
        columnGroup=columnGroup
        editColumnGroup=editColumnGroup
      }}
    `);

    assert.equal(findAll(displayedColumns).length, 13, 'shall find correct number of displayed columns');

    const availableOptions = findAll(`${availableMeta} button:not(disabled)`);
    // add candidate meta
    await click(availableOptions[0]);
    assert.equal(findAll(displayedColumns).length, 14, 'shall find correct number of displayed columns');
  });
});
