import reselect from 'reselect';
import { isBlank } from '@ember/utils';

const { createSelector } = reselect;

const groupWizardState = (state) => state.usm.groupWizard;

/**
 * the group object to be created/updated/saved
 * @public
 */
export const group = createSelector(
  groupWizardState,
  (groupWizardState) => groupWizardState.group
);

export const isGroupLoading = createSelector(
  groupWizardState,
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
 * form fields visited by the user
 * @public
 */
export const visited = createSelector(
  groupWizardState,
  (groupWizardState) => groupWizardState.visited
);

/**
 * returns a name validator object with values set for
 * - isError, errorMessage, isVisited
 * @public
 */
export const nameValidator = createSelector(
  group, visited,
  (group, visited) => {
    return {
      isError: isBlank(group.name),
      errorMessage: 'adminUsm.groupWizard.nameRequired',
      isVisited: visited.indexOf('group.name') > -1
    };
  }
);

export const steps = createSelector(
  groupWizardState,
  (groupWizardState) => groupWizardState.steps
);

export const isIdentifyGroupStepValid = createSelector(
  nameValidator,
  (nameValidator) => nameValidator.isError === false
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
