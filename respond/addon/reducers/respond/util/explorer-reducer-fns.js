import { handle } from 'redux-pack';
import { isEmberArray } from 'ember-array/utils';
import { SINCE_WHEN_TYPES_BY_NAME } from 'respond/utils/since-when-types';

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

/**
 * When handling updates, we need to identify the ids of all the updated entities as well as the precise updates
 * that were made to those entities. This utility function takes an update payload response (from either a regular promise,
 * or a settled set of promises), and pulls out the entityIds and updates that were made.
 * @param payload
 * @returns {{entityIds: *, updates: (*|live.updates|{title}|query.updates|{})}}
 * @private
 */
export const _extractEntityIdsAndUpdates = (payload) => {
  // The payload can come in as an array (when multiple requests are being settled) or as an object (normal promise response)
  // To make things easier, we normalize an object payload into the format of the array payload
  if (!isEmberArray(payload)) {
    payload = [{ value: { ...payload } }];
  }
  return {
    entityIds: payload.reduce((ids, { value: { request } }) => ids.concat(request.entityIds), []),
    updates: payload[0].value.request.updates // the updates from each settled request should the same, so just grab them from the first
  };
};

// Updates the state value with the value updated on the server
const _handleUpdates = (action) => {
  return (state) => {
    const { payload } = action;
    const { entityIds, updates } = _extractEntityIdsAndUpdates(payload);

    const updatedEntities = state.items.map((entity) => {
      const updatedEntity = entityIds.includes(entity.id) ? { ...entity, ...updates } : entity;
      // reset the focus item to the newly updated entity, if it exists
      if (state.focusedItem && state.focusedItem.id === updatedEntity.id) {
        state.set('focusedItem', updatedEntity);
      }
      return updatedEntity;
    });
    return state.set('items', updatedEntities);
  };
};

// Remove the items that were deleted from the items array in app state. Also remove focusedItem if it was one of the deleted
const _handleDeletes = (action) => {
  return (state) => {
    const { items, focusedItem, itemsSelected, itemsTotal } = state;
    const { payload } = action;
    let removedItemIds = [];

    // If the payload is an array, we had multiple promises (deletion requests) being settled, each of which has its own payload/resolved value
    if (isEmberArray(payload)) {
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
    success: (s) => (s.merge({
      items: action.payload.data,
      itemsStatus: 'complete'
    }))
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
  return state.merge({
    items: [...state.items, ...data],
    itemsStatus: meta.complete ? 'complete' : 'streaming'
  });
};

const fetchItemsStreamCompleted = (state) => {
  return state.set('stopItemsStream', null);
};

const fetchItemsStreamError = (state) => {
  return state.set('stopItemsStream', null);
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
  return state.merge({
    // if item toggled is currently focused, remove from focus, otherwise set new item to focus
    focusedItem: state.focusedItem === item ? null : item
  });
};

const clearFocusItem = (state) => {
  // if item toggled is currently focused, remove from focus, otherwise set new item to focus
  return state.set('focusedItem', null);
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

  return state.merge({
    // if one item was deselected when in select all state, reset isSelectAll to false
    isSelectAll: state.isSelectAll && itemDeselected ? false : state.isSelectAll,
    itemsSelected
  });
};

const toggleSelectAll = (state) => {
  return state.merge({
    isSelectAll: !state.isSelectAll,
    itemsSelected: !state.isSelectAll ? state.items.map((item) => item.id) : []
  });
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