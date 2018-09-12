import reselect from 'reselect';
import { isPresent } from '@ember/utils';

const { createSelector } = reselect;

const _policiesState = (state) => state.usm.policies;
const _allItems = (state) => state.usm.policies.items;
const _selectedItems = (state) => state.usm.policies.itemsSelected;

export const policies = createSelector(
  _allItems,
  (_allItems) => _allItems
);

export const isPoliciesLoading = createSelector(
  _policiesState,
  (_policiesState) => _policiesState.itemsStatus === 'wait'
);

export const focusedPolicy = createSelector(
  _policiesState,
  (_policiesState) => _policiesState.focusedItem
);

export const selectedEditItem = createSelector(
  _selectedItems,
  (items) => {
    if (isPresent(items) && items.length == 1) {
      const [item] = items;
      return item;
    } else {
      return 'none';
    }
  }
);

export const hasSelectedEditItem = createSelector(
  selectedEditItem,
  (item) => {
    return (isPresent(item) && (item !== 'none'));
  }
);

export const selectedDeleteItems = createSelector(
  _selectedItems, _allItems,
  (items, all) => {
    if (items && all) {
      return items.filter((selected) => !all.findBy('id', selected).defaultPolicy);
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
