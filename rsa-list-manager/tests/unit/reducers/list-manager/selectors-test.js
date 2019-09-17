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
  filterText
} from 'rsa-list-manager/selectors/list-manager/selectors';
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

test('highlightedIndex returns highlightedIndex for listLocation', function(assert) {
  const randomIndex = Math.floor(Math.random() * 20) + 1;
  const state = new ReduxDataHelper().highlightedIndex(randomIndex).build();
  const indexSelected = highlightedIndex(state, listLocation1);
  assert.equal(indexSelected, randomIndex, 'Shall select highlightedIndex based on listLocation');
});

test('listName returns listName for listLocation', function(assert) {
  const state = new ReduxDataHelper().listName(listName1).build();
  const listNameSelected = listName(state, listLocation1);
  assert.equal(listNameSelected, listName1, 'Shall select listName based on listLocation');
});

test('isListManagerReady returns true if listLocation exists', function(assert) {
  const state = new ReduxDataHelper().listLocation(listLocation1).build();
  const isReady = isListManagerReady(state, listLocation1);
  assert.ok(isReady, 'isListManagerReady shall be true if listLocation exists');
});

test('isListManagerReady returns false if listLocation does not exist', function(assert) {
  const state = new ReduxDataHelper().build();
  const isReady = isListManagerReady(state, listLocation1);
  assert.notOk(isReady, 'isListManagerReady shall be false if listLocation does not exist');
});

test('list returns list for listLocation', function(assert) {
  const state = new ReduxDataHelper().listLocation(listLocation1).list(list1).build();
  const listSelected = list(state, listLocation1);
  assert.deepEqual(listSelected, list1, 'Shall select list based on listLocation');
});

test('filteredList returns filteredList for listLocation and filterText', function(assert) {
  const filterText1 = 'fo';
  const filteredList1 = list1.filter((item) => item.name.toLowerCase().includes(filterText1));
  const state = new ReduxDataHelper().listLocation(listLocation1).list(list1).filterText(filterText1).build();
  const filtered = filteredList(state, listLocation1);
  assert.deepEqual(filtered, filteredList1, 'Shall select filteredList based on listLocation and filterText');
});

test('filteredList returns unfiltered list for listLocation if no filterText', function(assert) {
  const filterText1 = '';
  const state = new ReduxDataHelper().listLocation(listLocation1).list(list1).filterText(filterText1).build();
  const filtered = filteredList(state, listLocation1);
  assert.deepEqual(filtered, state.listManager.list, 'filteredList shall return list based on listLocation if no filterText');
});

test('itemType returns itemType for listLocation and listName', function(assert) {
  const state = new ReduxDataHelper().listLocation(listLocation1).list(list1).listName(listName1).build();
  const itemTypeSelected = itemType(state, listLocation1);
  assert.deepEqual(itemTypeSelected, listName1.slice(0, -1), 'Shall select itemType based on listLocation and listName');
});

test('newItemButtonTitle returns newItemButtonTitle for listLocation and listName', function(assert) {
  const state = new ReduxDataHelper().listLocation(listLocation1).list(list1).listName(listName1).build();
  const buttonTitle = newItemButtonTitle(state, listLocation1);
  const itemType1 = listName1.slice(0, -1);
  assert.equal(buttonTitle, `New ${itemType1}`, 'Shall select newItemButtonTitle based on listLocation and listName');
});

test('isExpanded returns isExpanded for listLocation', function(assert) {
  const state = new ReduxDataHelper().listLocation(listLocation1).isExpanded(false).build();
  const expanded = isExpanded(state, listLocation1);
  assert.notOk(expanded, 'Shall select isExpanded based on listLocation');
});

test('filterText returns filterText for listLocation', function(assert) {
  const state = new ReduxDataHelper().listLocation(listLocation1).filterText('someText').build();
  const text = filterText(state, listLocation1);
  assert.equal(text, 'someText', 'Shall select filterText based on listLocation');
});
