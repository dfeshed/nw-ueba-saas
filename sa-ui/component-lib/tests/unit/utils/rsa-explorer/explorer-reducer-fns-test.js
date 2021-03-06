import Immutable from 'seamless-immutable';
import { module, test } from 'qunit';
import initialState from 'component-lib/utils/rsa-explorer/explorer-reducer-initial-state';
import reducers from 'component-lib/utils/rsa-explorer/explorer-reducer-fns';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../helpers/make-pack-action';

module('Unit | Utility | Explorer Reducers');

test('The toggleFilterPanel() reducer toggles the isFilterPanelOpen state', function(assert) {
  const initState = Immutable.from(initialState);
  const isFilterPanelOpen = false;
  const expectedEndState = {
    ...initState,
    isFilterPanelOpen
  };
  const endState = reducers.toggleFilterPanel(initState);
  assert.deepEqual(endState, expectedEndState);
});

test('The updateFilter() reducer updates itemsFilters object', function(assert) {
  const initState = Immutable.from(initialState);
  const priority = ['LOW', 'CRITICAL'];
  const expectedEndState = {
    ...initState,
    itemsFilters: {
      ...initState.itemsFilters,
      priority
    }
  };

  const endState = reducers.updateFilter(initState, {
    payload: { priority }
  });
  assert.deepEqual(endState, expectedEndState);
});

test('The toggleFocusItem() reducer sets the item into focus and removes on alternate calls', function(assert) {
  const initState = Immutable.from(initialState);
  const focusedItem = {
    test: 'test'
  };
  const expectedEndState = {
    ...initState,
    focusedItem
  };
  // calling toggleFocusItem with a value will set it as the focusedItem
  const endState = reducers.toggleFocusItem(initState, { payload: focusedItem });
  assert.deepEqual(endState, expectedEndState);

  const toggledExpectedEndState = {
    ...endState,
    focusedItem: null
  };
  // calling toggleFocusItem again with the same value will set the focusedItem back to null/empty
  const toggledEndState = reducers.toggleFocusItem(endState, { payload: endState.focusedItem });
  assert.deepEqual(toggledEndState, toggledExpectedEndState);
});

test('The toggleSelectItem() reducer w/altRowSelection selects the item when not selected', function(assert) {
  const focusedItem = {
    id: 'item1',
    test: 'test'
  };
  const initState = Immutable.from({
    ...initialState,
    altRowSelection: true,
    focusedItem,
    itemsSelected: [ 'item2', 'item3' ],
    isSelectAll: false
  });
  const item = 'item1';
  const expectedEndState = {
    ...initState,
    focusedItem: null,
    itemsSelected: [ 'item2', 'item3', 'item1' ],
    isSelectAll: false
  };
  // calling toggleSelectItem with a value will set it as the focusedItem
  const endState = reducers.toggleSelectItem(initState, { payload: item });
  assert.deepEqual(endState, expectedEndState);
});

test('The toggleSelectItem() reducer w/altRowSelection deselects the item when selected', function(assert) {
  const focusedItem = {
    id: 'item1',
    test: 'test'
  };
  const initState = Immutable.from({
    ...initialState,
    altRowSelection: true,
    focusedItem,
    itemsSelected: [ 'item1', 'item2', 'item3' ],
    isSelectAll: false
  });
  const item = 'item1';
  const expectedEndState = {
    ...initState,
    focusedItem: null,
    itemsSelected: [ 'item2', 'item3' ],
    isSelectAll: false
  };
  // calling toggleSelectItem with a value will set it as the focusedItem
  const endState = reducers.toggleSelectItem(initState, { payload: item });
  assert.deepEqual(endState, expectedEndState);
});

test('The toggleFocusItem() reducer w/altRowSelection sets the item into focus and removes on alternate calls', function(assert) {
  const initState = Immutable.from({
    ...initialState,
    altRowSelection: true,
    itemsSelected: [ 'item1', 'item2', 'item3' ],
    isSelectAll: true
  });
  const focusedItem = {
    id: 'item1',
    test: 'test'
  };
  const expectedEndState = {
    ...initState,
    focusedItem,
    itemsSelected: [ 'item1' ],
    isSelectAll: false
  };
  // calling toggleFocusItem with a value will set it as the focusedItem
  const endState = reducers.toggleFocusItem(initState, { payload: focusedItem });
  assert.deepEqual(endState, expectedEndState);

  const toggledExpectedEndState = {
    ...endState,
    focusedItem: null,
    itemsSelected: [],
    isSelectAll: false
  };
  // calling toggleFocusItem again with the same value will set the focusedItem back to null/empty
  const toggledEndState = reducers.toggleFocusItem(endState, { payload: endState.focusedItem });
  assert.deepEqual(toggledEndState, toggledExpectedEndState);
});

