import { module, test } from 'qunit';
import {
  highlightedIndex,
  isListManagerReady,
  list,
  listName,
  modelName,
  filteredList,
  itemType,
  newItemButtonTitle,
  isExpanded,
  filterText,
  selectedItemId,
  viewName,
  highlightedId,
  isListView,
  noResultsMessage,
  selectedIndex,
  helpId,
  hasContextualHelp,
  caption,
  titleTooltip,
  filterPlaceholder,
  hasIsEditableIndicators,
  editItem,
  isNewItem,
  shouldSelectedItemPersist,
  disabledListName
} from 'rsa-list-manager/selectors/list-manager/selectors';
import { LIST_VIEW, DETAILS_VIEW } from 'rsa-list-manager/constants/list-manager';
import ReduxDataHelper from '../../../helpers/redux-data-helper';

module('Unit | Selectors | list-manager');

const stateLocation1 = 'listManager';
const listName1 = 'Some Items';
const modelName1 = 'item-api';
const list1 = [
  { id: 3, name: 'eba', subItems: [ 'a', 'b', 'c' ] },
  { id: 1, name: 'foo', subItems: [ 'a', 'b' ] },
  { id: 2, name: 'bar', subItems: [ 'e', 'b', 'c' ] },
  { id: 4, name: 'Baz', subItems: [ 'c' ] }
];
const listHasIsEditable = [
  { id: 1, name: 'eba', isEditable: true },
  { id: 2, name: 'foo', isEditable: true },
  { id: 3, name: 'bar', isEditable: false },
  { id: 4, name: 'Baz', isEditable: false }
];
const listNotHasIsEditable = [
  { id: 1, name: 'eba' },
  { id: 2, name: 'foo' },
  { id: 3, name: 'bar' }
];

const randomIndex = Math.floor(Math.random() * Math.floor(list1.length));

const item1 = { id: 2, name: 'bar', subItems: [ 'e', 'b', 'c' ] };
const viewName1 = 'list-view';

test('highlightedIndex returns highlightedIndex for stateLocation', function(assert) {
  const state = new ReduxDataHelper()
    .highlightedIndex(randomIndex)
    .build();
  const result = highlightedIndex(state, stateLocation1);
  assert.equal(result, randomIndex, 'Shall select highlightedIndex based on stateLocation');
});

test('listName returns listName for stateLocation', function(assert) {
  const state = new ReduxDataHelper()
    .listName(listName1)
    .build();
  const result = listName(state, stateLocation1);
  assert.equal(result, listName1, 'Shall select listName based on stateLocation');
});

test('modelName returns modelName for stateLocation', function(assert) {
  const state = new ReduxDataHelper()
    .modelName(modelName1)
    .build();
  const result = modelName(state, stateLocation1);
  assert.equal(result, modelName1, 'Shall select modelName based on stateLocation');
});

test('isListManagerReady returns true if stateLocation and list exist', function(assert) {
  const state = new ReduxDataHelper()
    .stateLocation(stateLocation1)
    .list([])
    .build();
  const result = isListManagerReady(state, stateLocation1);
  assert.ok(result, 'isListManagerReady shall be true if stateLocation and list exist');
});

test('isListManagerReady returns false if stateLocation does not exist', function(assert) {
  const state = new ReduxDataHelper().list([]).build();
  const result = isListManagerReady(state, stateLocation1);
  assert.notOk(result, 'isListManagerReady shall be false if stateLocation does not exist');
});

test('isListManagerReady returns false if list does not exist', function(assert) {
  const state = new ReduxDataHelper().stateLocation(stateLocation1).build();
  const result = isListManagerReady(state, stateLocation1);
  assert.notOk(result, 'isListManagerReady shall be false if list does not exist');
});

test('list returns list for stateLocation', function(assert) {
  const state = new ReduxDataHelper()
    .stateLocation(stateLocation1)
    .list(list1)
    .build();
  const result = list(state, stateLocation1);
  assert.deepEqual(result, list1, 'Shall select list based on stateLocation');
});

test('filteredList returns filteredList for stateLocation', function(assert) {
  const filterText1 = 'fo';
  const filteredList1 = list1.filter((item) => item.name.toLowerCase().includes(filterText1));
  const state = new ReduxDataHelper()
    .stateLocation(stateLocation1)
    .list(list1)
    .filterText(filterText1)
    .build();
  const result = filteredList(state, stateLocation1);
  assert.deepEqual(result, filteredList1, 'Shall select filteredList based on stateLocation and filterText');
});

