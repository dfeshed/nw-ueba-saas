import reselect from 'reselect';
import { isBlank } from '@ember/utils';
import { lookup } from 'ember-dependency-lookup';
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
 * validates a primary address value
 * returns error if value blank
 * @public
 */
export const primaryAddressValidator = (state, selectedSettingId) => {
  const value = primaryAddress(state, selectedSettingId);
  let error = false;
  let enableMessage = false;
  let message = '';

  // primary address cannot be blank
  if (isBlank(value)) {
    error = true;
    enableMessage = true;
    message = `adminUsm.policy.${selectedSettingId}InvalidMsg`;
  }
  return {
    isError: error,
    showError: enableMessage,
    errorMessage: message
  };
};

/**
 * returns the priamryAddress value from state based on the selectedSettingId.
 * @public
 */
export const primaryAddress = (state, selectedSettingId) => {
  return state.usm.policyWizard.policy[selectedSettingId];
};


/**
 * It returns the port value (primaryHttpsPort or primaryUdpPort) from state based on the selectedSettingId.
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
  let error = false;
  let enableMessage = false;
  let message = '';

  const validPort = isBetween(value);
  if (!validPort) {
    error = true;
    enableMessage = true;
    message = 'adminUsm.policy.portInvalidMsg';
  }
  return {
    isError: error,
    showError: enableMessage,
    errorMessage: message
  };
};

/**
 * returns a beacon interval value based on the selectedSettingId
 * (value of primaryHttpsBeaconInterval or primaryUdpBeaconInterval)
 * @public
 */
export const beaconIntervalValue = (state, selectedSettingId) => {
  return state.usm.policyWizard.policy[selectedSettingId];
};

/**
 * returns a beacon interval value validator object with values set for
 * - isError, errorMessage
 * @public
 */
export const beaconIntervalValueValidator = (state, selectedSettingId) => {
  const intervalValue = beaconIntervalValue(state, selectedSettingId);
  const intervalUnit = state.usm.policyWizard.policy[`${selectedSettingId}Unit`];
  // const multiplier = (intervalUnit === 'HOURS') ? 3600 : (intervalUnit === 'MINUTES') ? 60 : 1;
  let multiplier = 1; // 'SECONDS'
  if (intervalUnit === 'MINUTES') {
    multiplier = 60;
  } else if (intervalUnit === 'HOURS') {
    multiplier = 3600;
  }
  const seconds = intervalValue * multiplier;

  let error = false;
  let enableMessage = false;
  let message = '';

  // HTTPS valid interval is 1 minute to 24 hours
  if (selectedSettingId === 'primaryHttpsBeaconInterval') {
    if (seconds < 60 || seconds > 86400) {
      error = true;
      enableMessage = true;
      message = `adminUsm.policy.${selectedSettingId}InvalidMsg`;
    }
  // UDP valid interval is 5 seconds to 10 minutes
  } else if (selectedSettingId === 'primaryUdpBeaconInterval') {
    if (seconds < 5 || seconds > 600) {
      error = true;
      enableMessage = true;
      message = `adminUsm.policy.${selectedSettingId}InvalidMsg`;
    }
  }
  return {
    isError: error,
    showError: enableMessage,
    errorMessage: message
  };
};

const BEACON_INTERVAL_UNITS = {
  primaryHttpsBeaconInterval: ['MINUTES', 'HOURS'],
  primaryUdpBeaconInterval: ['SECONDS', 'MINUTES']
};

/**
 * returns the available beacon interval unit options based on the selectedSettingId
 * @public
 */
export const beaconIntervalUnits = (selectedSettingId) => {
  const i18n = lookup('service:i18n');
  const intervalUnits = BEACON_INTERVAL_UNITS[selectedSettingId];
  const options = intervalUnits.map((unit) => {
    const label = i18n.t(`adminUsm.policy.${selectedSettingId}_${unit}`);
    return {
      unit,
      label
    };
  });
  return options;
};

/**
 * we need the beacon interval unit object based on the selectedSettingId & the unit string value
 * (value of primaryHttpsBeaconIntervalUnit or primaryUdpBeaconIntervalUnit)
 * @public
 */
export const selectedBeaconIntervalUnit = (state, selectedSettingId) => {
  const intervalUnits = beaconIntervalUnits(selectedSettingId);
  let selected = null;
  for (let s = 0; s < intervalUnits.length; s++) {
    const intervalUnit = intervalUnits[s];
    if (state.usm.policyWizard.policy[`${selectedSettingId}Unit`] === intervalUnit.unit) {
      selected = intervalUnit;
      break;
    }
  }
  return selected;
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

/**
 * returns a scan start date validator object with values set for
 * - isError, errorMessage
 * @public
 */
export const startDateValidator = (state) => {
  const value = startDate(state);

  let error = false;
  let enableMessage = false;
  let message = '';

  // start date cannot be blank
  if (isBlank(value)) {
    error = true;
    enableMessage = true;
    message = 'adminUsm.policy.scanStartDateInvalidMsg';
  }
  return {
    isError: error,
    showError: enableMessage,
    errorMessage: message
  };
};

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

const _state = (state) => state;

/**
 * Map to hold validator functions for settings
 * if a setting is selected on the right side,
 * its validator is invoked
 * @private
 */
const validatorFnMap = {
  'scanStartDate': startDateValidator,
  'primaryAddress': primaryAddressValidator,
  'primaryHttpsPort': isPortValid,
  'primaryUdpPort': isPortValid,
  'primaryHttpsBeaconInterval': beaconIntervalValueValidator,
  'primaryUdpBeaconInterval': beaconIntervalValueValidator
};

export const isDefinePolicyStepValid = createSelector(
  _state, selectedSettings,
  (_state, selectedSettings) => {
    // at least one setting required to save a policy
    let isValid = selectedSettings.length > 0;
    for (let i = 0; i < selectedSettings.length; i++) {
      const el = selectedSettings[i];
      const selectedSettingId = el.id;
      const validator = validatorFnMap[selectedSettingId];
      if (!el.isHeader && validator) {
        // call validator function
        isValid = isValid && validator(_state, selectedSettingId).isError === false;
      }
    }
    return isValid;
  }
);

export const isWizardValid = createSelector(
  isIdentifyPolicyStepValid, isDefinePolicyStepValid,
  (isIdentifyPolicyStepValid, isDefinePolicyStepValid) => {
    return isIdentifyPolicyStepValid && isDefinePolicyStepValid;
  }
);