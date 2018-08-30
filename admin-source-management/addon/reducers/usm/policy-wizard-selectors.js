import reselect from 'reselect';
import { isBlank } from '@ember/utils';

const { createSelector } = reselect;

const policyWizardState = (state) => state.usm.policyWizard;

/**
 * the policy object to be created/updated/saved
 * @public
 */
export const policy = createSelector(
  policyWizardState,
  (policyWizardState) => policyWizardState.policy
);

/**
 * form fields visited by the user
 * @public
 */
export const visited = createSelector(
  policyWizardState,
  (policyWizardState) => policyWizardState.visited
);

/**
 * all available policy sourceType objects
 * @public
 */
export const sourceTypes = createSelector(
  policyWizardState,
  (policyWizardState) => policyWizardState.sourceTypes
);

/**
 * we need the selected policy sourceType object, but policy.type only has the type string value,
 * so we'll find the policy sourceType object by the type string value
 * @public
 */
export const selectedSourceType = createSelector(
  policy, sourceTypes,
  (policy, sourceTypes) => {
    let selected = null;
    for (let s = 0; s < sourceTypes.length; s++) {
      const sourceType = sourceTypes[s];
      if (policy.policyType === sourceType.policyType) {
        selected = sourceType;
        break;
      }
    }
    return selected;
  }
);

/**
 * returns a name validator object with values set for
 * - isError, errorMessage, isVisited
 * @public
 */
export const nameValidator = createSelector(
  policy, visited,
  (policy, visited) => {
    return {
      isError: isBlank(policy.name),
      errorMessage: 'adminUsm.policyWizard.nameRequired',
      isVisited: visited.indexOf('policy.name') > -1
    };
  }
);

export const steps = createSelector(
  policyWizardState,
  (policyWizardState) => policyWizardState.steps
);

export const isIdentifyPolicyStepValid = createSelector(
  nameValidator,
  (nameValidator) => nameValidator.isError === false
);

// TODO implement real check
export const isDefinePolicyStepvalid = createSelector(
  policy,
  (policy) => policy.name === policy.name
);

// TODO implement real check
export const isApplyToGroupStepvalid = createSelector(
  policy,
  (policy) => policy.name === policy.name
);

// TODO implement real check
export const isReviewPolicyStepvalid = createSelector(
  policy,
  (policy) => policy.name === policy.name
);

export const isWizardValid = createSelector(
  isIdentifyPolicyStepValid, isDefinePolicyStepvalid, isApplyToGroupStepvalid, isReviewPolicyStepvalid,
  (isIdentifyPolicyStepValid, isDefinePolicyStepvalid, isApplyToGroupStepvalid, isReviewPolicyStepvalid) => {
    return isIdentifyPolicyStepValid && isDefinePolicyStepvalid &&
      isApplyToGroupStepvalid && isReviewPolicyStepvalid;
  }
);

export const isPolicyLoading = createSelector(
  policyWizardState,
  (policyWizardState) => policyWizardState.policyStatus === 'wait'
);
