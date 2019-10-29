import { test, module } from 'qunit';
import Immutable from 'seamless-immutable';
import makePackAction from '../../../helpers/make-pack-action';
import { LIFECYCLE } from 'redux-pack';
import * as ACTION_TYPES from 'rsa-list-manager/actions/types';
import reducer from 'rsa-list-manager/reducers/list-manager/reducer';
import { EDIT_VIEW, LIST_VIEW } from 'rsa-list-manager/constants/list-manager';

module('Unit | Reducers | list-manager');

const stateLocation1 = 'listManager';
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
      stateLocation: stateLocation1,
      listName: listName1,
      list: list1,
      selectedItemId: list1[0].id,
      helpId: helpId1
    }
  };
  const result = reducer(prevState, action);
  assert.equal(result.stateLocation, stateLocation1, 'stateLocation shall be set');
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
    payload: { editItemId: randomId }
  };

  const result = reducer(prevState, action);
  assert.equal(result.editItemId, randomId, 'editItemId shall be set correctly');
  assert.equal(result.viewName, EDIT_VIEW, 'viewName shall be set to edit-view');
});

test('Should set relevant properties correctly at start of creating new item', function(assert) {
  const previous = Immutable.from({});
  const startAction = makePackAction(LIFECYCLE.START, {
    type: ACTION_TYPES.ITEM_CREATE,
    payload: {
      data: {}
    }
  });

  const newEndState = reducer(previous, startAction);
  assert.equal(newEndState.isItemsLoading, true, 'isItemsLoading shall be set true');
  assert.equal(newEndState.createItemErrorCode, null, 'createItemErrorCode shall be null');
  assert.equal(newEndState.createItemErrorMessage, null, 'createItemErrorMessage shall be null');
});

test('Should set relevant properties correctly after successfully creating new item', function(assert) {
  const previous = Immutable.from({});
  const newItemName = `TEST-${Date.now().toString().substring(6)}`;
  const someItemAttribute = [ 'a', 'b', 'c' ];

  const successAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.ITEM_CREATE,
    payload: {
      data: {
        name: newItemName,
        id: '23',
        someItemAttribute
      }
    }
  });

  const newEndState = reducer(previous, successAction);
  assert.equal(newEndState.list[0].name, newItemName);
  assert.deepEqual(newEndState.list[0].someItemAttribute, someItemAttribute, 'item not transformed');
  assert.equal(newEndState.isItemsLoading, false, 'isItemsLoading shall be set false');
  assert.equal(newEndState.editItemId, '23', 'item created will be set for edit');
  assert.notOk(newEndState.createItemErrorCode, 'createItemErrorCode shall not be set');
  assert.notOk(newEndState.createItemErrorMessage, 'createItemErrorMessage shall not be set');
});


test('should transform created item if transform function present', function(assert) {
  const previous = Immutable.from({});
  const newItemName = `TEST-${Date.now().toString().substring(6)}`;
  const someItemAttribute = [ { foo: 'a', bar: 'b' }];
  const data = {
    name: newItemName,
    id: '23',
    someItemAttribute
  };

  const itemTransform = () => {
    return {
      name: newItemName,
      id: '23',
      someItemAttribute: [ { baz: 'a', taz: 'b' }]
    };
  };

  const successAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.ITEM_CREATE,
    payload: { data },
    meta: { itemTransform }
  });

  const newEndState = reducer(previous, successAction);
  assert.equal(newEndState.list[0].name, newItemName);
  assert.deepEqual(newEndState.list[0].someItemAttribute, [ { baz: 'a', taz: 'b' }], 'item transformed correctly');
  assert.equal(newEndState.isItemsLoading, false, 'isItemsLoading shall be set false');
  assert.equal(newEndState.editItemId, '23', 'item created will be set for edit');
  assert.notOk(newEndState.createItemErrorCode, 'createItemErrorCode shall not be set');
  assert.notOk(newEndState.createItemErrorMessage, 'createItemErrorMessage shall not be set');
});

