import reselect from 'reselect';
import _ from 'lodash';
import { isBlank, isEmpty } from '@ember/utils';
import { exceedsLength, isNameInList, groupExpressionValidator } from './util/selector-helpers';
import { getValidatorForExpression } from 'admin-source-management/reducers/usm/group-wizard-reducers';

const { createSelector } = reselect;

const _groupWizardState = (state) => state.usm.groupWizard;
const _policiesState = (state) => state.usm.policies;
export const selectedSourceType = (state) => _groupWizardState(state).selectedSourceType;
export const groupRanking = (state) => _groupWizardState(state).groupRanking;
const _groupRankingOrig = (state) => _groupWizardState(state).groupRankingOrig;
export const groupRankingStatus = (state) => _groupWizardState(state).groupRankingStatus;
export const group = (state) => _groupWizardState(state).group;
const _groupOrig = (state) => _groupWizardState(state).groupOrig;
export const assignedPolicies = (state) => _groupWizardState(state).group.assignedPolicies;
export const groupList = (state) => _groupWizardState(state).groupList;
export const policyList = (state) => _groupWizardState(state).policyList;
export const visited = (state) => _groupWizardState(state).visited;
export const steps = (state) => _groupWizardState(state).steps;
export const rankingSteps = (state) => _groupWizardState(state).rankingSteps;
export const groupCriteria = (state) => _groupWizardState(state).group.groupCriteria.criteria;
export const groupCriteriaCache = (state) => _groupWizardState(state).criteriaCache;
export const groupAttributesMap = (state) => _groupWizardState(state).groupAttributesMap;
export const andOrOperator = (state) => _groupWizardState(state).group.groupCriteria.conjunction;
export const selectedGroupRanking = (state) => _groupWizardState(state).selectedGroupRanking;
export const groupRankingPrevListStatus = (state) => _policiesState(state).groupRankingPrevListStatus;
export const focusedItem = (state) => _policiesState(state).focusedItem;

export const hasGroupRankingChanged = createSelector(
  _groupRankingOrig, groupRanking,
  (_groupRankingOrig, groupRanking) => {
    const groupRankingCleaned = groupRanking.map((rank) => _.omit(rank, 'isChecked'));
    return !_.isEqual(_groupRankingOrig, groupRankingCleaned);
  }
);

export const groupRankingQuery = createSelector(
  selectedSourceType, groupRanking,
  (selectedSourceType, groupRanking) => {
    const groupRankingIDs = groupRanking.map((rank) => rank.id);
    return { policyType: selectedSourceType, groupIds: groupRankingIDs };
  }
);

export const groupRankingViewQuery = createSelector(
  selectedSourceType, groupRanking,
  (selectedSourceType, groupRanking) => {
    const groupRankingIDs = groupRanking.filter((group) => group.isChecked ? group : '').map((rank) => rank.id);
    return { policyType: selectedSourceType, groupIds: groupRankingIDs };
  }
);

export const hasGroupChanged = createSelector(
  _groupOrig, group,
  (_groupOrig, group) => {
    return !_.isEqual(_groupOrig, group);
  }
);

export const isGroupLoading = createSelector(
  _groupWizardState,
  (groupWizardState) => {
    return groupWizardState.groupFetchStatus === 'wait' ||
      groupWizardState.groupStatus === 'wait' ||
      groupWizardState.initGroupFetchPoliciesStatus === 'wait';
  }
);

export const isGroupFetchError = createSelector(
  _groupWizardState,
  (groupWizardState) => {
    return groupWizardState.groupFetchStatus === 'error';
  }
);

export const isLoadingGroupRanking = createSelector(
  _groupWizardState,
  (groupWizardState) => groupWizardState.groupRankingStatus === 'wait'
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

// loop the assignedPolicies to preserve order of creation
export const assignedPolicyList = createSelector(
  assignedPolicies, policyList,
  (assignedPolicies, policyList) => {
    const list = [];
    for (const key in assignedPolicies) {
      if (assignedPolicies.hasOwnProperty(key)) {
        // set placeholder values
        if (assignedPolicies[key].referenceId === 'placeholder') {
          const placeholder = {
            id: assignedPolicies[key].referenceId,
            name: assignedPolicies[key].name,
            policyType: key
          };
          list.push(placeholder);
        } else {
          // loop the policyList to retrieve etire policy
          for (let p = 0; p < policyList.length; p++) {
            const policy = policyList[p];
            if (policy.id === assignedPolicies[key].referenceId) {
              list.push(policy);
            }
          }
        }
      }
    }
    return list;
  }
);

export const limitedPolicySourceTypes = createSelector(
  availablePolicySourceTypes, assignedPolicyList,
  (sourceTypes, assignedPolicyList) => {
    const list = [];
    for (let index = 0; index < sourceTypes.length; index++) {
      const found = assignedPolicyList.some(function(element) {
        return element.policyType === sourceTypes[index];
      });
      if (!found) {
        list.push(sourceTypes[index]);
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

export const isGroupCriteriaEmpty = createSelector(
  groupCriteriaCache,
  (groupCriteriaCache) => {
    return isEmpty(groupCriteriaCache);
  }
);

export const groupCriteriaValidator = createSelector(
  groupCriteriaCache,
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