test('filteredList returns unfiltered list for stateLocation if no filterText', function(assert) {
  const filterText1 = '';
  const state = new ReduxDataHelper()
    .stateLocation(stateLocation1)
    .list(list1)
    .filterText(filterText1)
    .build();
  const result = filteredList(state, stateLocation1);
  assert.deepEqual(result, state.listManager.list, 'filteredList shall return list based on stateLocation if no filterText');
});

test('itemType returns itemType for stateLocation', function(assert) {
  const state = new ReduxDataHelper()
    .stateLocation(stateLocation1)
    .list(list1)
    .listName(listName1)
    .build();
  const result = itemType(state, stateLocation1);
  assert.deepEqual(result, listName1.slice(0, -1), 'Shall select itemType based on stateLocation and listName');
});

test('newItemButtonTitle returns newItemButtonTitle for stateLocation', function(assert) {
  const state = new ReduxDataHelper()
    .stateLocation(stateLocation1)
    .list(list1)
    .listName(listName1)
    .build();
  const result = newItemButtonTitle(state, stateLocation1);
  const expected = listName1.slice(0, -1);
  assert.equal(result, `New ${expected}`, 'Shall select newItemButtonTitle based on stateLocation and listName');
});

test('isExpanded returns isExpanded for stateLocation', function(assert) {
  const state = new ReduxDataHelper()
    .stateLocation(stateLocation1)
    .isExpanded(false)
    .build();
  const result = isExpanded(state, stateLocation1);
  assert.notOk(result, 'Shall select isExpanded based on stateLocation');
});

test('filterText returns filterText for stateLocation', function(assert) {
  const state = new ReduxDataHelper()
    .stateLocation(stateLocation1)
    .filterText('someText')
    .build();
  const result = filterText(state, stateLocation1);
  assert.equal(result, 'someText', 'Shall select filterText based on stateLocation');
});

test('selectedItemId returns selectedItemId for stateLocation', function(assert) {
  const state = new ReduxDataHelper()
    .stateLocation(stateLocation1)
    .selectedItemId(item1.id)
    .build();
  const result = selectedItemId(state, stateLocation1);
  assert.equal(result, item1.id, 'Shall select selectedItemId based on stateLocation');
});

test('viewName returns viewName for stateLocation', function(assert) {
  const state = new ReduxDataHelper()
    .stateLocation(stateLocation1)
    .viewName(viewName1)
    .build();
  const result = viewName(state, stateLocation1);
  assert.equal(result, viewName1, 'Shall select viewName based on stateLocation');
});

test('highlightedId returns highlightedId for stateLocation', function(assert) {
  const list2 = [
    { id: 'a', name: 'eba', subItems: [ 'a', 'b', 'c' ] },
    { id: 'b', name: 'foo', subItems: [ 'a', 'b' ] },
    { id: 'c', name: 'bar', subItems: [ 'e', 'b', 'c' ] },
    { id: 'd', name: 'Baz', subItems: [ 'c' ] }
  ];

  const index = Math.floor(Math.random() * Math.floor(4));
  const state = new ReduxDataHelper()
    .stateLocation(stateLocation1)
    .list(list2)
    .highlightedIndex(index)
    .build();
  const result = highlightedId(state, stateLocation1);
  assert.equal(result, list2[index].id, 'Shall select viewName based on stateLocation');
});

test('isListView returns true for stateLocation if viewName is list-view', function(assert) {
  const state = new ReduxDataHelper()
    .viewName(LIST_VIEW)
    .build();
  const result = isListView(state, stateLocation1);
  assert.ok(result, 'isListView shall return true if viewName is list-view');
});

test('isListView returns false for stateLocation if viewName is not list-view', function(assert) {
  const state = new ReduxDataHelper()
    .viewName('some-view')
    .build();
  const result = isListView(state, stateLocation1);
  assert.notOk(result, 'isListView shall return false if viewName is not list-view');
});

test('noResultsMessage returns noResultsMessage for stateLocation', function(assert) {
  const state = new ReduxDataHelper()
    .listName('apples')
    .build();
  const result = noResultsMessage(state, stateLocation1);
  const expected = 'All apples have been excluded by the current filter';
  assert.equal(result, expected, 'Shall select noResultsMessage based on stateLocation and listName');
});

test('selectedIndex returns selectedIndex for stateLocation', function(assert) {
  const state = new ReduxDataHelper()
    .stateLocation(stateLocation1)
    .list(list1)
    .listName(listName1)
    .selectedItemId(list1[randomIndex].id)
    .build();
  const result = selectedIndex(state, stateLocation1);
  assert.equal(result, randomIndex, 'Shall select selectedIndex based on stateLocation and selectedItemId');
});

