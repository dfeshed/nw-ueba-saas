import reselect from 'reselect';

const { createSelector } = reselect;

const groupsState = (state) => state.usm.groups;

export const groups = createSelector(
  groupsState,
  (groupsState) => groupsState.groups
);

export const isGroupsLoading = createSelector(
  groupsState,
  (groupsState) => groupsState.groupsStatus === 'wait'
);
