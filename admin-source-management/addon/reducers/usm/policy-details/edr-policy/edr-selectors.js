import reselect from 'reselect';
import { isBlank } from '@ember/utils';
import { lookup } from 'ember-dependency-lookup';
import _ from 'lodash';

const { createSelector } = reselect;

export const focusedPolicy = (state) => state.usm.policies.focusedItem;
const _listOfEndpoints = (state) => state.usm.policyWizard.listOfEndpointServers || [];

/**
 * formats the policy in focus to return an array of sections
 * to the edr policy details template
 * Each section has a header and a list of properties and their values
 * [settingSection[0], settingSection[1] ...]
 * settingSection: {header: '', props: [{name: '', value:''}]}
 * the values are formatted based on other properties in the policy and
 * concatenating translations if needed
 * @public
*/
export const selectedEdrPolicy = createSelector(
  focusedPolicy, _listOfEndpoints,
  (focusedPolicy, _listOfEndpoints) => {
    const _i18n = lookup('service:i18n');

    const policyDetails = [];
    const scanScheduleSettings = [];
    const advancedScanSettings = [];
    const invasiveActionsSettings = [];
    const endpointServersSettings = [];
    const agentSettings = [];
    const advancedConfigSettings = [];

    if (focusedPolicy && focusedPolicy.defaultPolicy &&
      focusedPolicy.policyType === 'edrPolicy' && _.isEmpty(focusedPolicy.primaryAddress)) {
      endpointServersSettings.push({
        name: 'adminUsm.policyWizard.edrPolicy.primaryAddress',
        value: _i18n.t('adminUsm.policies.detail.defaultPrimaryAddress'),
        tooltip: _i18n.t('adminUsm.policies.detail.defaultPrimaryAddressTooltip'),
        defaultEndpointServer: true
      });
    }

    for (const prop in focusedPolicy) {
      if (!isBlank(focusedPolicy[prop])) {
        const scanSetting = _getScanSetting(prop, focusedPolicy);
        const advScanSetting = _getAdvancedScanSetting(prop, focusedPolicy);
        const invActionSetting = _getInvasiveActionsSetting(prop, focusedPolicy);
        const endpointSetting = _getEndpointServerSetting(prop, focusedPolicy, _listOfEndpoints);
        const agentSetting = _getAgentSetting(prop, focusedPolicy);
        const advancedConfigSetting = _getAdvancedConfigSetting(prop, focusedPolicy);

        if (scanSetting) {
          scanScheduleSettings.push(scanSetting);
        } else if (advScanSetting) {
          advancedScanSettings.push(advScanSetting);
        } else if (invActionSetting) {
          invasiveActionsSettings.push(invActionSetting);
        } else if (endpointSetting) {
          endpointServersSettings.push(endpointSetting);
        } else if (agentSetting) {
          agentSettings.push(agentSetting);
        } else if (advancedConfigSetting) {
          advancedConfigSettings.push(advancedConfigSetting);
        }
      }
    }
    if (scanScheduleSettings.length > 0) {
      policyDetails.push({
        header: 'adminUsm.policyWizard.edrPolicy.scanSchedule',
        props: scanScheduleSettings
      });
    }
    if (agentSettings.length > 0) {
      policyDetails.push({
        header: 'adminUsm.policyWizard.edrPolicy.agentSettings',
        props: agentSettings
      });
    }
    if (advancedScanSettings.length > 0) {
      policyDetails.push({
        header: 'adminUsm.policyWizard.edrPolicy.advScanSettings',
        props: advancedScanSettings
      });
    }
    if (invasiveActionsSettings.length > 0) {
      policyDetails.push({
        header: 'adminUsm.policyWizard.edrPolicy.invasiveActions',
        props: invasiveActionsSettings
      });
    }
    if (endpointServersSettings.length > 0) {
      policyDetails.push({
        header: 'adminUsm.policyWizard.edrPolicy.endpointServerSettings',
        props: endpointServersSettings
      });
    }
    if (advancedConfigSettings.length > 0) {
      policyDetails.push({
        header: 'adminUsm.policyWizard.edrPolicy.advancedConfig',
        props: advancedConfigSettings
      });
    }
    return policyDetails;
  }
);

