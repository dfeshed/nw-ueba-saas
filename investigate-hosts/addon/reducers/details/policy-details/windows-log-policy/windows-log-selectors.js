import reselect from 'reselect';
import { isBlank } from '@ember/utils';
import { lookup } from 'ember-dependency-lookup';

import { policyAdminUsm } from 'investigate-hosts/reducers/details/overview/selectors';

const { createSelector } = reselect;

/**
 * formats the policy in focus to return an array of sections
 * to the edr policy details template
 * Each section has a header and a list of properties and their values
 * [settingSection[0], settingSection[1] ...]
 * settingSection: {header: '', props: [{ name: '', value:'' }]
 * the values are formatted based on other properties in the policy and
 * concatenating translations if needed
 * @public
*/

const _listOfLogServers = () => []; // to do
// const _listOfEndpoints = (state) => state.usm.policyWizard.listOfEndpointServers || [];

export const selectedWindowsLogPolicy = createSelector(
  policyAdminUsm, _listOfLogServers,
  (policyAdminUsm, _listOfLogServers) => {
    const focusedPolicy = policyAdminUsm.windowsLogPolicy;
    const policyDetails = [];
    const channelFilters = [];
    const basicSettings = [];
    const windowsLogPolicyEnabled = focusedPolicy ? focusedPolicy.enabled : '';

    for (const prop in focusedPolicy) {
      if (prop === 'channelFilters') {
        for (let i = 0; i < focusedPolicy.channelFilters.length; ++i) {
          channelFilters.push(_getChannelFilterSetting(focusedPolicy.channelFilters[i], prop));
        }
      } else {
        if (!isBlank(focusedPolicy[prop])) {
          const basicSetting = _getBasicSetting(prop, focusedPolicy, _listOfLogServers, windowsLogPolicyEnabled);
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

const _getBasicSetting = (prop, focusedPolicy, _listOfLogServers, windowsLogPolicyEnabled) => {
  const _i18n = lookup('service:i18n');
  let basicSettings = {};
  if (windowsLogPolicyEnabled === 'Enabled' && windowsLogPolicyEnabled != '') {
    basicSettings = {
      enabled: {
        name: 'adminUsm.policies.detail.windowsLogPolicyEnabled',
        value: _i18n.t('adminUsm.policies.detail.enabled')
      },
      primaryDestination: {
        name: 'adminUsm.policies.detail.primaryDestination',
        value: _getDisplayName(prop, focusedPolicy[prop], _listOfLogServers)
      },
      secondaryDestination: {
        name: 'adminUsm.policies.detail.secondaryDestination',
        value: _getDisplayName(prop, focusedPolicy[prop], _listOfLogServers)
      },
      protocol: {
        name: 'adminUsm.policies.detail.protocol',
        value: focusedPolicy[prop]
      },
      sendTestLog: {
        name: 'adminUsm.policies.detail.sendTestLog',
        value: _i18n.t('adminUsm.policies.detail.enabled')
      }
    };
  } else {
    basicSettings = {
      enabled: {
        name: 'adminUsm.policies.detail.windowsLogPolicyEnabled',
        value: _i18n.t('adminUsm.policies.detail.disabled')
      }
    };
  }
  return basicSettings[prop];
};

const _getChannelFilterSetting = (chFilter) => {
  return {
    name: `${chFilter.channel} ${chFilter.filterType}`,
    value: chFilter.eventId
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