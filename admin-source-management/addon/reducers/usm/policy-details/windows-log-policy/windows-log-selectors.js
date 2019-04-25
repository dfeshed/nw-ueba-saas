import reselect from 'reselect';
import { isBlank } from '@ember/utils';
import { lookup } from 'ember-dependency-lookup';

const { createSelector } = reselect;


const _focusedPolicyOrigin = (state) => {
  if (state.usm.policies.focusedItem && state.usm.policies.focusedItem.origins) {
    return state.usm.policies.focusedItem.origins;
  }
};

export const focusedPolicy = (state) => {
  if (state.usm.policies.focusedItem && state.usm.policies.focusedItem.origins) {
    return state.usm.policies.focusedItem.policy;
  } else {
    return state.usm.policies.focusedItem;
  }
};

const _listOfLogServers = (state) => state.usm.policyWizard.listOfLogServers || [];

/**
 * formats the policy in focus to return an array of sections
 * to the windows log policy details template
 * Each section has a header and a list of properties and their values
 * [settingSection[0], settingSection[1] ...]
 * settingSection: {header: '', props: [{name: '', value:''}]}
 * the values are formatted based on other properties in the policy and
 * concatenating translations if needed
 * @public
*/
export const selectedWindowsLogPolicy = createSelector(
  focusedPolicy, _listOfLogServers, _focusedPolicyOrigin,
  (focusedPolicy, _listOfLogServers, _focusedPolicyOrigin) => {
    const policyDetails = [];
    const channelFilters = [];
    const basicSettings = [];
    const emptyOrigin = { groupName: '', policyName: '', conflict: false };
    for (const prop in focusedPolicy) {
      if (prop === 'channelFilters') {
        for (let i = 0; i < focusedPolicy.channelFilters.length; ++i) {
          channelFilters.push(_getChannelFilterSetting(focusedPolicy.channelFilters[i], _focusedPolicyOrigin, emptyOrigin, prop));
        }
      } else {
        if (!isBlank(focusedPolicy[prop])) {
          const basicSetting = _getBasicSetting(prop, focusedPolicy, _listOfLogServers, _focusedPolicyOrigin, emptyOrigin);
          if (basicSetting) {
            basicSettings.push(basicSetting);
          }
        }
      }
    }
    if (basicSettings.length > 0) {
      policyDetails.push({
        header: 'adminUsm.policies.detail.windowsLogSettings',
        props: basicSettings
      });
    }
    if (channelFilters.length > 0) {
      policyDetails.push({
        header: 'adminUsm.policies.detail.channelFilterSettings',
        channels: channelFilters
      });
    }
    return policyDetails;
  }
);

const _getBasicSetting = (prop, focusedPolicy, _listOfLogServers, _focusedPolicyOrigin, emptyOrigin) => {
  const _i18n = lookup('service:i18n');
  let settingValue = _i18n.t('adminUsm.policies.detail.disabled');
  if (focusedPolicy[prop]) {
    settingValue = _i18n.t('adminUsm.policies.detail.enabled');
  }

  const basicSettings = {
    enabled: {
      name: 'adminUsm.policies.detail.windowsLogPolicyEnabled',
      value: settingValue,
      origin: _focusedPolicyOrigin && _focusedPolicyOrigin[prop] ? _focusedPolicyOrigin[prop] : emptyOrigin
    },
    primaryDestination: {
      name: 'adminUsm.policies.detail.primaryDestination',
      value: _getDisplayName(prop, focusedPolicy[prop], _listOfLogServers),
      origin: _focusedPolicyOrigin && _focusedPolicyOrigin[prop] ? _focusedPolicyOrigin[prop] : emptyOrigin
    },
    secondaryDestination: {
      name: 'adminUsm.policies.detail.secondaryDestination',
      value: _getDisplayName(prop, focusedPolicy[prop], _listOfLogServers),
      origin: _focusedPolicyOrigin && _focusedPolicyOrigin[prop] ? _focusedPolicyOrigin[prop] : emptyOrigin
    },
    protocol: {
      name: 'adminUsm.policies.detail.protocol',
      value: focusedPolicy[prop],
      origin: _focusedPolicyOrigin && _focusedPolicyOrigin[prop] ? _focusedPolicyOrigin[prop] : emptyOrigin
    },
    sendTestLog: {
      name: 'adminUsm.policies.detail.sendTestLog',
      value: settingValue,
      origin: _focusedPolicyOrigin && _focusedPolicyOrigin[prop] ? _focusedPolicyOrigin[prop] : emptyOrigin
    }
  };
  return basicSettings[prop];
};

const _getChannelFilterSetting = (chFilter, _focusedPolicyOrigin, emptyOrigin, prop) => {
  return {
    name: `${chFilter.channel} ${chFilter.filterType}`,
    value: chFilter.eventId,
    origin: _focusedPolicyOrigin && _focusedPolicyOrigin[prop] ? _focusedPolicyOrigin[prop] : emptyOrigin
  };
};

const _getDisplayName = (prop, destAddress, listOfLogServers) => {
  if (prop !== 'primaryDestination' && prop !== 'secondaryDestination') {
    return null;
  }
  let focusedPolicyDestinationName = destAddress;

  const logServer = listOfLogServers.filter((obj) => obj.host === destAddress);
  if (logServer != null && logServer.length === 1) {
    focusedPolicyDestinationName = logServer[0].displayName;
  }
  return focusedPolicyDestinationName;
};