import { handle } from 'redux-pack';
import { isArray } from '@ember/array';
import { SINCE_WHEN_TYPES_BY_NAME } from './since-when-types';

function defaultCustomDateRange() {
  return {
    start: null,
    end: null
  };
}

function defaultDateRange(state) {
  return { ...SINCE_WHEN_TYPES_BY_NAME[state.defaultDateRangeTypeName] };
}

const itemsFilters = (state) => ({
  [state.defaultDateFilterField]: defaultDateRange(state)
});

// Updates the state value with the value updated on the server
const _handleUpdates = (action) => {
  return (state) => {
    const { payload } = action;
    // The payload can come in as an array (when multiple requests are being settled) or as an object (normal promise response)
    // To make things easier, we normalize an object payload into the format of the array payload
    const normalizedPayload = !isArray(payload) ? [{ value: { ...payload } }] : payload;

    // The array of entities that have been updated reduced to a single array
    const updateData = normalizedPayload.reduce((updatedEntities, { value: { data } }) => {
      return updatedEntities.concat(data);
    }, []);

    // Find the updated entity from the list of updated objects, or use the current entity if not updated
    const updatedEntities = state.items.map((entity) => (updateData.findBy('id', entity.id) || entity));
    // Ensure the reference to the focus item is updated if there is one
    const focusedItem = state.focusedItem && updatedEntities.findBy('id', state.focusedItem.id);
    return state.merge({
      items: updatedEntities,
      focusedItem
    });
  };
};

// Remove the items that were deleted from the items array in app state. Also remove focusedItem if it was one of the deleted
const _handleDeletes = (action) => {
  return (state) => {
    const { items, focusedItem, itemsSelected, itemsTotal } = state;
    const { payload } = action;
    let removedItemIds = [];

    // If the payload is an array, we had multiple promises (deletion requests) being settled, each of which has its own payload/resolved value
    if (isArray(payload)) {
      removedItemIds = payload.reduce((removed, { value: { data } }) => removed.concat(data), []);
    } else { // a single promise (deletion request) resolved
      removedItemIds = payload.data;
    }

    // Filter out newly deleted items from the itemsSelected array
    const updatedItemsSelected = itemsSelected.filter((itemId) => (!removedItemIds.includes(itemId)));

    // Filter out newly deleted items from the main items array
    const updatedItems = items.filter((item) => (!removedItemIds.includes(item.id)));

    return state.merge({
      items: updatedItems,
      itemsSelected: updatedItemsSelected,
      // if we have a focused item and it's one that's being deleted, reset focusedItem to null
      focusedItem: focusedItem && removedItemIds.includes(focusedItem.id) ? null : focusedItem,
      // Update the itemsTotal count to account for the newly removed items
      itemsTotal: itemsTotal - removedItemIds.length,
      isSelectAll: false
    });
  };
};

const fetchItems = (state, action) => (
  handle(state, action, {
    start: (s) => s.merge({ itemsStatus: 'wait', focusedItem: null }),
    success: (s) => s.merge({
      items: action.payload.data,
      itemsStatus: 'complete'
    }),
    failure: (s) => s.set('itemsStatus', 'error')
  })
);

const fetchItemsStreamStarted = (state) => {
  return state.merge({
    items: [],
    itemsStatus: 'wait',
    focusedItem: null,
    itemsSelected: [],
    isSelectAll: false });
};

const fetchItemsStreamInitialized = (state, { payload }) => {
  return state.set('stopItemsStream', payload);
};

const fetchItemsStreamBatchRetrieved = (state, { payload: { data, meta } }) => {
  data = data || [];
  // if select all is active while the items are streaming in, ensure items in each new batch get selected
  const selectedIds = state.isSelectAll ? data.map((item) => item.id) : [];
  return state.merge({
    items: [...state.items, ...data],
    itemsSelected: [ ...state.itemsSelected, ...selectedIds ],
    itemsStatus: meta.complete ? 'complete' : 'streaming'
  });
};

const fetchItemsStreamCompleted = (state) => {
  return state.set('stopItemsStream', null);
};

const fetchItemsStreamError = (state) => {
  return state.merge({
    itemsStatus: 'error',
    stopItemsStream: null
  });
};

const fetchItemCount = (state, action) => (
  handle(state, action, {
    start: (s) => s.set('itemsTotal', '--'),
    success: (s) => s.set('itemsTotal', action.payload.meta ? action.payload.meta.total : action.payload.data)
  })
);

