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

export const primaryLogServersList = createSelector(
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
 * Returns the selected primary log server
 * @public
 * @return  {obj} {id: "id1", host: "10.10.10.10"}
 */
export const selectedPrimaryLogServer = createSelector(
  policy, primaryLogServersList,
  (policy, primaryLogServersList) => {
    let selected = null;

    for (let s = 0; s < primaryLogServersList.length; s++) {
      const logServer = primaryLogServersList[s];
      if (policy.primaryDestination === logServer.host) {
        selected = logServer;
        break;
      }
    }
    return selected;
  }
);

/**
 * Excludes the primary selected server from the list and returns the rest
 * We don't want to display a server that is chosen as a primary server in the secondary server dropdown.
 * @public
 */
export const secondaryLogServersList = createSelector(
  [primaryLogServersList, selectedPrimaryLogServer],
  (primaryLogServersList, selectedPrimaryLogServer) => {
    // If primaryLogServer is already chosen, exclude that from the secondaryLogServersList
    if (selectedPrimaryLogServer) {
      return primaryLogServersList.filter((obj) => obj.id !== selectedPrimaryLogServer.id);
    } else {
      return primaryLogServersList;
    }
  }
);

/**
 * Returns the selected secondary log server
 * @public
* @return  {obj} {id: "id2", host: "10.10.10.12"}
 */
export const selectedSecondaryLogServer = createSelector(
  policy, secondaryLogServersList,
  (policy, secondaryLogServersList) => {
    let selected = null;
    for (let s = 0; s < secondaryLogServersList.length; s++) {
      const logServer = secondaryLogServersList[s];
      if (policy.secondaryDestination === logServer.host) {
        selected = logServer;
        break;
      }
    }
    return selected;
  }
);

/**
 * validates both the primary and secondary log servers
 * returns error if value is blank
 * @public
 */
export const windowsLogDestinationValidator = (state, selectedSettingId) => {
  let error = false;
  let enableMessage = false;
  let message = '';
  let value;

  if (selectedSettingId === 'primaryDestination') {
    value = selectedPrimaryLogServer(state) ? selectedPrimaryLogServer(state).host : '';
  } else { // secondaryDestination
    value = selectedSecondaryLogServer(state) ? selectedSecondaryLogServer(state).host : '';
  }

  if (isBlank(value)) {
    error = true;
    enableMessage = true;
    message = 'adminUsm.policyWizard.windowsLogPolicy.windowsLogDestinationInvalidMsg';
  }
  return {
    isError: error,
    showError: enableMessage,
    errorMessage: message
  };
};

export const protocolsList = ['UDP', 'TCP', 'TLS'];

export const selectedProtocol = createSelector(
  policy,
  (policy) => policy.protocol
);

/**
 * Map to hold all Windows Log Policy validator functions for settings
 * @public
 */
export const windowsLogPolicyValidatorFnMap = {
  'primaryDestination': windowsLogDestinationValidator,
  'secondaryDestination': windowsLogDestinationValidator
};
