import reselect from 'reselect';

const { createSelector } = reselect;

const _groupsState = (state) => state.usm.groups;
const _allItems = (state) => state.usm.groups.items;
const _selectedItems = (state) => state.usm.groups.itemsSelected;

export const isGroupsLoading = createSelector(
  _groupsState,
  (_groupsState) => _groupsState.itemsStatus === 'wait'
);

export const focusedGroup = createSelector(
  _groupsState,
  (_groupsState) => _groupsState.focusedItem
);

export const hasSelectedApplyPoliciesItems = createSelector(
  _selectedItems,
  (items) => {
    if (items) {
      // return (items.length == 1);
      return false;  /* for now */
    }
  }
);

export const selectedDeleteItems = createSelector(
  _selectedItems,
  (items) => {
    if (items) {
      return items;
    }
  }
);

export const hasSelectedDeleteItems = createSelector(
  selectedDeleteItems,
  (items) => {
    if (items) {
      return (items.length > 0);
    }
  }
);

export const selectedPublishItems = createSelector(
  _selectedItems, _allItems,
  (items, all) => {
    if (items && all) {
      return items.filter((selected) => all.findBy('id', selected).dirty);
    }
  }
);

export const hasSelectedPublishItems = createSelector(
  selectedPublishItems,
  (items) => {
    if (items) {
      return (items.length > 0);
    }
  }
);
