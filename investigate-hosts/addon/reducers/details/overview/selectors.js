import reselect from 'reselect';
import securityConfig from './config';
import { secondsToMinutesConverter } from 'investigate-hosts/reducers/details/selector-utils';

const { createSelector } = reselect;

const _hostDetails = (state) => state.endpoint.overview.hostDetails || [];
const _snapShots = (state) => state.endpoint.detailsInput.snapShots;
const _exportJsonStatus = (state) => state.endpoint.overview.exportJSONStatus;
const _agentStatus = (state) => state.endpoint.machines.agentStatus;
const _downloadId = (state) => state.endpoint.overview.downloadId;
const _arrangeSecurityConfigsBy = (state) => state.endpoint.overview.arrangeSecurityConfigsBy;
const _policyDetails = (state) => state.endpoint.overview.policyDetails || {};
const _serverId = (state) => state.endpointQuery.serverId;
const _selectedMachineServerId = (state) => state.endpointQuery.selectedMachineServerId;

const _hostAgentStatus = createSelector(
  _hostDetails,
  (hostDetails) => {
    return hostDetails && hostDetails.agentStatus;
  }
);

export const machineOsType = createSelector(
  _hostDetails,
  (hostDetails) => {
    if (hostDetails && hostDetails.machineIdentity) {
      return hostDetails.machineIdentity.machineOsType;
    }
    return 'windows';
  }
);

export const hostWithStatus = createSelector(
  _hostDetails, _hostAgentStatus, _agentStatus,
  (hostDetails, hostAgentStatus, agentStatus) => {
    if (hostAgentStatus) {
      if (hostDetails.id === (agentStatus ? agentStatus.agentId : null)) {
        return {
          ...hostDetails,
          agentStatus
        };
      } else {
        return {
          ...hostDetails,
          agentStatus: hostAgentStatus
        };
      }
    }

  }
);

export const hostName = createSelector(
  _hostDetails,
  (hostDetails) => hostDetails && hostDetails.machineIdentity ? hostDetails.machineIdentity.machineName : ''
);

export const lastScanTime = createSelector(
  _snapShots,
  (snapShots) => snapShots && snapShots.length ? snapShots[0] : ''
);


export const isJsonExportCompleted = createSelector(
  _exportJsonStatus,
  (exportJsonStatus) => exportJsonStatus === 'completed'
);

export const isSnapshotsAvailable = createSelector(
  _snapShots,
  (snapShots) => snapShots && snapShots.length
);

export const downloadLink = createSelector(
  [ _downloadId, _selectedMachineServerId, _serverId ],
  (downloadId, selectedMachineServerId, serverId) => {
    if (downloadId) {
      if (selectedMachineServerId) {
        return `${location.origin}/rsa/endpoint/${selectedMachineServerId}/machine/download?id=${downloadId}`;
      } else if (serverId) {
        return `${location.origin}/rsa/endpoint/${serverId}/machine/download?id=${downloadId}`;
      }
      return null;
    }
  }
);

export const getSecurityConfigurations = createSelector(
  _hostDetails,
  ({ machine }) => {
    if (machine) {
      return machine.securityConfigurations || [];
    } else {
      return [];
    }
  }
);

export const _osTypeSecurityConfig = createSelector(
  [ machineOsType, getSecurityConfigurations ],
  (osType, config) => securityConfig[osType].map((sc) => {
    const disabled = config.some((c) => c.includes(sc.keyword));
    const label = disabled ? sc.label.red : sc.label.green;
    return { ...sc, label, disabled };
  })
);

/**
 * selector to sort the security configs based on arrange by radio button
 * @public
 */
export const arrangedSecurityConfigs = createSelector(
  [ _osTypeSecurityConfig, _arrangeSecurityConfigsBy ],
  (securityConfig, arrangeBy) => arrangeBy === 'status' ? securityConfig.sortBy('disabled').reverse() : securityConfig.sortBy('value')
);

/**
 * selector to check whether all the security configs status are same
 * @public
 */
export const sameConfigStatus = createSelector(
  _osTypeSecurityConfig,
  (securityConfig) => securityConfig.every((c) => c.disabled) || securityConfig.every((c) => !c.disabled)
);

/**
 * selector to check whether the agent is a 4.4 agent or not
 * @public
 */
export const isEcatAgent = createSelector(
  _hostDetails,
  (hostDetails) => {
    if (hostDetails && hostDetails.machineIdentity) {
      const { machineIdentity: { agentVersion } } = hostDetails;
      return agentVersion && agentVersion.startsWith('4.4');
    }
    return false;
  }
);

export const isMachineWindows = createSelector(
  machineOsType,
  (machineOsType) => machineOsType && (machineOsType.toLowerCase() === 'windows')
);

