// import { _policyWizardState, policy } from '../policy-wizard-selectors';
import reselect from 'reselect';
import { isBlank } from '@ember/utils';
import { ALL_RADIO_OPTIONS } from './windowsLog-settings';

const { createSelector } = reselect;

// TODO imports from policy-wizard-selectors are undefined...
const _policyWizardState = (state) => state.usm.policyWizard;
const policy = (state) => _policyWizardState(state).policy;

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
// Log Server settings
// ====================================================================
const _listOfLogServers = (state) => _policyWizardState(state).listOfLogServers || [];

export const logServersList = createSelector(
  // only format/return what is in state - the reducer is responsible for the defaults for each setting
  [_listOfLogServers],
  (listOfLogServers) => {
    const services = [];
    for (let i = 0; i < listOfLogServers.length; i++) {
      const service = {
        id: listOfLogServers[i].id,
        host: listOfLogServers[i].host,
        name: listOfLogServers[i].displayName
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
export const selectedLogServer = createSelector(
  policy, logServersList,
  (policy, logServersList) => {
    let selected = null;

    for (let s = 0; s < logServersList.length; s++) {
      const logServer = logServersList[s];
      if (policy.primaryDestination === logServer.host) {
        selected = logServer;
        break;
      }
    }
    return selected;
  }
);

/**
 * returns the primaryDestination value from state based on the selectedSettingId.
 * @public
 */
export const primaryDestination = (state, selectedSettingId) => {
  return _policyWizardState(state).policy[selectedSettingId];
};

/**
 * validates a primary destination value
 * returns error if value is blank
 * @public
 */
export const primaryDestinationValidator = (state, selectedSettingId) => {
  const value = primaryDestination(state, selectedSettingId);
  let error = false;
  let enableMessage = false;
  let message = '';

  // primary address cannot be blank
  if (isBlank(value)) {
    error = true;
    enableMessage = true;
    message = `adminUsm.policyWizard.windowsLogPolicy.${selectedSettingId}InvalidMsg`;
  }
  return {
    isError: error,
    showError: enableMessage,
    errorMessage: message
  };
};

/**
 * Map to hold all Windows Log Policy validator functions for settings
 * @public
 */
export const windowsLogPolicyValidatorFnMap = {
  'primaryDestination': primaryDestinationValidator
};