test('Should set relevant properties correctly after failure to create new item', function(assert) {
  const previous = Immutable.from({
    list: []
  });

  const failureAction = makePackAction(LIFECYCLE.FAILURE, {
    type: ACTION_TYPES.ITEM_CREATE,
    payload: {
      meta: {
        message: 'TEST'
      },
      code: 999
    }
  });

  const newEndState = reducer(previous, failureAction);
  assert.equal(newEndState.isItemsLoading, false, 'isItemsLoading shall be set false');
  assert.equal(newEndState.createItemErrorCode, 999, 'createItemErrorCode shall be set');
  assert.equal(newEndState.createItemErrorMessage, 'TEST', 'createItemErrorMessage shall be set');
});

test('Should set relevant properties correctly at start of deleting item', function(assert) {

  const previous = Immutable.from({});

  const startAction = makePackAction(LIFECYCLE.START, {
    type: ACTION_TYPES.ITEM_DELETE,
    payload: {
      data: {}
    }
  });

  const newEndState = reducer(previous, startAction);
  assert.equal(newEndState.isItemsLoading, true, 'isItemsLoading shall be set true');
  assert.equal(newEndState.deleteItemErrorCode, null, 'deleteItemErrorCode shall be null');
  assert.equal(newEndState.deleteItemErrorMessage, null, 'deleteItemErrorMessage shall be null');
});

test('Should set relevant properties correctly after successfully deleting item', function(assert) {
  const id = `TEST-${Date.now().toString().substring(6)}`;
  const previous = Immutable.from({
    list: [
      {
        id,
        name: 'TEST',
        contentType: 'USER',
        columns: [{
          field: 'time',
          title: 'Collection Time',
          position: 0,
          width: 175
        },
        {
          field: 'service',
          title: 'Service Name',
          position: 1,
          width: 100
        }]
      },
      {
        id: '12345',
        name: 'TEST2',
        contentType: 'USER',
        columns: [{
          field: 'time',
          title: 'Collection Time',
          position: 0,
          width: 175
        }]
      }
    ]
  });

  const successAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.ITEM_DELETE,
    payload: {
      data: true,
      request: {
        id
      }
    }
  });

  const newEndState = reducer(previous, successAction);
  const found = newEndState.list.find((cg) => cg.id === id);
  assert.notOk(found, 'Successfully removed item from state');
  assert.equal(newEndState.isItemsLoading, false, 'isItemsLoading shall be set false');
  assert.notOk(newEndState.deleteItemErrorCode, 'deleteItemErrorCode shall not be set');
  assert.notOk(newEndState.deleteItemErrorMessage, 'deleteItemErrorMessage shall not be set');
  assert.notOk(newEndState.editItemId, 'editItemId shall be reset');
  assert.equal(newEndState.viewName, LIST_VIEW, 'viewName shall be set to list-view');
});

test('Should set relevant properties correctly after failure to delete item', function(assert) {
  const previous = Immutable.from({
    list: [
      {
        id: '12345',
        name: 'TEST',
        contentType: 'USER',
        columns: [{
          field: 'time',
          title: 'Collection Time',
          position: 0,
          width: 175
        },
        {
          field: 'service',
          title: 'Service Name',
          position: 1,
          width: 100
        }]
      },
      {
        id: '23456',
        name: 'TEST2',
        contentType: 'USER',
        columns: [{
          field: 'time',
          title: 'Collection Time',
          position: 0,
          width: 175
        }]
      }
    ]
  });

  const failureAction = makePackAction(LIFECYCLE.FAILURE, {
    type: ACTION_TYPES.ITEM_DELETE,
    payload: {
      meta: {
        message: 'TEST'
      },
      code: 999
    }
  });

  const newEndState = reducer(previous, failureAction);
  assert.equal(newEndState.isItemsLoading, false, 'isItemsLoading shall be set false');
  assert.deepEqual(previous.list, newEndState.list, 'columnGroups shall not change');
  assert.ok(newEndState.deleteItemErrorCode, 'deleteItemErrorCode shall be set');
  assert.ok(newEndState.deleteItemErrorMessage, 'deleteItemErrorMessage shall be set');
});

test('Should set relevant properties correctly at start of updating item', function(assert) {

  const previous = Immutable.from({
    list: null
  });

  const startAction = makePackAction(LIFECYCLE.START, {
    type: ACTION_TYPES.ITEM_UPDATE,
    payload: {
      data: {}
    }
  });

  const newEndState = reducer(previous, startAction);
  assert.equal(newEndState.isItemsLoading, true, 'isItemsLoading shall be set true');
  assert.equal(newEndState.updateItemErrorCode, null, 'updateItemErrorCode shall be null');
  assert.equal(newEndState.updateItemErrorMessage, null, 'updateItemErrorMessage shall be null');
});