test('The clearFocusItem() reducer updates removes any focusedItem', function(assert) {
  const initState = Immutable.from({
    ...initialState,
    focusedItem: { test: 'test' }
  });
  const expectedEndState = {
    ...initState,
    focusedItem: null
  };
  const endState = reducers.clearFocusItem(initState);
  assert.deepEqual(endState, expectedEndState);
});

test('The clearFocusItem() reducer w/altRowSelection updates removes any focusedItem', function(assert) {
  const initState = Immutable.from({
    ...initialState,
    altRowSelection: true,
    itemsSelected: [ 'item1' ],
    focusedItem: {
      id: 'item1',
      test: 'test'
    }
  });
  const expectedEndState = {
    ...initState,
    focusedItem: null,
    itemsSelected: [],
    isSelectAll: false
  };
  const endState = reducers.clearFocusItem(initState);
  assert.deepEqual(endState, expectedEndState);
});

test('The fetchItems() reducer updates the status property on startup', function(assert) {
  const initState = Immutable.from(initialState);
  const expectedEndState = {
    ...initState,
    itemsStatus: 'wait'
  };
  const action = makePackAction(LIFECYCLE.START, { });
  const endState = reducers.fetchItems(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('The fetchItems() reducer updates the items property on successful completion', function(assert) {
  const initState = Immutable.from(initialState);
  const items = [{ test: '1' }];
  const expectedEndState = {
    ...initState,
    items,
    itemsStatus: 'complete'
  };
  const action = makePackAction(LIFECYCLE.SUCCESS, { payload: { data: [{ test: '1' }] } });
  const endState = reducers.fetchItems(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('The fetchItemCount() reducer updates the itemsTotal property on startup', function(assert) {
  const initState = Immutable.from(initialState);
  const expectedEndState = {
    ...initState,
    itemsTotal: '--'
  };
  const action = makePackAction(LIFECYCLE.START, { });
  const endState = reducers.fetchItemCount(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('The fetchItemCount() reducer updates the itemsTotal property on successful completion', function(assert) {
  const initState = Immutable.from(initialState);

  const expectedEndState = {
    ...initState,
    itemsTotal: 11
  };
  const action = makePackAction(LIFECYCLE.SUCCESS, { payload: { data: 11 } });
  const endState = reducers.fetchItemCount(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('The fetchItemsStreamBatchRetrieved() reducer function updates items state', function(assert) {
  const initState = Immutable.from(initialState);
  const items = [{ id: 'INC-123', test: '1' }];
  const expectedEndState = {
    ...initState,
    items,
    itemsStatus: 'complete'
  };

  const endState = reducers.fetchItemsStreamBatchRetrieved(initState, { payload: { data: items, meta: { complete: true } } });
  assert.deepEqual(endState, expectedEndState);
});

test('The fetchItemsStreamBatchRetrieved() reducer function updates itemsSelected if isSelectAll is true', function(assert) {
  const initState = { ...initialState, isSelectAll: true };
  const items = [{ id: 'INC-123', test: '1' }];
  const expectedEndState = {
    ...initState,
    items,
    itemsStatus: 'complete',
    itemsSelected: ['INC-123']
  };

  const endState = reducers.fetchItemsStreamBatchRetrieved(Immutable.from(initState), { payload: { data: items, meta: { complete: true } } });
  assert.deepEqual(endState, expectedEndState);
});

test('The updateItem() reducer sets the "isTransactionUnderway" property to true at startup', function(assert) {
  const initState = Immutable.from(initialState);
  const expectedEndState = {
    ...initState,
    isTransactionUnderway: true
  };
  const action = makePackAction(LIFECYCLE.START, { });
  const endState = reducers.updateItem(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('The updateItem() reducer updates the items and focusedItem properties', function(assert) {
  const item = { id: 'INC-123', priority: 'LOW' };
  const updatedItem = { id: 'INC-123', priority: 'CRITICAL' };
  const payload = { data: [updatedItem] };
  const initState = {
    ...initialState,
    items: [item],
    focusedItem: item
  };
  const expectedEndState = {
    ...initState,
    items: [updatedItem],
    focusedItem: updatedItem
  };
  const action = makePackAction(LIFECYCLE.SUCCESS, { payload });
  const endState = reducers.updateItem(Immutable.from(initState), action);
  assert.deepEqual(endState, expectedEndState);
});

