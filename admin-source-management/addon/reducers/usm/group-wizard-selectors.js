import reselect from 'reselect';
import { isBlank } from '@ember/utils';
import { exceedsLength, isNameInList } from './util/selector-helpers';

const { createSelector } = reselect;

const _groupWizardState = (state) => state.usm.groupWizard;
export const group = (state) => _groupWizardState(state).group;
export const groupList = (state) => _groupWizardState(state).groupList;
export const policyList = (state) => _groupWizardState(state).policyList;
export const visited = (state) => _groupWizardState(state).visited;
export const steps = (state) => _groupWizardState(state).steps;
export const rankingSteps = (state) => _groupWizardState(state).rankingSteps;
export const groupCriteria = (state) => _groupWizardState(state).group.groupCriteria.criteria;
export const groupAttributesMap = (state) => _groupWizardState(state).groupAttributesMap;
export const andOrOperator = (state) => _groupWizardState(state).group.groupCriteria.conjunction;

export const isGroupLoading = createSelector(
  _groupWizardState,
  (groupWizardState) => {
    return groupWizardState.groupStatus === 'wait' ||
      groupWizardState.initGroupFetchPoliciesStatus === 'wait';
  }
);

/**
 * returns a name validator object with values set for
 * - isError, errorMessage, isVisited
 * @public
 */
export const nameValidator = createSelector(
  groupList, group, visited,
  (groupList, group, visited) => {
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
    } else if (isNameInList(groupList, group.id, group.name)) {
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

export const selectedPolicy = createSelector(
  group, policyList,
  (group, policyList) => {
    let selected = null;
    if (group.assignedPolicies) {
      for (let p = 0; p < policyList.length; p++) {
        const policy = policyList[p];
        if (group.assignedPolicies.hasOwnProperty(policy.policyType) &&
            group.assignedPolicies[policy.policyType]) {
          const groupPolicyId = group.assignedPolicies[policy.policyType].referenceId;
          if (policy.id === groupPolicyId) {
            selected = policy;
            break;
          }
        }
      }
    }
    return selected;
  }
);

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
