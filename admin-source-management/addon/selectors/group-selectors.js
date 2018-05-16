import reselect from 'reselect';
import { isBlank } from '@ember/utils';

const { createSelector } = reselect;

const groupState = (state) => state.usm.group;

export const group = createSelector(
  groupState,
  (groupState) => groupState.group
);

export const isGroupLoading = createSelector(
  groupState,
  (groupState) => groupState.groupSaveStatus === 'wait'
);

export const hasMissingRequiredData = createSelector(
  group,
  (group) => {
    return isBlank(group.name);
  }
);
