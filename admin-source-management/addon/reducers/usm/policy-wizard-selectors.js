import reselect from 'reselect';
import { isBlank } from '@ember/utils';
import _ from 'lodash';
import moment from 'moment';
import {
  RADIO_BUTTONS_CONFIG,
  ALL_RADIO_OPTIONS,
  isBetween
} from 'admin-source-management/utils/settings';
import { exceedsLength, isNameInList } from './util/selector-helpers';

const { createSelector } = reselect;

const _policyWizardState = (state) => state.usm.policyWizard;
export const policy = (state) => _policyWizardState(state).policy;
export const policyList = (state) => _policyWizardState(state).policyList;
export const visited = (state) => _policyWizardState(state).visited;
export const steps = (state) => _policyWizardState(state).steps;
export const sourceTypes = (state) => _policyWizardState(state).sourceTypes;
export const listOfEndpoints = (state) => _policyWizardState(state).listOfEndpointServers || [];

export const endpointServersList = createSelector(
  // only format/return what is in state - the reducer is responsible for the defaults for each setting
  [listOfEndpoints],
  (listOfEndpoints) => {
    const services = [];
    for (let i = 0; i < listOfEndpoints.length; i++) {
      const service = {
        id: listOfEndpoints[i].id,
        host: listOfEndpoints[i].host,
        name: listOfEndpoints[i].displayName
      };
      services.push(service);
    }
    return services;
  }
);

/**
 * we need the selected policy primaryAddress
 * @public
 */
export const selectedEndpointSever = createSelector(
  policy, endpointServersList,
  (policy, endpointServersList) => {
    let selected = null;
    for (let s = 0; s < endpointServersList.length; s++) {
      const endpointServer = endpointServersList[s];
      if (policy.primaryAddress === endpointServer.host) {
        selected = endpointServer;
        break;
      }
    }
    return selected;
  }
);

export const isPolicyLoading = createSelector(
  _policyWizardState,
  (policyWizardState) => policyWizardState.policyStatus === 'wait'
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

/**
 * It returns the appropriate radio button value based on the selectedSettingId
 * @public
 */
export const radioButtonValue = (state, selectedSettingId) => state.usm.policyWizard.policy[selectedSettingId];

/**
 * It returns the port value (httpPort or udpPort) from state based on the selectedSettingId.
 * @public
 */
export const portValue = (state, selectedSettingId) => {
  return state.usm.policyWizard.policy[selectedSettingId];
};

/**
 * It returns true if port is valid and within bounds, false otherwise
 * @public
 */
export const isPortValid = (state, selectedSettingId) => {
  const value = portValue(state, selectedSettingId);
  return isBetween(value);
};

/**
 * It returns the appropriate radio button option based on the selectedSettingId
 * @public
 */
export const radioButtonOption = (selectedSettingId) => {
  const getOptionsById = ALL_RADIO_OPTIONS.find((d) => d.id === selectedSettingId);
  const { options } = getOptionsById;
  return options;
};

const weeks = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'];
// Key is type
const COUNTER = {
  'DAYS': [1, 2, 3, 4, 5, 6, 10, 15, 20],
  'WEEKS': [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24]
};

export const radioButtonConfig = () => RADIO_BUTTONS_CONFIG;

export const scanType = createSelector(
  policy,
  (policy) => policy.scanType // only format/return what is in state - the reducer is responsible for the defaults for each setting
);

/**
 * The state stores the scanStartDate as a YYYY-MM-DD formatted string,
 * but the rsa-form-datetime component needs a Date Object, Timestamp, or an ISO 8601 Date String,
 * so we'll convert it to an ISO 8601 Date String here.
 * @public
 */
export const startDate = createSelector(
  policy,
  (policy) => {
    // only format/return what is in state - the reducer is responsible for the defaults for each setting
    const scanStartDateISO = policy.scanStartDate ? moment(policy.scanStartDate, 'YYYY-MM-DD').toISOString(true) : policy.scanStartDate; // moment().startOf('date');
    return scanStartDateISO;
  }
);

export const startTime = createSelector(
  policy,
  (policy) => policy.scanStartTime // only format/return what is in state - the reducer is responsible for the defaults for each setting
);

export const interval = createSelector(
  policy,
  (policy) => policy.recurrenceInterval // only format/return what is in state - the reducer is responsible for the defaults for each setting
);

export const intervalType = createSelector(
  policy,
  (policy) => policy.recurrenceUnit // only format/return what is in state - the reducer is responsible for the defaults for each setting
);

export const isWeeklyInterval = createSelector(
  intervalType,
  (intervalUnit) => intervalUnit === 'WEEKS'
);

export const runOnDaysOfWeek = createSelector(
  policy,
  (policy) => policy.runOnDaysOfWeek // only format/return what is in state - the reducer is responsible for the defaults for each setting
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
          isActive: runOnDaysOfWeek && runOnDaysOfWeek.includes(week)
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

export const cpuMax = createSelector(
  policy,
  (policy) => policy.cpuMax // only format/return what is in state - the reducer is responsible for the defaults for each setting
);

export const cpuMaxVm = createSelector(
  policy,
  (policy) => policy.cpuMaxVm // only format/return what is in state - the reducer is responsible for the defaults for each setting
);

/**
 * returns a name validator object with values set for
 * - isError, errorMessage
 * @public
 */
export const nameValidator = createSelector(
  policyList, policy, visited,
  (policyList, policy, visited) => {
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
    } else if (isNameInList(policyList, policy.id, policy.name)) {
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
