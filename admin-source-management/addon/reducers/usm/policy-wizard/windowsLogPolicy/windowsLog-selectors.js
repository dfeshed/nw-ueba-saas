// import { _policyWizardState, policy } from '../policy-wizard-selectors';
import reselect from 'reselect';
import { isBlank } from '@ember/utils';
import {
  CHANNEL_CONFIG
} from './windowsLog-settings';

const { createSelector } = reselect;

// TODO imports from policy-wizard-selectors are undefined...
const _policyWizardState = (state) => state.usm.policyWizard;
const policy = (state) => _policyWizardState(state).policy;

// ====================================================================
// Log Server settings
// ====================================================================
const _listOfLogServers = (state) => _policyWizardState(state).listOfLogServers || [];

// Same list of log servers are being used for windowslog and file policy types.
// For a file policy, if the log server version is older than 11.4
// make it read only (user wont be able to pick from the dropdown)
export const primaryLogServersList = createSelector(
  // only format/return what is in state - the reducer is responsible for the defaults for each setting
  [_listOfLogServers, policy],
  (listOfLogServers, policy) => {
    const services = [];
    for (let i = 0; i < listOfLogServers.length; i++) {
      // Disable the dropdown option if version < 11.4
      let isOldVersion = false;
      if (policy.policyType === 'filePolicy' && listOfLogServers[i].version < 11.4) {
        isOldVersion = true;
      }
      const service = {
        id: listOfLogServers[i].id,
        host: listOfLogServers[i].host,
        name: listOfLogServers[i].displayName,
        disabled: isOldVersion
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

export const channels = createSelector(
  policy,
  (policy) => policy.channelFilters
);

export const channelConfig = () => CHANNEL_CONFIG;

/**
 * validates the channelFilters array.
 * ChannelFilters is an array of objects
 * [{ channel: 'Security', filterType: 'EXCLUDE', eventId: '4' },
 *  { channel: '', filterType: 'INCLUDE', eventId: 'ALL'}]
 * @public
 */
export const channelFiltersValidator = (state) => {
  const VALID_EVENT_PATTERN = /^[0-9-]+$/;
  let error = false;
  let message = '';
  let invalidEntry = '';
  const value = channels(state);

  if (value) {
    // channels is an array of objects, loop through each obj and validate
    value.every((obj) => {
      const { eventId, filterType, channel } = obj;
      const isEventIdString = typeof eventId === 'string';
      let hasInvalidEventId = false;
      // if the field is blank, show an error message
      if (isBlank(channel) || isBlank(filterType) || isBlank(eventId)) {
        error = true;
        invalidEntry = '';
        message = 'adminUsm.policyWizard.windowsLogPolicy.invalidChannelFilter';
        return false;
      }
      // If Event ID is a string, it should be 'ALL'
      // any other string is treated as invalid
      if (eventId && isEventIdString && (eventId.trim().toUpperCase() === 'ALL' && filterType.toUpperCase() !== 'EXCLUDE')) {
        return true;
      }

      // If multiple event ids are entered, make sure they dont have invalid characters
      const arrayOfEvents = eventId.split(',');
      hasInvalidEventId = arrayOfEvents.some((event) => {
        return !VALID_EVENT_PATTERN.test(event.trim());
      });

      if (hasInvalidEventId) {
        error = true;
        message = 'adminUsm.policyWizard.windowsLogPolicy.invalidEventId';
        invalidEntry = eventId;
        return false;
      }
      return true;
    });
  }
  return {
    isError: error,
    errorMessage: message,
    invalidTableItem: invalidEntry
  };
};

/**
 * Map to hold all Windows Log Policy validator functions for settings
 * @public
 */
export const windowsLogPolicyValidatorFnMap = {
  'primaryDestination': windowsLogDestinationValidator,
  'secondaryDestination': windowsLogDestinationValidator,
  'channelFilters': channelFiltersValidator
};