test('Should set relevant properties correctly after successfully updating item', function(assert) {
  const id = `TEST-${Date.now().toString().substring(6)}`;
  const previous = Immutable.from({
    list: [
      {
        id,
        name: 'TEST',
        contentType: 'USER',
        columns: [{
          field: 'time',
          title: 'Collection Time',
          position: 0,
          width: 175
        },
        {
          field: 'service',
          title: 'Service Name',
          position: 1,
          width: 100
        }]
      },
      {
        id: '12345',
        name: 'TEST2',
        contentType: 'USER',
        columns: [{
          field: 'time',
          title: 'Collection Time',
          position: 0,
          width: 175
        }]
      }
    ]
  });
  const itemName = `UPDATED-${Date.now().toString().substring(6)}`;
  const itemAttribute = [{
    field: 'time1',
    title: 'Collection Time',
    position: 0,
    width: 175
  },
  {
    field: 'service1',
    title: 'Service Name',
    position: 1,
    width: 100
  }];

  const successAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.ITEM_UPDATE,
    payload: {
      data: {
        id,
        name: itemName,
        columns: itemAttribute,
        contentType: 'USER'
      }
    }
  });

  const newEndState = reducer(previous, successAction);
  const found = newEndState.list.find((cg) => cg.id === id && cg.name === itemName);
  assert.ok(found, 'Updated item found');
  assert.ok(found.columns[0].field, 'time1', 'Item has other attributes updated');
  assert.equal(newEndState.isItemsLoading, false, 'isItemsLoading shall be set false');
  assert.notOk(newEndState.updateItemErrorCode, 'updateItemErrorCode shall not be set');
  assert.notOk(newEndState.updateItemErrorMessage, 'updateItemErrorMessage shall not be set');
});

test('should transform updated item if transform function present', function(assert) {
  const someItemAttribute = [ { foo: 'a', bar: 'b' }];
  const originalItem = {
    name: 'OldName',
    id: '23',
    someItemAttribute
  };
  const previous = Immutable.from({
    list: [ originalItem ],
    editItemId: originalItem.id
  });

  const updatedItem = {
    name: 'NewName',
    id: originalItem.id,
    someItemAttribute
  };

  const itemTransform = (data) => {
    return {
      name: data.name,
      id: data.id,
      someItemAttribute: [ { baz: 'a', taz: 'b' }]
    };
  };

  const successAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.ITEM_UPDATE,
    payload: { data: updatedItem },
    meta: { itemTransform }
  });

  const newEndState = reducer(previous, successAction);
  assert.equal(newEndState.list[0].name, updatedItem.name);
  assert.deepEqual(newEndState.list[0].someItemAttribute, [ { baz: 'a', taz: 'b' }], 'item transformed correctly');
  assert.equal(newEndState.isItemsLoading, false, 'isItemsLoading shall be set false');
  assert.equal(newEndState.editItemId, '23', 'item created will be set for edit');
  assert.notOk(newEndState.createItemErrorCode, 'createItemErrorCode shall not be set');
  assert.notOk(newEndState.createItemErrorMessage, 'createItemErrorMessage shall not be set');
});


test('Should set relevant properties correctly after failure to update item', function(assert) {
  const previous = Immutable.from({
    list: [
      {
        id: '12345',
        name: 'TEST',
        contentType: 'USER',
        columns: [{
          field: 'time',
          title: 'Collection Time',
          position: 0,
          width: 175
        }]
      }
    ]
  });

  const failureAction = makePackAction(LIFECYCLE.FAILURE, {
    type: ACTION_TYPES.ITEM_UPDATE,
    payload: {
      meta: {
        message: 'TEST'
      },
      code: 999
    }
  });

  const newEndState = reducer(previous, failureAction);
  assert.equal(newEndState.isItemsLoading, false, 'isItemsLoading shall be set false');
  assert.deepEqual(previous.list, newEndState.list, 'columnGroups shall not change');
  assert.ok(newEndState.updateItemErrorCode, 'updateItemErrorCode shall be set');
  assert.ok(newEndState.updateItemErrorMessage, 'updateItemErrorMessage shall be set');
});