const _getScanSetting = (prop, focusedPolicy) => {
  const _i18n = lookup('service:i18n');

  let scanTypeValue = _i18n.t('adminUsm.policyWizard.edrPolicy.scanTypeManual');
  if (focusedPolicy[prop] === 'ENABLED') {
    scanTypeValue = _i18n.t('adminUsm.policyWizard.edrPolicy.scanTypeScheduled');
  }

  let unit = _i18n.t('adminUsm.policyWizard.edrPolicy.recurrenceIntervalOptions.intervalText.DAYS');
  if (focusedPolicy.recurrenceUnit === 'WEEKS') {
    unit = _i18n.t('adminUsm.policyWizard.edrPolicy.recurrenceIntervalOptions.intervalText.WEEKS');
  }

  let days = '';
  if (focusedPolicy.runOnDaysOfWeek) {
    days = `${_i18n.t('adminUsm.policyWizard.edrPolicy.recurrenceIntervalOptions.on')} ${focusedPolicy.runOnDaysOfWeek.join(',')}`;
  }

  const recurValue = `${_i18n.t('adminUsm.policies.detail.recurrenceEvery')} ${focusedPolicy.recurrenceInterval} ${unit} ${days}`;

  const scanSettings = {
    scanType: {
      name: 'adminUsm.policyWizard.edrPolicy.scanType',
      value: scanTypeValue

    },
    scanStartDate: {
      name: 'adminUsm.policyWizard.edrPolicy.scanStartDate',
      value: focusedPolicy[prop]
    },
    scanStartTime: {
      name: 'adminUsm.policyWizard.edrPolicy.scanStartTime',
      value: focusedPolicy[prop]
    },
    recurrenceInterval: {
      name: 'adminUsm.policyWizard.edrPolicy.recurrenceInterval',
      value: recurValue,
      isTranslation: true
    },
    cpuMax: {
      name: 'adminUsm.policyWizard.edrPolicy.cpuMax',
      value: `${focusedPolicy[prop]} ${'%'}`
    },
    cpuMaxVm: {
      name: 'adminUsm.policyWizard.edrPolicy.cpuMaxVm',
      value: `${focusedPolicy[prop]} ${'%'}`
    }
  };
  return scanSettings[prop];
};

const _getAdvancedScanSetting = (prop, focusedPolicy) => {
  const advancedScanSettings = {
    /* captureFloatingCode: {
      name: 'adminUsm.policyWizard.edrPolicy.captureFloatingCode',
      value: settingValue
    },*/
    scanMbr: {
      name: 'adminUsm.policyWizard.edrPolicy.scanMbr',
      value: _setSelectedValue(focusedPolicy[prop])
    },
    /*
    filterSignedHooks: {
      name: 'adminUsm.policyWizard.edrPolicy.filterSignedHooks',
      value: settingValue
    },*/
    requestScanOnRegistration: {
      name: 'adminUsm.policyWizard.edrPolicy.requestScanOnRegistration',
      value: _setSelectedValue(focusedPolicy[prop])
    }
  };
  return advancedScanSettings[prop];
};

const _getInvasiveActionsSetting = (prop, focusedPolicy) => {
  const invasiveActionSettings = {
    blockingEnabled: {
      name: 'adminUsm.policyWizard.edrPolicy.blockingEnabled',
      value: _setSelectedValue(focusedPolicy[prop])
    }
  };
  return invasiveActionSettings[prop];
};

const _setSelectedValue = (enabled) => {
  const _i18n = lookup('service:i18n');
  let settingValue = _i18n.t('adminUsm.policies.detail.disabled');
  if (enabled) {
    settingValue = _i18n.t('adminUsm.policies.detail.enabled');
  }
  return settingValue;
};

