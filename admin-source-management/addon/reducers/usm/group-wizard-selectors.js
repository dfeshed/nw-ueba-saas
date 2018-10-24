import reselect from 'reselect';
import { isBlank, isEmpty } from '@ember/utils';
import { exceedsLength, isNameInList, groupExpressionValidator } from './util/selector-helpers';
import { getValidatorForExpression } from 'admin-source-management/reducers/usm/group-wizard-reducers';

const { createSelector } = reselect;

const _groupWizardState = (state) => state.usm.groupWizard;
export const selectedSourceType = (state) => _groupWizardState(state).selectedSourceType;
export const groupRanking = (state) => _groupWizardState(state).groupRanking;
export const groupRankingStatus = (state) => _groupWizardState(state).groupRankingStatus;
export const group = (state) => _groupWizardState(state).group;
export const assignedPolicies = (state) => _groupWizardState(state).group.assignedPolicies;
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

export const availablePolicySourceTypes = createSelector(
  policyList,
  (policyList) => {
    const list = [];
    for (let index = 0; index < policyList.length; index++) {
      const sourceType = policyList[index].policyType;
      if (!list.includes(sourceType)) {
        list.push(sourceType);
      }
    }
    return list;
  }
);

export const assignedPolicyList = createSelector(
  assignedPolicies, policyList,
  (assignedPolicies, policyList) => {
    const list = [];
    if (assignedPolicies) {
      let allowPlaceHolder = true;
      for (let p = 0; p < policyList.length; p++) {
        const policy = policyList[p];
        if (assignedPolicies.hasOwnProperty(policy.policyType) && assignedPolicies[policy.policyType]) {
          const groupPolicyId = assignedPolicies[policy.policyType].referenceId;
          if (allowPlaceHolder && groupPolicyId === 'placeholder') {
            const groupPolicyName = assignedPolicies[policy.policyType].name;
            const placeholder = {
              id: groupPolicyId,
              name: groupPolicyName,
              policyType: policy.policyType
            };
            list.push(placeholder);
            allowPlaceHolder = false;
          } else if (policy.id === groupPolicyId) {
            list.push(policy);
          }
        }
      }
    }
    return list;
  }
);

// Validation related selectors
// ----------------------------------------

export const identifyGroupStepShowErrors = createSelector(
  steps,
  (steps) => {
    return steps[0].showErrors;
  }
);

export const defineGroupStepShowErrors = createSelector(
  steps,
  (steps) => {
    return steps[1].showErrors;
  }
);

export const applyPolicyStepShowErrors = createSelector(
  steps,
  (steps) => {
    return steps[2].showErrors;
  }
);

/**
 * returns a name validator object with values set for
 * - isError, errorMessage, isVisited
 * @public
 */
export const nameValidator = createSelector(
  groupList, group, visited, identifyGroupStepShowErrors,
  (groupList, group, visited, stepShowErrors) => {
    let error = false;
    let enableMessage = false;
    let message = '';
    if (isBlank(group.name)) {
      error = true;
      // only blank value requires visited
      if (stepShowErrors || visited.includes('group.name')) {
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

export const groupCriteriaValidator = createSelector(
  groupCriteria,
  (groupCriteria) => {
    let error = false;
    if (isEmpty(groupCriteria)) {
      error = true;
    } else {
      for (let index = 0; index < groupCriteria.length; index++) {
        const expression = groupCriteria[index];
        const validator = getValidatorForExpression(expression);
        if (validator) {
          const [ /* attribute */, /* operator */, values ] = expression;
          error = groupExpressionValidator(values, validator, true, true).isError;
          if (error) {
            break;
          }
        }
      }
    }
    return {
      isError: error
    };
  }
);

export const policyAssignmentValidator = createSelector(
  assignedPolicies, policyList,
  (assignedPolicies, policyList) => {
    let error = false;
    for (const sourceType in assignedPolicies) {
      if (assignedPolicies.hasOwnProperty(sourceType) && assignedPolicies[sourceType]) {
        const groupPolicyId = assignedPolicies[sourceType].referenceId;
        let found = false;
        for (let index = 0; index < policyList.length; index++) {
          const policy = policyList[index];
          if ((policy.policyType === sourceType) && (policy.id === groupPolicyId)) {
            found = true;
            break;
          }
        }
        if (!found) {
          error = true;
          break;
        }
      }
    }
    return {
      isError: error
    };
  }
);

export const isIdentifyGroupStepValid = createSelector(
  nameValidator, descriptionValidator,
  (nameValidator, descriptionValidator) => {
    return nameValidator.isError === false && descriptionValidator.isError === false;
  }
);

export const isDefineGroupStepValid = createSelector(
  groupCriteriaValidator,
  (groupCriteriaValidator) => {
    return groupCriteriaValidator.isError === false;
  }
);

export const isApplyPolicyStepValid = createSelector(
  policyAssignmentValidator,
  (policyAssignmentValidator) => {
    return policyAssignmentValidator.isError === false;
  }
);


// TODO implement real check
export const isReviewGroupStepValid = createSelector(
  group,
  (group) => group.name === group.name
);

export const isWizardValid = createSelector(
  isIdentifyGroupStepValid, isDefineGroupStepValid, isApplyPolicyStepValid, isReviewGroupStepValid,
  (isIdentifyGroupStepValid, isDefineGroupStepValid, isApplyPolicyStepValid, isReviewGroupStepValid) => {
    return isIdentifyGroupStepValid && isDefineGroupStepValid &&
    isApplyPolicyStepValid && isReviewGroupStepValid;
  }
);
