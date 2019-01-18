import reselect from 'reselect';
import { isBlank } from '@ember/utils';
import { lookup } from 'ember-dependency-lookup';
import moment from 'moment';
// import { _policyWizardState, policy } from '../policy-wizard-selectors';
import {
  RADIO_BUTTONS_CONFIG,
  ALL_RADIO_OPTIONS,
  isBetween
} from './edr-settings';
import { VALID_HOSTNAME_REGEX } from '../../util/selector-helpers';

const { createSelector } = reselect;

// TODO imports from policy-wizard-selectors are undefined...
const _policyWizardState = (state) => state.usm.policyWizard;
const policy = (state) => _policyWizardState(state).policy;

// ====================================================================
// Scan Schedule settings
// ====================================================================
export const scanType = createSelector(
  policy,
  (policy) => policy.scanType
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
    message = 'adminUsm.policyWizard.edrPolicy.scanStartDateInvalidMsg';
  }
  return {
    isError: error,
    showError: enableMessage,
    errorMessage: message
  };
};

const weeks = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'];
// Key is type
const COUNTER = {
  'DAYS': [1, 2, 3, 4, 5, 6, 10, 15, 20],
  'WEEKS': [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24]
};

// intervalType/recurrenceUnit a.k.a scan frequency config ('DAYS' | 'WEEKS')
export const radioButtonConfig = () => RADIO_BUTTONS_CONFIG;

export const intervalType = createSelector(
  policy,
  (policy) => policy.recurrenceUnit
);

export const interval = createSelector(
  policy,
  (policy) => policy.recurrenceInterval
);

export const isWeeklyInterval = createSelector(
  intervalType,
  (intervalUnit) => intervalUnit === 'WEEKS'
);

export const runOnDaysOfWeek = createSelector(
  policy,
  (policy) => policy.runOnDaysOfWeek
);

export const weekOptions = createSelector(
  intervalType, runOnDaysOfWeek,
  (intervalType, runOnDaysOfWeek) => {
    if (intervalType === 'WEEKS') {
      const config = weeks.map((week) => {
        const label = `adminUsm.policyWizard.edrPolicy.recurrenceInterval.week.${week}`;
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
    const runLabel = `adminUsm.policyWizard.edrPolicy.recurrenceInterval.intervalText.${intervalType}`;
    const options = COUNTER[intervalType];
    return { runLabel, options };
  }
);

export const startTime = createSelector(
  policy,
  (policy) => policy.scanStartTime
);

export const cpuMax = createSelector(
  policy,
  (policy) => policy.cpuMax
);

export const cpuMaxVm = createSelector(
  policy,
  (policy) => policy.cpuMaxVm
);

// ====================================================================
// Advanced Scan settings
// * all edr-radios so all use radioButtonOption & radioButtonValue
// ====================================================================
/**
 * It returns the appropriate radio button option based on the selectedSettingId
 * @public
 */
export const radioButtonOption = (selectedSettingId) => {
  const getOptionsById = ALL_RADIO_OPTIONS.find((d) => d.id === selectedSettingId);
  const { options } = getOptionsById;
  return options;
};

/**
 * It returns the appropriate radio button value based on the selectedSettingId
 * @public
 */
export const radioButtonValue = (state, selectedSettingId) => _policyWizardState(state).policy[selectedSettingId];

// ====================================================================
// Invasive Actions settings
// * all edr-radios so all use radioButtonOption & radioButtonValue
// ====================================================================


// ====================================================================
// Endpoint Server settings
// ====================================================================
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

/**
 * returns the priamryAddress value from state based on the selectedSettingId.
 * @public
 */
export const primaryAddress = (state, selectedSettingId) => {
  return _policyWizardState(state).policy[selectedSettingId];
};

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
    message = `adminUsm.policyWizard.edrPolicy.${selectedSettingId}InvalidMsg`;
  }
  return {
    isError: error,
    showError: enableMessage,
    errorMessage: message
  };
};

export const primaryAlias = createSelector(
  policy,
  (policy) => policy.primaryAlias
);

/**
 * User can optionally enter an alternative IP or hostname when selecting an endpoint server.
 * Validates a primaryAlias value
 * returns error if value is invalid
 * @public
 */
export const isPrimaryAliasValid = (state) => {
  const value = primaryAlias(state);
  let error = false;
  let enableMessage = false;
  let message = '';

  // primary alias cannot be an invalid hostname, it can be blank since it is optional
  if (!isBlank(value) && !(VALID_HOSTNAME_REGEX.test(value))) {
    error = true;
    enableMessage = true;
    message = 'adminUsm.policyWizard.edrPolicy.primaryAliasInvalid';
  }
  return {
    isError: error,
    showError: enableMessage,
    errorMessage: message
  };
};

/**
 * It returns the port value (primaryHttpsPort or primaryUdpPort) from state based on the selectedSettingId.
 * @public
 */
export const portValue = (state, selectedSettingId) => {
  return _policyWizardState(state).policy[selectedSettingId];
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
    message = 'adminUsm.policyWizard.edrPolicy.portInvalidMsg';
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
  return _policyWizardState(state).policy[selectedSettingId];
};

/**
 * returns a beacon interval value validator object with values set for
 * - isError, errorMessage
 * @public
 */
export const beaconIntervalValueValidator = (state, selectedSettingId) => {
  const intervalValue = beaconIntervalValue(state, selectedSettingId);
  const intervalUnit = _policyWizardState(state).policy[`${selectedSettingId}Unit`];
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
      message = `adminUsm.policyWizard.edrPolicy.${selectedSettingId}InvalidMsg`;
    }
  // UDP valid interval is 5 seconds to 10 minutes
  } else if (selectedSettingId === 'primaryUdpBeaconInterval') {
    if (seconds < 5 || seconds > 600) {
      error = true;
      enableMessage = true;
      message = `adminUsm.policyWizard.edrPolicy.${selectedSettingId}InvalidMsg`;
    }
  }
  return {
    isError: error,
    showError: enableMessage,
    errorMessage: message
  };
};

