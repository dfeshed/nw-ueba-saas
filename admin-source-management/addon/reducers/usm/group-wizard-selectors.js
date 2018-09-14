import reselect from 'reselect';
import { isBlank, isPresent } from '@ember/utils';
import { exceedsLength } from './util/selector-helpers';

const { createSelector } = reselect;

const _groupWizardState = (state) => state.usm.groupWizard;
export const group = (state) => _groupWizardState(state).group;
export const groupCriteria = (state) => _groupWizardState(state).group.groupCriteria.criteria;
export const visited = (state) => _groupWizardState(state).visited;
export const steps = (state) => _groupWizardState(state).steps;
export const groupAttributesMap = (state) => _groupWizardState(state).groupAttributesMap;
const isExistingName = (name) => {
  if (isPresent(name)) {
    // This will be added when backend api is in master. Separate PR
    // return isPresent(groupSummaries) && !!groupSummaries.findBy('name', name);
    return false;
  }
};

export const isGroupLoading = createSelector(
  _groupWizardState,
  (groupWizardState) => {
    return groupWizardState.groupStatus === 'wait' ||
      groupWizardState.initGroupFetchPoliciesStatus === 'wait';
  }
);

export const hasMissingRequiredData = createSelector(
  group,
  (group) => {
    return isBlank(group.name);
  }
);

/**
 * returns a name validator object with values set for
 * - isError, errorMessage, isVisited
 * @public
 */
export const nameValidator = createSelector(
  group, visited,
  (group, visited) => {
    let error = false;
    let enableMessage = false;
    let message = '';
    if (isBlank(group.name)) {
      error = true;
      // only blank value requires visited
      if (visited.includes('group.name')) {
        enableMessage = true;
        message = 'adminUsm.groupWizard.nameRequired';
      }
    } else if (exceedsLength(group.name, 256)) {
      error = true;
      enableMessage = true;
      message = 'adminUsm.groupWizard.nameExceedsMaxLength';
    } else if (isExistingName(group.name)) {
      error = true;
      enableMessage = true;
      message = 'adminUsm.groupWizard.nameExists';
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
 * - isError, errorMessage, isVisited
 * @public
 */
export const descriptionValidator = createSelector(
  group,
  (group) => {
    let error = false;
    let enableMessage = false;
    let message = '';
    if (exceedsLength(group.description, 8000)) {
      error = true;
      enableMessage = true;
      message = 'adminUsm.groupWizard.descriptionExceedsMaxLength';
    }
    return {
      isError: error,
      showError: enableMessage,
      errorMessage: message
    };
  }
);

/**
 * we need the selected policy object, but group.policy has a map of { 'type': 'policyID' } ( ex. { 'edrPolicy': 'id_abc123' } ),
 * so we'll find the policy object by the type:ID map for the selected policy
 * @public
 */
// export const selectedPolicy = createSelector(
//   group, policies,
//   (group, policies) => {
//     let selected = null;
//     if (group.assignedPolicies) {
//       for (let p = 0; p < policies.length; p++) {
//         const policy = policies[p];
//         if (group.assignedPolicies.hasOwnProperty(policy.policyType) &&
//             group.assignedPolicies[policy.policyType]) {
//           const groupPolicyId = group.assignedPolicies[policy.policyType].referenceId;
//           if (policy.id === groupPolicyId) {
//             selected = policy;
//             break;
//           }
//         }
//       }
//     }
//     return selected;
//   }
// );

export const isIdentifyGroupStepValid = createSelector(
  nameValidator, descriptionValidator,
  (nameValidator, descriptionValidator) => {
    return nameValidator.isError === false && descriptionValidator.isError === false;
  }
);

// TODO implement real check
export const isDefineGroupStepvalid = createSelector(
  group,
  (group) => group.name === group.name
);

// TODO implement real check
export const isApplyPolicyStepvalid = createSelector(
  group,
  (group) => group.name === group.name
);

// TODO implement real check
export const isReviewGroupStepvalid = createSelector(
  group,
  (group) => group.name === group.name
);

export const isWizardValid = createSelector(
  isIdentifyGroupStepValid, isDefineGroupStepvalid, isApplyPolicyStepvalid, isReviewGroupStepvalid,
  (isIdentifyGroupStepValid, isDefineGroupStepvalid, isApplyPolicyStepvalid, isReviewGroupStepvalid) => {
    return isIdentifyGroupStepValid && isDefineGroupStepvalid &&
      isApplyPolicyStepvalid && isReviewGroupStepvalid;
  }
);
