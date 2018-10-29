import reselect from 'reselect';
import { isBlank } from '@ember/utils';
import { lookup } from 'ember-dependency-lookup';

const { createSelector } = reselect;

export const focusedPolicy = (state) => state.usm.policies.focusedItem;

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
  focusedPolicy,
  (focusedPolicy) => {
    const policyDetails = [];
    const channelFilters = [];
    const basicSettings = [];

    for (const prop in focusedPolicy) {
      if (prop === 'channelFilters') {
        for (let i = 0; i < focusedPolicy.channelFilters.length; ++i) {
          channelFilters.push(_getChannelFilterSetting(focusedPolicy.channelFilters[i]));
        }
      } else {
        if (!isBlank(focusedPolicy[prop])) {
          const basicSetting = _getBasicSetting(prop, focusedPolicy);
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

const _getBasicSetting = (prop, focusedPolicy) => {
  const _i18n = lookup('service:i18n');
  let settingValue = _i18n.t('adminUsm.policies.detail.disabled');
  if (focusedPolicy[prop]) {
    settingValue = _i18n.t('adminUsm.policies.detail.enabled');
  }

  const basicSettings = {
    enabled: {
      name: 'adminUsm.policies.detail.windowsLogPolicyEnabled',
      value: settingValue
    },
    primaryDestination: {
      name: 'adminUsm.policies.detail.primaryDestination',
      value: focusedPolicy[prop]
    },
    secondaryDestination: {
      name: 'adminUsm.policies.detail.secondaryDestination',
      value: focusedPolicy[prop]
    },
    protocol: {
      name: 'adminUsm.policies.detail.protocol',
      value: focusedPolicy[prop]
    },
    sendTestLog: {
      name: 'adminUsm.policies.detail.sendTestLog',
      value: settingValue
    }
  };
  return basicSettings[prop];
};

const _getChannelFilterSetting = (chFilter) => {
  return {
    name: `${chFilter.channel} ${chFilter.filterType}`,
    value: chFilter.eventId
  };
};