import reselect from 'reselect';
import { isPresent } from '@ember/utils';

const { createSelector } = reselect;

const _groupsState = (state) => state.usm.groups;
export const groups = (state) => state.usm.groups.items;
export const selectedGroups = (state) => state.usm.groups.itemsSelected;
export const focusedGroup = (state) => state.usm.groups.focusedItem;

export const isGroupsLoading = createSelector(
  _groupsState,
  (_groupsState) => _groupsState.itemsStatus === 'wait'
);

export const hasSelectedApplyPoliciesItems = createSelector(
  selectedGroups,
  (items) => {
    if (items) {
      // return (items.length == 1);
      return false;  /* for now */
    }
  }
);

export const selectedEditItem = createSelector(
  selectedGroups,
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
  selectedGroups,
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
  selectedGroups, groups,
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