const updateItem = (state, action) => (
  handle(state, action, {
    start: (s) => s.set('isTransactionUnderway', true),
    success: _handleUpdates(action),
    failure: (s) => s,
    finish: (s) => s.set('isTransactionUnderway', false)
  })
);

const deleteItem = (state, action) => (
  handle(state, action, {
    start: (s) => s.set('isTransactionUnderway', true),
    success: _handleDeletes(action),
    failure: (s) => s,
    finish: (s) => s.set('isTransactionUnderway', false)
  })
);

const updateFilter = (state, { payload }) => {
  return state.set('itemsFilters', {
    ...state.itemsFilters,
    ...payload
  });
};

const toggleFilterPanel = (state) => {
  return state.set('isFilterPanelOpen', !state.isFilterPanelOpen);
};

const toggleCustomDateRestriction = (state) => {
  return state.merge({
    hasCustomDateRestriction: !state.hasCustomDateRestriction,
    itemsFilters: {
      ...state.itemsFilters,
      [state.defaultDateFilterField]: !state.hasCustomDateRestriction ? defaultCustomDateRange() : defaultDateRange(state)
    }
  });
};

const resetFilters = (state) => {
  return state.merge({
    itemsFilters: itemsFilters(state),
    hasCustomDateRestriction: false
  });
};

const toggleFocusItem = (state, { payload: item }) => {
  if (state.altRowSelection) {
    return state.merge({
      // Using Gmail like selection method,
      // unselect all items when focusing on a row except for the focused item
      focusedItem: state.focusedItem === item ? null : item,
      itemsSelected: state.focusedItem === item ? [] : [ item.id ],
      isSelectAll: false
    });
  } else {
    return state.merge({
      // if item toggled is currently focused, remove from focus, otherwise set new item to focus
      focusedItem: state.focusedItem === item ? null : item
    });
  }
};

const clearFocusItem = (state) => {
  if (state.altRowSelection) {
    return state.merge({
      // Using Gmail like selection method,
      // unselect all items including the focused item
      focusedItem: null,
      itemsSelected: [],
      isSelectAll: false
    });
  } else {
    // if item toggled is currently focused, remove from focus, otherwise set new item to focus
    return state.set('focusedItem', null);
  }
};

const toggleSelectItem = (state, { payload: item }) => {
  let itemDeselected = false;

  if (!item) {
    return state;
  }
  const itemsSelected = [...state.itemsSelected];
  const index = itemsSelected.indexOf(item);

  if (index > -1) {
    itemsSelected.removeAt(index);
    itemDeselected = true;
  } else {
    itemsSelected.pushObject(item);
  }

  if (state.altRowSelection) {
    return state.merge({
      // Using Gmail like selection method,
      // + unselect the focused item
      focusedItem: null,
      isSelectAll: state.isSelectAll && itemDeselected ? false : state.isSelectAll,
      itemsSelected
    });
  } else {
    return state.merge({
      // if one item was deselected when in select all state, reset isSelectAll to false
      isSelectAll: state.isSelectAll && itemDeselected ? false : state.isSelectAll,
      itemsSelected
    });
  }
};

const toggleSelectAll = (state) => {
  if (state.altRowSelection) {
    return state.merge({
      // Using Gmail like selection method,
      // + unselect the focused item
      focusedItem: null,
      isSelectAll: !state.isSelectAll,
      itemsSelected: !state.isSelectAll ? state.items.map((item) => item.id) : []
    });
  } else {
    return state.merge({
      // if one item was deselected when in select all state, reset isSelectAll to false
      isSelectAll: !state.isSelectAll,
      itemsSelected: !state.isSelectAll ? state.items.map((item) => item.id) : []
    });
  }
};

const sortBy = (state, { payload: { sortField, isSortDescending } }) => {
  return state.merge({
    sortField,
    isSortDescending
  });
};

export default {
  itemsFilters,
  defaultDateRange,
  defaultCustomDateRange,
  _handleUpdates,
  fetchItems,
  fetchItemsStreamStarted,
  fetchItemsStreamInitialized,
  fetchItemsStreamBatchRetrieved,
  fetchItemsStreamCompleted,
  fetchItemsStreamError,
  fetchItemCount,
  updateItem,
  deleteItem,
  updateFilter,
  toggleFilterPanel,
  toggleCustomDateRestriction,
  resetFilters,
  toggleFocusItem,
  clearFocusItem,
  toggleSelectItem,
  toggleSelectAll,
  sortBy
};