const _getEndpointServerSetting = (prop, focusedPolicy, listOfEndpoints) => {
  const _i18n = lookup('service:i18n');
  let htppsBeaconIntervalUnitValue = _i18n.t('adminUsm.policyWizard.edrPolicy.primaryHttpsBeaconInterval_HOURS');
  let udpBeaconIntervalUnitValue = _i18n.t('adminUsm.policyWizard.edrPolicy.primaryUdpBeaconInterval_SECONDS');
  if (focusedPolicy.primaryHttpsBeaconIntervalUnit === 'MINUTES') {
    htppsBeaconIntervalUnitValue = _i18n.t('adminUsm.policyWizard.edrPolicy.primaryHttpsBeaconInterval_MINUTES');
  }
  if (focusedPolicy.primaryUdpBeaconIntervalUnit === 'MINUTES') {
    udpBeaconIntervalUnitValue = _i18n.t('adminUsm.policyWizard.edrPolicy.primaryUdpBeaconInterval_MINUTES');
  }

  const endpointSettings = {
    primaryAddress: {
      name: 'adminUsm.policyWizard.edrPolicy.primaryAddress',
      value: _getDisplayName(prop, focusedPolicy[prop], listOfEndpoints)
    },
    primaryAlias: {
      name: 'adminUsm.policyWizard.edrPolicy.primaryAlias',
      value: focusedPolicy[prop]
    },
    primaryHttpsPort: {
      name: 'adminUsm.policyWizard.edrPolicy.primaryHttpsPort',
      value: focusedPolicy[prop]
    },
    primaryHttpsBeaconInterval: {
      name: 'adminUsm.policyWizard.edrPolicy.primaryHttpsBeaconInterval',
      value: `${focusedPolicy[prop]} ${htppsBeaconIntervalUnitValue}`
    },
    primaryUdpPort: {
      name: 'adminUsm.policyWizard.edrPolicy.primaryUdpPort',
      value: focusedPolicy[prop]
    },
    primaryUdpBeaconInterval: {
      name: 'adminUsm.policyWizard.edrPolicy.primaryUdpBeaconInterval',
      value: `${focusedPolicy[prop]} ${udpBeaconIntervalUnitValue}`
    },
    rarEnabled: {
      name: 'adminUsm.policyWizard.edrPolicy.rarEnabled',
      value: _setSelectedValue(focusedPolicy[prop])
    }
  };
  return endpointSettings[prop];
};

const _getAgentSetting = (prop, focusedPolicy) => {
  const _i18n = lookup('service:i18n');
  let agentSettingValue = _i18n.t('adminUsm.policyWizard.edrPolicy.insights');
  if (focusedPolicy[prop] === 'ADVANCED') {
    agentSettingValue = _i18n.t('adminUsm.policyWizard.edrPolicy.advanced');
  }

  const agentSettings = {
    agentMode: {
      name: 'adminUsm.policyWizard.edrPolicy.agentMode',
      value: agentSettingValue
    }
  };
  return agentSettings[prop];
};

const _getAdvancedConfigSetting = (prop, focusedPolicy) => {
  const advancedConfigSettings = {
    customConfig: {
      name: 'adminUsm.policyWizard.edrPolicy.customConfig',
      value: _truncateCustomSetting(focusedPolicy[prop])
    }
  };
  return advancedConfigSettings[prop];
};

const _getDisplayName = (prop, primaryAddress, listOfEndpoints) => {
  if (prop !== 'primaryAddress') {
    return null;
  }
  let focusedPolicyPrimaryName = primaryAddress;

  const endpointServer = listOfEndpoints.filter((obj) => obj.host === primaryAddress);
  if (endpointServer != null && endpointServer.length === 1) {
    focusedPolicyPrimaryName = endpointServer[0].displayName;
  }
  return focusedPolicyPrimaryName;
};

const _truncateCustomSetting = (customSetting) => {
  return {
    nonTruncated: customSetting,
    truncatedWithEllipsis: customSetting ? _.truncate(customSetting, { length: 256, omission: '...' }) : null
  };
};