test('selectedIndex returns -1 for stateLocation if there is no selectedItemId', function(assert) {
  const state = new ReduxDataHelper()
    .stateLocation(stateLocation1)
    .list(list1)
    .listName(listName1)
    .selectedItemId(null)
    .build();
  const result = selectedIndex(state, stateLocation1);
  assert.equal(result, -1, 'selectedIndex shall return -1 if there is no selectedItemId');
});

test('helpId returns helpId for stateLocation', function(assert) {
  const helpId1 = { moduleId: 'investigation', topicId: 'eaColumnGroups' };
  const state = new ReduxDataHelper()
    .stateLocation(stateLocation1)
    .list(list1)
    .listName(listName1)
    .helpId(helpId1)
    .build();
  const result = helpId(state, stateLocation1);
  assert.deepEqual(result, helpId1, 'Shall select filterText based on stateLocation');
});

test('hasContextualHelp returns true for stateLocation if helpId has moduleId and topicId', function(assert) {
  const helpId1 = { moduleId: 'investigation', topicId: 'eaColumnGroups' };
  const state = new ReduxDataHelper()
    .stateLocation(stateLocation1)
    .list(list1)
    .listName(listName1)
    .helpId(helpId1)
    .build();
  const result = hasContextualHelp(state, stateLocation1);
  assert.ok(result, 'hasContextualHelp shall return true if moduleId and topicId both exist in helpId');
});

test('hasContextualHelp returns false for stateLocation if helpId is missing moduleId or topicId', function(assert) {
  const helpId1 = { moduleId: null, topicId: 'eaColumnGroups' };
  const state = new ReduxDataHelper()
    .stateLocation(stateLocation1)
    .list(list1)
    .listName(listName1)
    .helpId(helpId1)
    .build();
  const result = hasContextualHelp(state, stateLocation1);
  assert.notOk(result, 'hasContextualHelp shall return false if moduleId does not exist in helpId');

  const helpId2 = { moduleId: 'investigation', topicId: null };
  const state2 = new ReduxDataHelper()
    .stateLocation(stateLocation1)
    .list(list1)
    .listName(listName1)
    .helpId(helpId2)
    .build();
  const result2 = hasContextualHelp(state2, stateLocation1);
  assert.notOk(result2, 'hasContextualHelp shall return false if topicId does not exist in helpId');

  const helpId3 = { moduleId: null, topicId: null };
  const state3 = new ReduxDataHelper()
    .stateLocation(stateLocation1)
    .list(list1)
    .listName(listName1)
    .helpId(helpId3)
    .build();
  const result3 = hasContextualHelp(state3, stateLocation1);
  assert.notOk(result3, 'hasContextualHelp shall return false if topicId and moduleId do not exist in helpId');
});

test('caption returns caption for stateLocation if selectedItemId exists', function(assert) {
  const state = new ReduxDataHelper()
    .stateLocation(stateLocation1)
    .list(list1)
    .listName(listName1)
    .selectedItemId(list1[0].id)
    .build();
  const result = caption(state, stateLocation1);
  const expected = `${state.listManager.listName.slice(0, -1)}: ${list1[0].name}`;
  assert.equal(result, expected, 'Shall select caption based on stateLocation, listName, selectedItemId');
});

test('caption returns listName for stateLocation if selectedItemId does not exist', function(assert) {
  const state = new ReduxDataHelper()
    .stateLocation(stateLocation1)
    .list(list1)
    .listName(listName1)
    .selectedItemId(null)
    .build();
  const result = caption(state, stateLocation1);
  assert.equal(result, listName1, 'Shall select caption based on stateLocation, listName, selectedItemId');
});

test('titleTooltip returns titleTooltip for stateLocation if selectedItemId exists', function(assert) {
  const state = new ReduxDataHelper()
    .stateLocation(stateLocation1)
    .list(list1)
    .listName(listName1)
    .selectedItemId(list1[randomIndex].id)
    .build();
  const result = titleTooltip(state, stateLocation1);
  assert.equal(result, list1[randomIndex].name, 'Shall select titleTooltip based on stateLocation and selectedItemId');
});

test('titleTooltip returns null for stateLocation if selectedItemId does not exist', function(assert) {
  const state = new ReduxDataHelper()
    .stateLocation(stateLocation1)
    .list(list1)
    .listName(listName1)
    .selectedItemId(null)
    .build();
  const result = titleTooltip(state, stateLocation1);
  assert.notOk(result, 'Shall select titleTooltip based on stateLocation and selectedItemId');
});

