import { module, test } from 'qunit';
import {
  highlightedIndex,
  isListManagerReady,
  list,
  listName,
  filteredList,
  itemType,
  newItemButtonTitle,
  isExpanded,
  filterText,
  selectedItem,
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
  hasIsEditableIndicators
} from 'rsa-list-manager/selectors/list-manager/selectors';
import { LIST_VIEW } from 'rsa-list-manager/constants/list-manager';
import ReduxDataHelper from '../../../helpers/redux-data-helper';

module('Unit | Selectors | list-manager');

const listLocation1 = 'listManager';
const listName1 = 'Some Items';
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

const item1 = { id: 2, name: 'bar', subItems: [ 'e', 'b', 'c' ] };
const viewName1 = 'list-view';

test('highlightedIndex returns highlightedIndex for listLocation', function(assert) {
  const randomIndex = Math.floor(Math.random() * 20) + 1;
  const state = new ReduxDataHelper()
    .highlightedIndex(randomIndex)
    .build();
  const indexSelected = highlightedIndex(state, listLocation1);
  assert.equal(indexSelected, randomIndex, 'Shall select highlightedIndex based on listLocation');
});

test('listName returns listName for listLocation', function(assert) {
  const state = new ReduxDataHelper()
    .listName(listName1)
    .build();
  const listNameSelected = listName(state, listLocation1);
  assert.equal(listNameSelected, listName1, 'Shall select listName based on listLocation');
});

test('isListManagerReady returns true if listLocation exists', function(assert) {
  const state = new ReduxDataHelper()
    .listLocation(listLocation1)
    .build();
  const isReady = isListManagerReady(state, listLocation1);
  assert.ok(isReady, 'isListManagerReady shall be true if listLocation exists');
});

test('isListManagerReady returns false if listLocation does not exist', function(assert) {
  const state = new ReduxDataHelper().build();
  const isReady = isListManagerReady(state, listLocation1);
  assert.notOk(isReady, 'isListManagerReady shall be false if listLocation does not exist');
});

test('list returns list for listLocation', function(assert) {
  const state = new ReduxDataHelper()
    .listLocation(listLocation1)
    .list(list1)
    .build();
  const listSelected = list(state, listLocation1);
  assert.deepEqual(listSelected, list1, 'Shall select list based on listLocation');
});

test('filteredList returns filteredList for listLocation and filterText', function(assert) {
  const filterText1 = 'fo';
  const filteredList1 = list1.filter((item) => item.name.toLowerCase().includes(filterText1));
  const state = new ReduxDataHelper()
    .listLocation(listLocation1)
    .list(list1)
    .filterText(filterText1)
    .build();
  const filtered = filteredList(state, listLocation1);
  assert.deepEqual(filtered, filteredList1, 'Shall select filteredList based on listLocation and filterText');
});

test('filteredList returns unfiltered list for listLocation if no filterText', function(assert) {
  const filterText1 = '';
  const state = new ReduxDataHelper()
    .listLocation(listLocation1)
    .list(list1)
    .filterText(filterText1)
    .build();
  const filtered = filteredList(state, listLocation1);
  assert.deepEqual(filtered, state.listManager.list, 'filteredList shall return list based on listLocation if no filterText');
});

test('itemType returns itemType for listLocation and listName', function(assert) {
  const state = new ReduxDataHelper()
    .listLocation(listLocation1)
    .list(list1)
    .listName(listName1)
    .build();
  const itemTypeSelected = itemType(state, listLocation1);
  assert.deepEqual(itemTypeSelected, listName1.slice(0, -1), 'Shall select itemType based on listLocation and listName');
});

test('newItemButtonTitle returns newItemButtonTitle for listLocation and listName', function(assert) {
  const state = new ReduxDataHelper()
    .listLocation(listLocation1)
    .list(list1)
    .listName(listName1)
    .build();
  const buttonTitle = newItemButtonTitle(state, listLocation1);
  const itemType1 = listName1.slice(0, -1);
  assert.equal(buttonTitle, `New ${itemType1}`, 'Shall select newItemButtonTitle based on listLocation and listName');
});

test('isExpanded returns isExpanded for listLocation', function(assert) {
  const state = new ReduxDataHelper()
    .listLocation(listLocation1)
    .isExpanded(false)
    .build();
  const expanded = isExpanded(state, listLocation1);
  assert.notOk(expanded, 'Shall select isExpanded based on listLocation');
});