export const getPropertyData = createSelector(
  [_policyDetails, _hostDetails],
  (policyDetails, hostDetails) => {
    const edrPolicy = policyDetails.policy ? policyDetails.policy.edrPolicy : {};
    if (edrPolicy.scheduledScanConfig &&
      edrPolicy.scheduledScanConfig.recurrentSchedule &&
      edrPolicy.scheduledScanConfig.scanOptions) {
      const { scheduledScanConfig } = edrPolicy;
      const { recurrentSchedule, scanOptions } = scheduledScanConfig;
      const { recurrence, runAtTime, runOnDaysOfWeek, scheduleStartDate } = recurrentSchedule;
      const { unit, interval } = recurrence;
      let scanInterval = '';
      if (unit === 'DAYS') {
        scanInterval = (interval === 1 ? 'Every Day' : `Every ${interval} Days`);
      } else {
        const week = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
        const weekDay = week[runOnDaysOfWeek[0]];
        scanInterval = `Every ${interval} week(s) on ${weekDay}`;
      }
      const scheduleConfig = {
        enabled: edrPolicy.scheduledScanConfig.enabled,
        scanInterval,
        runAtTime,
        scheduleStartDate,
        scanOptions
      };
      return {
        ...hostDetails, scheduleConfig
      };
    } else {
      const scheduleConfig = {
        enabled: '',
        scanInterval: '',
        runAtTime: '',
        scheduleStartDate: '',
        scanOptions: ''
      };
      return {
        ...hostDetails, scheduleConfig
      };
    }
  }
);

const _getscheduledScanConfig = createSelector(
  [_policyDetails],
  (policyDetails) => {
    const { policy } = policyDetails;
    const edrPolicy = policy && policy.edrPolicy ? policy.edrPolicy : {};
    const { scheduledScanConfig } = edrPolicy;
    let newScheduledScanConfig = {};
    if (edrPolicy.scheduledScanConfig) {
      const { recurrentSchedule, scanOptions } = scheduledScanConfig;
      newScheduledScanConfig = {
        ...scheduledScanConfig,
        enabled: scheduledScanConfig.enabled ? 'Scheduled' : 'Manual'
      };
      if (edrPolicy.scheduledScanConfig.recurrentSchedule) {
        const { recurrence, runOnDaysOfWeek } = recurrentSchedule;
        const { unit, interval } = recurrence;
        let scanInterval = '';
        if (unit === 'DAYS') {
          scanInterval = (interval === 1 ? 'Every Day' : `Every ${interval} Days`);
        } else {
          const week = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
          const weekDay = week[runOnDaysOfWeek[0]];
          scanInterval = `Every ${interval} week(s) on ${weekDay}`;
        }
        newScheduledScanConfig = {
          ...newScheduledScanConfig,
          scanInterval
        };
      }
      if (scanOptions) {
        const { cpuMax, cpuMaxVm, scanMbr } = scanOptions;
        newScheduledScanConfig = {
          ...newScheduledScanConfig,
          scanOptions: {
            ...scanOptions,
            cpuMax: cpuMax ? `${cpuMax} %` : undefined,
            cpuMaxVm: cpuMaxVm ? `${cpuMaxVm} %` : undefined,
            scanMbr: scanMbr ? 'Enabled' : 'Disabled'
          }
        };
      }
    }
    return newScheduledScanConfig;
  }
);

export const _getWindowsLogPolicy = createSelector(
  [_policyDetails],
  (policyDetails) => {
    const { policy } = policyDetails;
    const windowsLogPolicy = policy && policy.windowsLogPolicy ? policy.windowsLogPolicy : {};
    const { enabled, sendTestLog } = windowsLogPolicy;
    return {
      ...windowsLogPolicy,
      enabled: enabled ? 'Enabled' : 'Disabled',
      sendTestLog: sendTestLog ? 'Enabled' : 'Disabled'
    };
  }
);

export const getPoliciesPropertyData = createSelector(
  [_policyDetails, _hostDetails, _getscheduledScanConfig, _getWindowsLogPolicy],
  (policyDetails, hostDetails, scheduledScanConfig, windowsLogPolicy) => {
    const policyStatus = hostDetails.groupPolicy ? hostDetails.groupPolicy.policyStatus : null;
    const { policy, evaluatedTime, message } = policyDetails;
    const edrPolicy = policy && policy.edrPolicy ? policy.edrPolicy : {};
    const { blockingConfig, serverConfig, transportConfig } = edrPolicy;
    let newTransportConfig = {};
    if (transportConfig) {
      const { primary } = transportConfig;
      const { httpsBeaconIntervalInSeconds, udpBeaconIntervalInSeconds } = primary;
      newTransportConfig = {
        primary: {
          ...primary,
          httpsBeaconInterval: secondsToMinutesConverter(httpsBeaconIntervalInSeconds),
          udpBeaconInterval: secondsToMinutesConverter(udpBeaconIntervalInSeconds)
        }
      };
    }
    return {
      ...policy,
      policyStatus,
      evaluatedTime,
      message,
      windowsLogPolicy,
      edrPolicy: {
        ...edrPolicy,
        agentMode: edrPolicy.agentMode && edrPolicy.agentMode === 'INSIGHTS' ? 'Insights' : 'Advanced',
        blockingConfig: {
          enabled: blockingConfig && blockingConfig.enabled ? 'Enabled' : 'Disabled'
        },
        serverConfig: {
          requestScanOnRegistration: serverConfig && serverConfig.requestScanOnRegistration ? 'Enabled' : 'Disabled'
        },
        transportConfig: newTransportConfig,
        scheduledScanConfig
      }
    };
  }
);