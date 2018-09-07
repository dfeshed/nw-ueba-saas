import reselect from 'reselect';
import { isBlank, isPresent } from '@ember/utils';
import { exceedsLength } from './util/selector-helpers';

const { createSelector } = reselect;

const policyWizardState = (state) => state.usm.policyWizard;

const isExistingName = (name) => {
  if (isPresent(name)) {
    // This will be added when backend api is in master. Separate PR
    // return isPresent(policySummaries) && !!policySummaries.findBy('name', name);
    return false;
  }
};

/**
 * the policy object to be created/updated/saved
 * @public
 */
export const policy = createSelector(
  policyWizardState,
  (policyWizardState) => policyWizardState.policy
);

export const isPolicyLoading = createSelector(
  policyWizardState,
  (policyWizardState) => policyWizardState.policyStatus === 'wait'
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
 * - isError, errorMessage
 * @public
 */
export const nameValidator = createSelector(
  policy, visited,
  (policy, visited) => {
    let error = false;
    let enableMessage = false;
    let message = '';
    if (isBlank(policy.name)) {
      error = true;
      // only blank value requires visited
      if (visited.includes('policy.name')) {
        enableMessage = true;
        message = 'adminUsm.policyWizard.nameRequired';
      }
    } else if (exceedsLength(policy.name, 256)) {
      error = true;
      enableMessage = true;
      message = 'adminUsm.policyWizard.nameExceedsMaxLength';
    } else if (isExistingName(policy.name)) {
      error = true;
      enableMessage = true;
      message = 'adminUsm.policyWizard.nameExists';
    }
    return {
      isError: error,
      showError: enableMessage,
      errorMessage: message
    };
  }
);

/**
 * returns a description validator object with values set for
 * - isError, errorMessage
 * @public
 */
export const descriptionValidator = createSelector(
  policy,
  (policy) => {
    let error = false;
    let enableMessage = false;
    let message = '';
    if (exceedsLength(policy.description, 8000)) {
      error = true;
      enableMessage = true;
      message = 'adminUsm.policyWizard.descriptionExceedsMaxLength';
    }
    return {
      isError: error,
      showError: enableMessage,
      errorMessage: message
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
