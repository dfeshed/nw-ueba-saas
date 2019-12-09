import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../../../helpers/vnext-patch';
import { find, findAll, render } from '@ember/test-helpers';
import EventColumnGroups from '../../../../../../data/subscriptions/column-group';
import { mapColumnGroupsForEventTable } from 'investigate-events/util/mapping';

let setState;

module('Integration | Component | Column Group Details', function(hooks) {
  let mappedColumnGroups;

  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.before(function() {
    mappedColumnGroups = mapColumnGroupsForEventTable(EventColumnGroups);
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  // selectors
  const columnGroupForm = '.column-group-form';
  const columnGroupView = '.column-group-view';

  // column-group-form selectors
  const addDetails = 'section.add-details';
  const displayedDetails = 'section.displayed-details';
  const scrollBox = '.column-group-details.scroll-box';
  const displayedColumns = `${displayedDetails} > ul.column-list li`;
  const availableMeta = `${addDetails} > ul.column-list li`;
  const editableGroupName = '.item-name';
  const editableGroupNameName = `${editableGroupName} .name`;
  const editableGroupNameValue = `${editableGroupName} .value`;
  const editableGroupNameValueInput = `${editableGroupNameValue} input`;

  // column-group-view selectors
  const readonlyGroupName = '.column-group-name';
  const readonlyGroupNameName = `${readonlyGroupName} .name`;
  const readonlyGroupNameValue = `${readonlyGroupName} .value`;
  const readonlyScrollBox = '.column-group-details.displayed-details.scroll-box.readonly';
  const readonlyScrollBoxName = `${readonlyScrollBox} .name`;
  const readonlyDisplayedColumns = `${readonlyScrollBox} > ul.column-list.value.readonly li`;

  const getTextFromDOMArray = (arr) => {
    return arr.reduce((a, c) => a + c.textContent.trim().replaceAll(' ', ''), '');
  };

  test('renders read only columnGroup details when an ootb columnGroup is being viewed', async function(assert) {
    this.set('columnGroup', mappedColumnGroups[5]);
    await render(hbs`{{events-table-container/header-container/column-groups/column-group-details columnGroup=columnGroup}}`);

    assert.ok(find(columnGroupView), 'Column Group Details rendered correctly');
    assert.equal(find(readonlyGroupNameName).textContent.trim(), 'Group Name', 'shall find correct text for group name title');
    assert.equal(find(readonlyGroupNameValue).textContent.trim(), 'Summary List', 'shall find correct text for group name value');
    assert.equal(find(readonlyScrollBoxName).textContent.trim(), 'Displayed Meta Keys', 'shall find correct text for scroll box name');
    assert.equal(findAll(readonlyDisplayedColumns).length, 3, '3/5 columns for Summary List rendered, time, medium not shown');

    const metaKeys = findAll(`${readonlyDisplayedColumns} span:first-of-type`);
    assert.equal(getTextFromDOMArray(metaKeys), 'custom.themesizecustom.meta-summary', 'Displayed meta keys');
  });

  test('renders an edit form to create a new column group', async function(assert) {
    this.set('columnGroup', null);
    this.set('editColumnGroup', () => {});
    new ReduxDataHelper(setState).metaKeyCache().build();
    await render(hbs`{{events-table-container/header-container/column-groups/column-group-details columnGroup=columnGroup editColumnGroup=editColumnGroup}}`);

    assert.ok(find(columnGroupForm), 'Column Group Details rendered correctly');
    assert.equal(find(editableGroupNameName).textContent.trim(), 'Group Name');
    assert.ok(find(editableGroupNameValueInput), 'input for group name');
    assert.equal(find(`${scrollBox} .name`).textContent.trim(), 'Displayed Meta Keys. (Maximum 40 keys)');
    assert.equal(findAll(displayedColumns).length, 0, 'No columns present in displayed keys');
    assert.equal(find('.add-details .name').textContent.trim(), 'Available Meta Keys');
    assert.equal(findAll(availableMeta).length, 93, '93/95 meta keys available');

  });

  test('renders an edit form to edit a custom column group', async function(assert) {
    const customColumnGroup = {
      id: 2,
      name: 'foo',
      columns: [
        { field: 'time', title: 'Time' },
        { field: 'medium', title: 'Type' },
        { field: 'action', title: 'Action' }
      ],
      isEditable: true
    };

    this.set('columnGroup', customColumnGroup);
    this.set('editColumnGroup', () => {});
    new ReduxDataHelper(setState).metaKeyCache().build();
    await render(hbs`{{events-table-container/header-container/column-groups/column-group-details columnGroup=columnGroup editColumnGroup=editColumnGroup}}`);

    assert.ok(find(columnGroupForm), 'Column Group Details rendered correctly');
    assert.equal(find(editableGroupNameName).textContent.trim(), 'Group Name');
    assert.ok(find(editableGroupNameValueInput), 'input for group name');
    assert.equal(findAll(editableGroupNameValueInput)[0].value, customColumnGroup.name, 'renders original group name');
    assert.equal(find(`${scrollBox} .name`).textContent.trim(), 'Displayed Meta Keys. (Maximum 40 keys)');
    assert.equal(findAll(displayedColumns).length, 1, '1 column present in displayed keys');
    assert.equal(find(`${addDetails} .name`).textContent.trim(), 'Available Meta Keys');
    assert.equal(findAll(availableMeta).length, 92, '92/95 meta keys available');
  });
});
