import reselect from 'reselect';
import { isBlank } from '@ember/utils';
import { lookup } from 'ember-dependency-lookup';
import _ from 'lodash';
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

export const general = createSelector(
  policyAdminUsm,
  (policyAdminUsm) => {
    if (policyAdminUsm) {
      return policyAdminUsm.general;
    }
  }
);

export const sources = createSelector(
  policyAdminUsm,
  (policyAdminUsm) => {
    if (policyAdminUsm) {
      return policyAdminUsm.sources;
    }
  }
);

export const selectedEdrPolicy = createSelector(
  policyAdminUsm,
  (policyAdminUsm) => {
    const _i18n = lookup('service:i18n');
    const focusedPolicy = policyAdminUsm.edrPolicy;
    const policyDetails = [];
    const scanScheduleSettings = [];
    const advancedScanSettings = [];
    const invasiveActionsSettings = [];
    const endpointServersSettings = [];
    const agentSettings = [];
    const advancedConfigSettings = [];
    const rarConfigSettings = [];
    const scheduledScan = focusedPolicy.scanType === 'Scheduled';

    if (focusedPolicy && focusedPolicy.defaultPolicy &&
      focusedPolicy.policyType === 'edrPolicy' && _.isEmpty(focusedPolicy.primaryAddress)) {
      endpointServersSettings.push({
        name: 'adminUsm.policyWizard.edrPolicy.primaryAddress',
        value: _i18n.t('adminUsm.policies.detail.defaultPrimaryAddress'),
        tooltip: _i18n.t('adminUsm.policies.detail.defaultPrimaryAddressTooltip'),
        defaultEndpointServer: true,
        origin: {
          groupName: _i18n.t('adminUsm.policies.detail.none'),
          policyName: _i18n.t('adminUsm.policies.detail.defaultEdrPolicy'),
          conflict: false
        }
      });
    }

    for (const prop in focusedPolicy) {
      if (!isBlank(focusedPolicy[prop])) {
        const scanSetting = _getScanSetting(prop, focusedPolicy, scheduledScan);
        const advScanSetting = _getAdvancedScanSetting(prop, focusedPolicy);
        const invActionSetting = _getInvasiveActionsSetting(prop, focusedPolicy);
        const endpointSetting = _getEndpointServerSetting(prop, focusedPolicy);
        const agentSetting = _getAgentSetting(prop, focusedPolicy);
        const advancedConfigSetting = _getAdvancedConfigSetting(prop, focusedPolicy);
        const rarPolicySetting = _getRarPolicySetting(prop, focusedPolicy);

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
        } else if (rarPolicySetting) {
          rarConfigSettings.push(rarPolicySetting);
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
    if (rarConfigSettings.length > 0) {
      policyDetails.push({
        header: 'adminUsm.policyWizard.edrPolicy.relayServer',
        props: rarConfigSettings
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

const _getScanSetting = (prop, focusedPolicy, scheduledScan) => {
  const _i18n = lookup('service:i18n');

  let scanTypeValue = _i18n.t('adminUsm.policyWizard.edrPolicy.scanTypeManual');
  if (focusedPolicy[prop] === 'Scheduled') {
    scanTypeValue = _i18n.t('adminUsm.policyWizard.edrPolicy.scanTypeScheduled');
  }

  let unit = _i18n.t('adminUsm.policyWizard.edrPolicy.recurrenceIntervalOptions.intervalText.DAYS');
  if (focusedPolicy.recurrenceUnit === 'WEEKS') {
    unit = _i18n.t('adminUsm.policyWizard.edrPolicy.recurrenceIntervalOptions.intervalText.WEEKS');
  }

  let days = '';
  if (focusedPolicy.runOnDaysOfWeek) {
    const daysLocale = focusedPolicy.runOnDaysOfWeek.map((day) => _i18n.t(`adminUsm.policyWizard.edrPolicy.recurrenceIntervalOptions.weekDay.${day}`).string).join(', ');
    days = `${_i18n.t('adminUsm.policyWizard.edrPolicy.recurrenceIntervalOptions.on')} ${daysLocale}`;
  }

  const recurValue = `${_i18n.t('adminUsm.policies.detail.recurrenceEvery')} ${focusedPolicy.recurrenceInterval} ${unit} ${days}`;

  let scanSettings = {
    scanType: {
      name: 'adminUsm.policyWizard.edrPolicy.scanType',
      value: scanTypeValue
    }
  };

  if (scheduledScan) {
    scanSettings = {
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
        value: focusedPolicy[prop]
      },
      cpuMaxVm: {
        name: 'adminUsm.policyWizard.edrPolicy.cpuMaxVm',
        value: focusedPolicy[prop]
      }
    };
  }
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
      value: focusedPolicy[prop]
    },
    /*
    filterSignedHooks: {
      name: 'adminUsm.policyWizard.edrPolicy.filterSignedHooks',
      value: settingValue
    },*/
    requestScanOnRegistration: {
      name: 'adminUsm.policyWizard.edrPolicy.requestScanOnRegistration',
      value: focusedPolicy[prop]
    }
  };
  return advancedScanSettings[prop];
};

const _getInvasiveActionsSetting = (prop, focusedPolicy) => {
  const invasiveActionSettings = {
    blockingEnabled: {
      name: 'adminUsm.policyWizard.edrPolicy.blockingEnabled',
      value: focusedPolicy[prop]
    }
  };
  return invasiveActionSettings[prop];
};

const _getRarPolicySetting = (prop, focusedPolicy) => {
  const rarPolicySetting = {
    rarPolicyServer: {
      name: 'adminUsm.policyWizard.edrPolicy.rarPolicyServer',
      value: focusedPolicy[prop]
    },
    rarPolicyPort: {
      name: 'adminUsm.policyWizard.edrPolicy.rarPolicyPort',
      value: focusedPolicy[prop]
    },
    rarPolicyBeaconInterval: {
      name: 'adminUsm.policyWizard.edrPolicy.rarPolicyBeaconInterval',
      value: focusedPolicy[prop]
    }
  };
  return rarPolicySetting[prop];
};

const _getEndpointServerSetting = (prop, focusedPolicy) => {
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
      // value: _getDisplayName(prop, focusedPolicy[prop], listOfEndpoints)
      value: focusedPolicy[prop]
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
    }
  };
  return endpointSettings[prop];
};

const _getAgentSetting = (prop, focusedPolicy) => {
  const _i18n = lookup('service:i18n');
  let agentSettingValue = _i18n.t('adminUsm.policyWizard.edrPolicy.insights');
  if (focusedPolicy[prop] === 'Advanced') {
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
      value: focusedPolicy[prop]
    }
  };
  return advancedConfigSettings[prop];
};
