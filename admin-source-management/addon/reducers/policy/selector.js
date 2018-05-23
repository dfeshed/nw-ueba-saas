import reselect from 'reselect';
import { isBlank } from '@ember/utils';

const { createSelector } = reselect;

const policyState = (state) => state.policy;

export const isPolicyListLoading = createSelector(
  policyState,
  (policyState) => policyState.policyStatus === 'wait'
);

export const isPolicyLoading = createSelector(
  policyState,
  (policyState) => policyState.policySaveStatus === 'wait'
);

export const currentPolicy = createSelector(
  policyState,
  (policyState) => policyState.policy
);

export const hasMissingRequiredData = createSelector(
  currentPolicy,
  (policy) => {
    return isBlank(policy.name);
  }
);

