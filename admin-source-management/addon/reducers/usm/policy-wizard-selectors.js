import reselect from 'reselect';
import { isBlank, isPresent } from '@ember/utils';
import _ from 'lodash';
import moment from 'moment';
import { exceedsLength } from './util/selector-helpers';
import { RADIO_BUTTONS_CONFIG, SCAN_SCHEDULE_CONFIG } from 'admin-source-management/utils/settings';

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

const availableSettings = (state) => state.usm.policyWizard.availableSettings || {};
const selectedSettings = (state) => state.usm.policyWizard.selectedSettings || {};

export const enabledAvailableSettings = createSelector(
  availableSettings,
  (availableSettings) => {
    return availableSettings.filter((el) => el.isEnabled);
  }
);

export const sortedSelectedSettings = createSelector(
  selectedSettings,
  (selectedSettings) => {
    return _.sortBy(selectedSettings, 'index');
  }
);

const weeks = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'];
// Key is type
const COUNTER = {
  'DAYS': [1, 2, 3, 4, 5, 6, 10, 15, 20],
  'WEEKS': [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24]
};

export const scheduleConfig = (state) => state.usm.policyWizard.policy.scheduleConfig || {};
export const radioButtonConfig = () => RADIO_BUTTONS_CONFIG;
export const scanScheduleConfig = () => SCAN_SCHEDULE_CONFIG;

export const scheduleOptions = createSelector(
  scheduleConfig,
  (scheduleConfig) => _.cloneDeep(scheduleConfig.scheduleOptions) || {}
);

export const scanOptions = createSelector(
  scheduleConfig,
  (scheduleConfig) => scheduleConfig.scanOptions || { cpuMaximum: '80', cpuMaximumOnVirtualMachine: '90' }
);

/**
 * The state stores the scanStartDate as a YYYY-MM-DD formatted string,
 * but the rsa-form-datetime component needs a Date Object, Timestamp, or an ISO 8601 Date String,
 * so we'll convert it to an ISO 8601 Date String here
 * @public
 */
export const startDate = createSelector(
  scheduleOptions,
  (scheduleOptions) => {
    const scanStartDate = scheduleOptions.scanStartDate ? moment(scheduleOptions.scanStartDate, 'YYYY-MM-DD') : moment().startOf('date');
    return scanStartDate.toISOString(true);
  }
);

export const startTime = createSelector(
  scheduleOptions,
  (scheduleOptions) => {
    return scheduleOptions.scanStartTime ? scheduleOptions.scanStartTime : '10:00';
  }
);

const intervalType = createSelector(
  scheduleOptions,
  (schedule) => schedule.recurrenceIntervalUnit || 'DAYS'
);

export const isWeeklyInterval = createSelector(
  intervalType,
  (intervalUnit) => intervalUnit === 'WEEKS'
);

const runOnDaysOfWeek = createSelector(
  scheduleOptions,
  (schedule) => schedule.runOnDaysOfWeek || []
);

export const weekOptions = createSelector(
  intervalType, runOnDaysOfWeek,
  (intervalType, runOnDaysOfWeek) => {
    if (intervalType === 'WEEKS') {
      const config = weeks.map((week) => {
        const label = `adminUsm.policy.scheduleConfiguration.recurrenceInterval.week.${week}`;
        return {
          label,
          week,
          isActive: runOnDaysOfWeek.includes(week)
        };
      });
      return config;
    }
    return null;
  }
);

export const runIntervalConfig = createSelector(
  intervalType,
  (intervalType) => {
    const runLabel = `adminUsm.policy.scheduleConfiguration.recurrenceInterval.intervalText.${intervalType}`;
    const options = COUNTER[intervalType];
    return { runLabel, options };
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
  nameValidator, descriptionValidator,
  (nameValidator, descriptionValidator) => {
    return nameValidator.isError === false && descriptionValidator.isError === false;
  }
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