test('filterText returns filterText for listLocation', function(assert) {
  const state = new ReduxDataHelper()
    .listLocation(listLocation1)
    .filterText('someText')
    .build();
  const text = filterText(state, listLocation1);
  assert.equal(text, 'someText', 'Shall select filterText based on listLocation');
});

test('selectedItem returns selectedItem for listLocation', function(assert) {
  const state = new ReduxDataHelper()
    .listLocation(listLocation1)
    .selectedItem(item1)
    .build();
  const item = selectedItem(state, listLocation1);
  assert.deepEqual(item, item1, 'Shall select selectedItem based on listLocation');
});

test('viewName returns viewName for listLocation', function(assert) {
  const state = new ReduxDataHelper()
    .listLocation(listLocation1)
    .viewName(viewName1)
    .build();
  const view = viewName(state, listLocation1);
  assert.equal(view, viewName1, 'Shall select viewName based on listLocation');
});

test('highlightedId returns highlightedId for listLocation', function(assert) {
  const list2 = [
    { id: 'a', name: 'eba', subItems: [ 'a', 'b', 'c' ] },
    { id: 'b', name: 'foo', subItems: [ 'a', 'b' ] },
    { id: 'c', name: 'bar', subItems: [ 'e', 'b', 'c' ] },
    { id: 'd', name: 'Baz', subItems: [ 'c' ] }
  ];

  const index = Math.floor(Math.random() * Math.floor(4));
  const state = new ReduxDataHelper()
    .listLocation(listLocation1)
    .list(list2)
    .highlightedIndex(index)
    .build();
  const id = highlightedId(state, listLocation1);
  assert.equal(id, list2[index].id, 'Shall select viewName based on listLocation');
});

test('isListView returns true if viewName is list-view', function(assert) {
  const state = new ReduxDataHelper()
    .viewName(LIST_VIEW)
    .build();
  const result = isListView(state, listLocation1);
  assert.ok(result, 'isListView shall return true if viewName is list-view');
});

test('isListView returns false if viewName is not list-view', function(assert) {
  const state = new ReduxDataHelper()
    .viewName('some-view')
    .build();
  const result = isListView(state, listLocation1);
  assert.notOk(result, 'isListView shall return false if viewName is not list-view');
});

test('noResultsMessage returns noResultsMessage for listLocation and listName', function(assert) {
  const state = new ReduxDataHelper()
    .listName('apples')
    .build();
  const result = noResultsMessage(state, listLocation1);
  const expected = 'All apples have been excluded by the current filter';
  assert.equal(result, expected, 'Shall select noResultsMessage based on listLocation and listName');
});

test('selectedIndex returns selectedIndex for listLocation and selectedItem', function(assert) {
  const randomIndex = Math.floor(Math.random() * Math.floor(list1.length));
  const state = new ReduxDataHelper()
    .listLocation(listLocation1)
    .list(list1)
    .listName(listName1)
    .selectedItem(list1[randomIndex])
    .build();
  const index = selectedIndex(state, listLocation1);
  assert.equal(index, randomIndex, 'Shall select selectedIndex based on listLocation and selectedItem');
});

test('selectedIndex returns -1 if there is no selectedItem', function(assert) {
  const state = new ReduxDataHelper()
    .listLocation(listLocation1)
    .list(list1)
    .listName(listName1)
    .selectedItem(null)
    .build();
  const index = selectedIndex(state, listLocation1);
  assert.equal(index, -1, 'selectedIndex shall return -1 if there is no selectedItem');
});

test('helpId returns helpId for listLocation', function(assert) {
  const helpId1 = { moduleId: 'investigation', topicId: 'eaColumnGroups' };
  const state = new ReduxDataHelper()
    .listLocation(listLocation1)
    .list(list1)
    .listName(listName1)
    .helpId(helpId1)
    .build();
  const result = helpId(state, listLocation1);
  assert.deepEqual(result, helpId1, 'Shall select filterText based on listLocation');
});

test('hasContextualHelp returns true for listLocation if helpId has moduleId and topicId', function(assert) {
  const helpId1 = { moduleId: 'investigation', topicId: 'eaColumnGroups' };
  const state = new ReduxDataHelper()
    .listLocation(listLocation1)
    .list(list1)
    .listName(listName1)
    .helpId(helpId1)
    .build();
  const result = hasContextualHelp(state, listLocation1);
  assert.ok(result, 'hasContextualHelp shall return true if moduleId and topicId both exist in helpId');
});

