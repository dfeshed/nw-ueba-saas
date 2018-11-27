import reselect from 'reselect';
import { isBlank } from '@ember/utils';
import { lookup } from 'ember-dependency-lookup';

const { createSelector } = reselect;

export const focusedPolicy = (state) => state.usm.policies.focusedItem;

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
  focusedPolicy,
  (focusedPolicy) => {
    const policyDetails = [];
    const scanScheduleSettings = [];
    const advancedScanSettings = [];
    const invasiveActionsSettings = [];
    const endpointServersSettings = [];
    const agentSettings = [];
    const advancedConfigSettings = [];

    for (const prop in focusedPolicy) {
      if (!isBlank(focusedPolicy[prop])) {
        const scanSetting = _getScanSetting(prop, focusedPolicy);
        const advScanSetting = _getAdvancedScanSetting(prop, focusedPolicy);
        const invActionSetting = _getInvasiveActionsSetting(prop, focusedPolicy);
        const endpointSetting = _getEndpointServerSetting(prop, focusedPolicy);
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
    if (agentSettings.length > 0) {
      policyDetails.push({
        header: 'adminUsm.policyWizard.edrPolicy.agentSettings',
        props: agentSettings
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
  if (focusedPolicy[prop] == 'ENABLED') {
    scanTypeValue = _i18n.t('adminUsm.policyWizard.edrPolicy.scanTypeScheduled');
  }

  let unit = _i18n.t('adminUsm.policyWizard.edrPolicy.recurrenceInterval.intervalText.DAYS');
  if (focusedPolicy.recurrenceUnit === 'WEEKS') {
    unit = _i18n.t('adminUsm.policyWizard.edrPolicy.recurrenceInterval.intervalText.WEEKS');
  }

  let days = '';
  if (focusedPolicy.runOnDaysOfWeek) {
    days = `${_i18n.t('adminUsm.policyWizard.edrPolicy.recurrenceInterval.on')} ${focusedPolicy.runOnDaysOfWeek.join(',')}`;
  }

  const recurValue = `${_i18n.t('adminUsm.policies.detail.recurrenceEvery')} ${focusedPolicy.recurrenceInterval} ${unit} ${days}`;

  const scanSettings = {
    scanType: {
      name: 'adminUsm.policyWizard.edrPolicy.schedOrManScan',
      value: scanTypeValue

    },
    scanStartDate: {
      name: 'adminUsm.policyWizard.edrPolicy.effectiveDate',
      value: focusedPolicy[prop]
    },
    scanStartTime: {
      name: 'adminUsm.policyWizard.edrPolicy.startTime',
      value: focusedPolicy[prop]
    },
    recurrenceInterval: {
      name: 'adminUsm.policyWizard.edrPolicy.scanFrequency',
      value: recurValue,
      isTranslation: true
    },
    cpuMax: {
      name: 'adminUsm.policyWizard.edrPolicy.cpuMax',
      value: `${focusedPolicy[prop]} ${'%'}`
    },
    cpuMaxVm: {
      name: 'adminUsm.policyWizard.edrPolicy.vmMax',
      value: `${focusedPolicy[prop]} ${'%'}`
    }
  };
  return scanSettings[prop];
};

const _getAdvancedScanSetting = (prop, focusedPolicy) => {
  const _i18n = lookup('service:i18n');
  let settingValue = _i18n.t('adminUsm.policies.detail.disabled');
  if (focusedPolicy[prop]) {
    settingValue = _i18n.t('adminUsm.policies.detail.enabled');
  }

  const advancedScanSettings = {
    /* captureFloatingCode: {
      name: 'adminUsm.policyWizard.edrPolicy.captureFloatingCode',
      value: settingValue
    },*/
    downloadMbr: {
      name: 'adminUsm.policyWizard.edrPolicy.downloadMbr',
      value: settingValue
    },
    /*
    filterSignedHooks: {
      name: 'adminUsm.policyWizard.edrPolicy.filterSignedHooks',
      value: settingValue
    },*/
    requestScanOnRegistration: {
      name: 'adminUsm.policyWizard.edrPolicy.requestScanOnRegistration',
      value: settingValue
    }
  };
  return advancedScanSettings[prop];
};

const _getInvasiveActionsSetting = (prop, focusedPolicy) => {
  const _i18n = lookup('service:i18n');
  let settingValue = _i18n.t('adminUsm.policies.detail.disabled');
  if (focusedPolicy[prop]) {
    settingValue = _i18n.t('adminUsm.policies.detail.enabled');
  }

  const invasiveActionSettings = {
    blockingEnabled: {
      name: 'adminUsm.policyWizard.edrPolicy.blockingEnabled',
      value: settingValue
    }
  };
  return invasiveActionSettings[prop];
};

const _getEndpointServerSetting = (prop, focusedPolicy) => {
  const _i18n = lookup('service:i18n');
  let beaconIntervalUnitValue = _i18n.t('adminUsm.policyWizard.edrPolicy.primaryHttpsBeaconInterval_HOURS');
  if (focusedPolicy.primaryHttpsBeaconIntervalUnit == 'MINUTES') {
    beaconIntervalUnitValue = _i18n.t('adminUsm.policyWizard.edrPolicy.primaryHttpsBeaconInterval_MINUTES');
  }

  const endpointSettings = {
    primaryAddress: {
      name: 'adminUsm.policyWizard.edrPolicy.primaryAddress',
      value: focusedPolicy[prop]
    },
    primaryHttpsPort: {
      name: 'adminUsm.policyWizard.edrPolicy.primaryHttpsPort',
      value: focusedPolicy[prop]
    },
    primaryHttpsBeaconInterval: {
      name: 'adminUsm.policyWizard.edrPolicy.primaryHttpsBeaconInterval',
      value: `${focusedPolicy[prop]} ${beaconIntervalUnitValue}`
    },
    primaryUdpPort: {
      name: 'adminUsm.policyWizard.edrPolicy.primaryUdpPort',
      value: focusedPolicy[prop]
    },
    primaryUdpBeaconInterval: {
      name: 'adminUsm.policyWizard.edrPolicy.primaryUdpBeaconInterval',
      value: `${focusedPolicy[prop]} ${beaconIntervalUnitValue}`
    }
  };
  return endpointSettings[prop];
};

const _getAgentSetting = (prop, focusedPolicy) => {
  const _i18n = lookup('service:i18n');
  let agentSettingValue = _i18n.t('adminUsm.policyWizard.edrPolicy.noMonitoring');
  if (focusedPolicy[prop] === 'FULL_MONITORING') {
    agentSettingValue = _i18n.t('adminUsm.policyWizard.edrPolicy.fullMonitoring');
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
      name: 'adminUsm.policyWizard.edrPolicy.customConfigSetting',
      value: focusedPolicy[prop]
    }
  };
  return advancedConfigSettings[prop];
};