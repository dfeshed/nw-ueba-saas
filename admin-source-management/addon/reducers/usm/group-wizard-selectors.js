import reselect from 'reselect';
import { isBlank, isPresent } from '@ember/utils';
import { exceedsLength } from './util/selector-helpers';

const { createSelector } = reselect;

const groupWizardState = (state) => state.usm.groupWizard;

const isExistingName = (name) => {
  if (isPresent(name)) {
    // This will be added when backend api is in master. Separate PR
    // return isPresent(groupSummaries) && !!groupSummaries.findBy('name', name);
    return false;
  }
};

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
