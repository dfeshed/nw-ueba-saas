import reselect from 'reselect';

const { createSelector } = reselect;

const _groupsState = (state) => state.usm.groups;

export const isGroupsLoading = createSelector(
  _groupsState,
  (_groupsState) => _groupsState.itemsStatus === 'wait'
);

export const focusedGroup = createSelector(
  _groupsState,
  (_groupsState) => _groupsState.focusedItem
);