const BEACON_INTERVAL_UNITS = {
  'primaryHttpsBeaconInterval': ['MINUTES', 'HOURS'],
  'primaryUdpBeaconInterval': ['SECONDS', 'MINUTES']
};

/**
 * returns the available beacon interval unit options based on the selectedSettingId
 * @public
 */
export const beaconIntervalUnits = (selectedSettingId) => {
  const i18n = lookup('service:i18n');
  const intervalUnits = BEACON_INTERVAL_UNITS[selectedSettingId];
  const options = intervalUnits.map((unit) => {
    const label = i18n.t(`adminUsm.policyWizard.edrPolicy.${selectedSettingId}_${unit}`);
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
    if (_policyWizardState(state).policy[`${selectedSettingId}Unit`] === intervalUnit.unit) {
      selected = intervalUnit;
      break;
    }
  }
  return selected;
};

export const customConfig = (state, selectedSettingId) => {
  return _policyWizardState(state).policy[selectedSettingId];
};

/**
 * validates the custom config setting value
 * returns error if value blank
 * @public
 */
export const customConfigValidator = (state, selectedSettingId) => {
  const value = customConfig(state, selectedSettingId);
  let error = false;
  let enableMessage = false;
  let message = '';

  // custom config cannot be blank
  if (isBlank(value) || value.length > 4000) {
    error = true;
    enableMessage = true;
    message = `adminUsm.policyWizard.edrPolicy.${selectedSettingId}InvalidMsg`;
  }
  return {
    isError: error,
    showError: enableMessage,
    errorMessage: message
  };
};

// ====================================================================
// Agent settings
// * all edr-radios so all use radioButtonOption & radioButtonValue
// ====================================================================

/**
 * Map to hold all EDR Policy validator functions for settings
 * @public
 */
export const edrPolicyValidatorFnMap = {
  'scanStartDate': startDateValidator,
  'primaryAddress': primaryAddressValidator,
  'primaryAlias': isPrimaryAliasValid,
  'primaryHttpsPort': isPortValid,
  'primaryUdpPort': isPortValid,
  'primaryHttpsBeaconInterval': beaconIntervalValueValidator,
  'primaryUdpBeaconInterval': beaconIntervalValueValidator,
  'customConfig': customConfigValidator
};