test('filterPlaceholder returns filterPlaceholder for stateLocation', function(assert) {
  const state = new ReduxDataHelper()
    .stateLocation(stateLocation1)
    .list(list1)
    .listName(listName1)
    .build();
  const result = filterPlaceholder(state, stateLocation1);
  const expected = `Filter ${listName1.toLowerCase()}`;
  assert.equal(result, expected, 'Shall select filterPlaceholder based on stateLocation and listName');
});

test('disabledListName returns listName for stateLocation', function(assert) {
  const state = new ReduxDataHelper()
    .stateLocation(stateLocation1)
    .list(list1)
    .listName(listName1)
    .build();
  const result = disabledListName(state, stateLocation1);
  const expected = listName1.toLowerCase();
  assert.equal(result, expected, 'Shall select disabledListName based on listName');
});

test('disabledListName returns empty when listName is not available', function(assert) {
  const state = new ReduxDataHelper()
    .stateLocation(stateLocation1)
    .list(list1)
    .listName(undefined)
    .build();
  const result = disabledListName(state, stateLocation1);
  const expected = '';
  assert.equal(result, expected, 'Shall select disabledListName based on listName');
});

test('hasIsEditableIndicators returns true for stateLocation if filteredList contains items with isEditable property',
  function(assert) {
    const state = new ReduxDataHelper()
      .stateLocation(stateLocation1)
      .list(listHasIsEditable)
      .filterText('a')
      .listName(listName1)
      .build();
    const result = hasIsEditableIndicators(state, stateLocation1);
    assert.ok(result, 'hasIsEditableIndicators shall return true if filteredList contains items with isEditable property');
  });

test('hasIsEditableIndicators returns false for stateLocation if no item in filteredList has isEditable property',
  function(assert) {
    const state = new ReduxDataHelper()
      .stateLocation(stateLocation1)
      .list(listNotHasIsEditable)
      .filterText('a')
      .listName(listName1)
      .build();
    const result = hasIsEditableIndicators(state, stateLocation1);
    assert.notOk(result,
      'hasIsEditableIndicators shall return false if filteredList does not contain any items with isEditable property');
  });

test('editItem returns editItem for stateLocation', function(assert) {
  const state = new ReduxDataHelper()
    .stateLocation(stateLocation1)
    .list(list1)
    .listName(listName1)
    .editItemId(list1[randomIndex].id)
    .build();
  const result = editItem(state, stateLocation1);
  const expected = list1[randomIndex];
  assert.deepEqual(result, expected, 'Shall select editItem based on stateLocation, list, and editItemId');
});

test('editItem returns undefined for stateLocation if no editItemId', function(assert) {
  const state = new ReduxDataHelper()
    .stateLocation(stateLocation1)
    .list(list1)
    .listName(listName1)
    .editItemId(null)
    .build();
  const result = editItem(state, stateLocation1);
  assert.equal(result, undefined, 'Shall return undefined for editItem if editItemId does not exist');
});

test('isNewItem returns true if newItem in edit view', function(assert) {
  const state = new ReduxDataHelper()
    .stateLocation(stateLocation1)
    .list(list1)
    .listName(listName1)
    .viewName(DETAILS_VIEW)
    .editItemId(null)
    .build();
  const result = isNewItem(state, stateLocation1);
  assert.equal(result, true, 'Shall return true if editItemId does not exist in edit view');
});

test('isNewItem returns false if editItem exists in edit view', function(assert) {
  const state = new ReduxDataHelper()
    .stateLocation(stateLocation1)
    .list(list1)
    .listName(listName1)
    .viewName(DETAILS_VIEW)
    .editItemId(list1[randomIndex].id)
    .build();
  const result = isNewItem(state, stateLocation1);
  assert.equal(result, false, 'Shall return false if editItemId exists in edit view');
});

test('shouldSelectedItemPersist returns shouldSelectedItemPersist for stateLocation', function(assert) {
  const state = new ReduxDataHelper()
    .stateLocation(stateLocation1)
    .list(list1)
    .listName(listName1)
    .shouldSelectedItemPersist(true)
    .build();
  const result = shouldSelectedItemPersist(state, stateLocation1);
  assert.ok(result, 'Shall select shouldSelectedItemPersist based on stateLocation');

  const state2 = new ReduxDataHelper()
    .stateLocation(stateLocation1)
    .list(list1)
    .listName(listName1)
    .shouldSelectedItemPersist(false)
    .build();
  const result2 = shouldSelectedItemPersist(state2, stateLocation1);
  assert.notOk(result2, 'Shall select shouldSelectedItemPersist based on stateLocation');
});
