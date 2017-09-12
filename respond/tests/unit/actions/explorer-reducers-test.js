import Immutable from 'seamless-immutable';
import { module, test } from 'qunit';
import initialState from 'respond/reducers/respond/util/explorer-reducer-initial-state';
import reducers from 'respond/reducers/respond/util/explorer-reducer-fns';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../helpers/make-pack-action';

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
