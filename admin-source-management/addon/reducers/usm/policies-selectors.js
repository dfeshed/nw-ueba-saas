import reselect from 'reselect';

const { createSelector } = reselect;

const policiesState = (state) => state.usm.policies;

export const policies = createSelector(
  policiesState,
  (policiesState) => policiesState.items
);

export const isPolicyListLoading = createSelector(
  policiesState,
  (policiesState) => policiesState.itemsStatus === 'wait'
);


