import { test, module } from 'qunit';
import Immutable from 'seamless-immutable';

import * as ACTION_TYPES from 'rsa-list-manager/actions/types';
import reducer from 'rsa-list-manager/reducers/list-manager/reducer';
import { EDIT_VIEW } from 'rsa-list-manager/constants/list-manager';

module('Unit | Reducers | list-manager');

const listLocation1 = 'listManager';
const listName1 = 'Some List';
const list1 = [
  { id: 3, name: 'eba', subItems: [ 'a', 'b', 'c' ] },
  { id: 1, name: 'foo', subItems: [ 'a', 'b' ] },
  { id: 2, name: 'bar', subItems: [ 'e', 'b', 'c' ] },
  { id: 4, name: 'Baz', subItems: [ 'c' ] }
];
const helpId1 = { moduleId: 'investigation', topicId: 'eaColumnGroups' };

test('ACTION_TYPES.INITIALIZE_LIST_MANAGER updates values', function(assert) {
  const prevState = Immutable.from({
    stateLocation: undefined,
    listName: undefined,
    list: undefined,
    filterText: undefined,
    selectedItemId: undefined,
    helpId: undefined
  });

  const action = {
    type: ACTION_TYPES.INITIALIZE_LIST_MANAGER,
    payload: {
      stateLocation: listLocation1,
      listName: listName1,
      list: list1,
      selectedItemId: list1[0].id,
      helpId: helpId1
    }
  };
  const result = reducer(prevState, action);
  assert.equal(result.stateLocation, listLocation1, 'stateLocation shall be set');
  assert.equal(result.listName, listName1, 'listName shall be set');
  assert.equal(result.filterText, '', 'filterText shall be set');
  assert.deepEqual(result.list, list1, 'list shall be set');
  assert.deepEqual(result.selectedItemId, list1[0].id, 'selectedItemId shall be set');
  assert.deepEqual(result.helpId, helpId1, 'helpId shall be set');
});

test('ACTION_TYPES.SET_HIGHLIGHTED_INDEX sets highlightedIndex', function(assert) {
  const prevState = Immutable.from({
    highlightedIndex: -1
  });

  const randomIndex = Math.floor(Math.random() * 20) + 1;
  const action = {
    type: ACTION_TYPES.SET_HIGHLIGHTED_INDEX,
    payload: randomIndex
  };

  const result = reducer(prevState, action);
  assert.equal(result.highlightedIndex, randomIndex, 'highlightedIndex shall be set correctly');
});

test('ACTION_TYPES.TOGGLE_LIST_VISIBILITY toggles isExpanded and resets highlightedIndex and filterText',
  function(assert) {
    const initialValue = false;
    const prevState = Immutable.from({
      isExpanded: initialValue,
      highlightedIndex: 3,
      filterText: ''
    });

    const action = {
      type: ACTION_TYPES.TOGGLE_LIST_VISIBILITY
    };

    const result = reducer(prevState, action);
    assert.equal(result.isExpanded, !initialValue, 'isExpanded shall be set correctly');
    assert.equal(result.highlightedIndex, -1, 'highlightedIndex shall be reset');
    assert.equal(result.filterText, '', 'filterText shall be reset');
    const result2 = reducer(Immutable.from(result), action);
    assert.equal(result2.isExpanded, initialValue, 'isExpanded shall be set correctly');
    assert.equal(result2.highlightedIndex, -1, 'highlightedIndex shall be reset');
    assert.equal(result2.filterText, '', 'filterText shall be reset');
  });

test('ACTION_TYPES.SET_FILTER_TEXT sets filterText', function(assert) {
  const prevState = Immutable.from({
    filterText: undefined
  });

  const action = {
    type: ACTION_TYPES.SET_FILTER_TEXT,
    payload: 'some text'
  };

  const result = reducer(prevState, action);
  assert.equal(result.filterText, 'some text', 'filterText shall be set correctly');
});

test('ACTION_TYPES.SET_SELECTED_ITEM_ID sets selectedItemId', function(assert) {
  const prevState = Immutable.from({
    selectedItemId: undefined
  });

  const item1 = { id: 123, name: 'some item' };

  const action = {
    type: ACTION_TYPES.SET_SELECTED_ITEM_ID,
    payload: item1.id
  };

  const result = reducer(prevState, action);
  assert.deepEqual(result.selectedItemId, item1.id, 'selectedItemId shall be set correctly');
});

test('ACTION_TYPES.SET_VIEW_NAME sets viewName and resets editItemId', function(assert) {
  const prevState = Immutable.from({
    viewName: undefined,
    editItemId: 1111
  });

  const action = {
    type: ACTION_TYPES.SET_VIEW_NAME,
    payload: 'some-view'
  };

  const result = reducer(prevState, action);
  assert.equal(result.viewName, 'some-view', 'viewName shall be set correctly');
  assert.notOk(result.editItemId, 'editItemId shall be reset');
});

test('ACTION_TYPES.EDIT_ITEM sets editItemId and viewName', function(assert) {
  const randomId = Math.floor(Math.random() * 1000) + 1;

  const prevState = Immutable.from({
    editItemId: null,
    viewName: 'some-view'
  });

  const action = {
    type: ACTION_TYPES.EDIT_ITEM,
    payload: randomId
  };

  const result = reducer(prevState, action);
  assert.equal(result.editItemId, randomId, 'editItemId shall be set correctly');
  assert.equal(result.viewName, EDIT_VIEW, 'viewName shall be set to edit-view');
});
