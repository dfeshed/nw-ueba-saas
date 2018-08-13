import reselect from 'reselect';

const { createSelector } = reselect;

const policiesState = (state) => state.usm.policies;

export const isPoliciesLoading = createSelector(
  policiesState,
  (policiesState) => policiesState.itemsStatus === 'wait'
);

export const focusedPolicy = createSelector(
  policiesState,
  (policiesState) => policiesState.focusedItem
);
