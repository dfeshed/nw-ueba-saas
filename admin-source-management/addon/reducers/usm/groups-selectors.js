import reselect from 'reselect';

const { createSelector } = reselect;

const groupsState = (state) => state.usm.groups;

export const isGroupsLoading = createSelector(
  groupsState,
  (groupsState) => groupsState.itemsStatus === 'wait'
);

export const focusedGroup = createSelector(
  groupsState,
  (groupsState) => groupsState.focusedItem
);