test('hasContextualHelp returns false for listLocation if helpId is missing moduleId or topicId', function(assert) {
  const helpId1 = { moduleId: null, topicId: 'eaColumnGroups' };
  const state = new ReduxDataHelper()
    .listLocation(listLocation1)
    .list(list1)
    .listName(listName1)
    .helpId(helpId1)
    .build();
  const result = hasContextualHelp(state, listLocation1);
  assert.notOk(result, 'hasContextualHelp shall return false if moduleId does not exist in helpId');

  const helpId2 = { moduleId: 'investigation', topicId: null };
  const state2 = new ReduxDataHelper()
    .listLocation(listLocation1)
    .list(list1)
    .listName(listName1)
    .helpId(helpId2)
    .build();
  const result2 = hasContextualHelp(state2, listLocation1);
  assert.notOk(result2, 'hasContextualHelp shall return false if topicId does not exist in helpId');

  const helpId3 = { moduleId: null, topicId: null };
  const state3 = new ReduxDataHelper()
    .listLocation(listLocation1)
    .list(list1)
    .listName(listName1)
    .helpId(helpId3)
    .build();
  const result3 = hasContextualHelp(state3, listLocation1);
  assert.notOk(result3, 'hasContextualHelp shall return false if topicId and moduleId do not exist in helpId');
});

test('caption returns caption for listLocation, listName, selectedItem if selectedItem exists', function(assert) {
  const state = new ReduxDataHelper()
    .listLocation(listLocation1)
    .list(list1)
    .listName(listName1)
    .selectedItem(list1[0])
    .build();
  const result = caption(state, listLocation1);
  const expected = `${state.listManager.listName.slice(0, -1)}: ${list1[0].name}`;
  assert.equal(result, expected, 'Shall select caption based on listLocation, listName, selectedItem');
});

test('caption returns listName for listLocation if selectedItem does not exist', function(assert) {
  const state = new ReduxDataHelper()
    .listLocation(listLocation1)
    .list(list1)
    .listName(listName1)
    .selectedItem(null)
    .build();
  const result = caption(state, listLocation1);
  assert.equal(result, listName1, 'Shall select caption based on listLocation, listName, selectedItem');
});

test('titleTooltip returns titleTooltip for listLocation and selectedItem if selectedItem exists', function(assert) {
  const randomIndex = Math.floor(Math.random() * Math.floor(list1.length));
  const state = new ReduxDataHelper()
    .listLocation(listLocation1)
    .list(list1)
    .listName(listName1)
    .selectedItem(list1[randomIndex])
    .build();
  const result = titleTooltip(state, listLocation1);
  assert.equal(result, list1[randomIndex].name, 'Shall select titleTooltip based on listLocation and selectedItem');
});

test('titleTooltip returns null for listLocation if selectedItem does not exist', function(assert) {
  const state = new ReduxDataHelper()
    .listLocation(listLocation1)
    .list(list1)
    .listName(listName1)
    .selectedItem(null)
    .build();
  const result = titleTooltip(state, listLocation1);
  assert.notOk(result, 'Shall select titleTooltip based on listLocation and selectedItem');
});

test('filterPlaceholder returns filterPlaceholder for listLocation and listName', function(assert) {
  const state = new ReduxDataHelper()
    .listLocation(listLocation1)
    .list(list1)
    .listName(listName1)
    .build();
  const result = filterPlaceholder(state, listLocation1);
  const expected = `Filter ${listName1.toLowerCase()}`;
  assert.equal(result, expected, 'Shall select filterPlaceholder based on listLocation and listName');
});

test('hasIsEditableIndicators returns true for listLocation if filteredList contains items where isEditable is not undefined',
  function(assert) {
    const state = new ReduxDataHelper()
      .listLocation(listLocation1)
      .list(listHasIsEditable)
      .filterText('a')
      .listName(listName1)
      .build();
    const result = hasIsEditableIndicators(state, listLocation1);
    assert.ok(result, 'hasIsEditableIndicators shall return true if filteredList contains items with isEditable property');
  });

test('hasIsEditableIndicators returns false for listLocation if filteredList only contains items where isEditable is undefined',
  function(assert) {
    const state = new ReduxDataHelper()
      .listLocation(listLocation1)
      .list(listNotHasIsEditable)
      .filterText('a')
      .listName(listName1)
      .build();
    const result = hasIsEditableIndicators(state, listLocation1);
    assert.notOk(result,
      'hasIsEditableIndicators shall return false if filteredList does not contain any items with isEditable property');
  });
