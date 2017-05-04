import { handle } from 'redux-pack';

function defaultCustomDateRange() {
  return {
    start: null,
    end: null
  };
}

function defaultDateRange() {
  return {
    'name': 'ALL_TIME',
    'unit': 'years',
    'subtract': 50
  };
}

const itemsFilters = () => ({
  status: [],
  priority: [],
  created: defaultDateRange()
});

// Updates the state value with the value updated on the server
const _handleUpdates = (action) => {
  return (state) => {
    const { payload: { request: { updates, entityIds } } } = action;
    const updatedEntities = state.items.map((entity) => {
      const updatedEntity = entityIds.includes(entity.id) ? { ...entity, ...updates } : entity;
      // reset the focus item to the newly updated entity, if it exists
      if (state.focusedItem && state.focusedItem.id === updatedEntity.id) {
        state.focusedItem = updatedEntity;
      }
      return updatedEntity;
    });
    return {
      ...state,
      items: updatedEntities
    };
  };
};

const fetchItems = (state, action) => (
  handle(state, action, {
    start: (s) => ({ ...s, itemsStatus: 'wait' }),
    success: (s) => ({
      ...s,
      items: action.payload.data,
      itemsStatus: 'complete'
    })
  })
);

const fetchItemCount = (state, action) => (
  handle(state, action, {
    start: (s) => ({ ...s, itemsTotal: '--' }),
    success: (s) => ({
      ...s,
      itemsTotal: action.payload.data
    })
  })
);

const updateItem = (state, action) => (
  handle(state, action, {
    start: (s) => ({ ...s, isTransactionUnderway: true }),
    success: _handleUpdates(action),
    failure: (s) => ({ ...s }),
    finish: (s) => ({ ...s, isTransactionUnderway: false })
  })
);

const updateFilter = (state, { payload }) => (
  {
    ...state,
    itemsFilters: {
      ...state.itemsFilters,
      ...payload
    }
  }
);

const toggleFilterPanel = (state) => ({
  ...state,
  isFilterPanelOpen: !state.isFilterPanelOpen
});

const toggleCustomDateRestriction = (state) => {
  return {
    ...state,
    hasCustomDateRestriction: !state.hasCustomDateRestriction,
    itemsFilters: {
      ...state.itemsFilters,
      created: !state.hasCustomDateRestriction ? defaultCustomDateRange() : defaultDateRange()
    }
  };
};

const resetFilters = (state) => (
  {
    ...state,
    itemsFilters: itemsFilters(),
    hasCustomDateRestriction: false
  }
);

const toggleFocusItem = (state, { payload: item }) => ({
  ...state,
  // if item toggled is currently focused, remove from focus, otherwise set new item to focus
  focusedItem: state.focusedItem === item ? null : item
});

const clearFocusItem = (state) => ({
  ...state,
  // if item toggled is currently focused, remove from focus, otherwise set new item to focus
  focusedItem: null
});

const toggleSelectItem = (state, { payload: item }) => {
  let itemDeselected = false;

  if (!item) {
    return state;
  }
  const { itemsSelected } = state;
  const index = itemsSelected.indexOf(item);

  if (index > -1) {
    itemsSelected.removeAt(index);
    itemDeselected = true;
  } else {
    itemsSelected.pushObject(item);
  }

  return {
    ...state,
    // if one item was deselected when in select all state, reset isSelectAll to false
    isSelectAll: state.isSelectAll && itemDeselected ? false : state.isSelectAll,
    itemsSelected: [...itemsSelected]
  };
};

const toggleSelectAll = (state) => {
  return {
    ...state,
    isSelectAll: !state.isSelectAll,
    itemsSelected: !state.isSelectAll ? state.items.map((item) => item.id) : []
  };
};

const sortBy = (state, { payload: { sortField, isSortDescending } }) => ({
  ...state,
  sortField,
  isSortDescending
});

export default {
  itemsFilters,
  defaultDateRange,
  defaultCustomDateRange,
  _handleUpdates,
  fetchItems,
  fetchItemCount,
  updateItem,
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