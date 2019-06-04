import reselect from 'reselect';
import securityConfig from './config';
import { secondsToMinutesConverter } from 'investigate-hosts/reducers/details/selector-utils';
import { isOSWindows, isModeAdvance, isAgentVersionAdvanced } from 'investigate-hosts/reducers/utils/mft-utils';
import _ from 'lodash';

const { createSelector } = reselect;

const _hostDetails = (state) => state.endpoint.overview.hostDetails || [];
const _hostOverview = (state) => state.endpoint.overview.hostOverview || {};
const _snapShots = (state) => state.endpoint.detailsInput.snapShots;
const _scanTime = (state) => state.endpoint.detailsInput.scanTime;
const _exportJsonStatus = (state) => state.endpoint.overview.exportJSONStatus;
const _agentStatus = (state) => state.endpoint.machines.agentStatus;
const _downloadId = (state) => state.endpoint.overview.downloadId;
const _arrangeSecurityConfigsBy = (state) => state.endpoint.overview.arrangeSecurityConfigsBy;
const _policyDetails = (state) => state.endpoint.overview.policyDetails || {};
const _serverId = (state) => state.endpointQuery.serverId;
const _selectedMachineServerId = (state) => state.endpointQuery.selectedMachineServerId;
const _activePropertyPanelTab = (state) => state.endpoint.visuals.activePropertyPanelTab;

const _hostAgentStatus = createSelector(
  _hostOverview,
  (hostOverview) => {
    return hostOverview && hostOverview.agentStatus;
  }
);

export const machineOsType = createSelector(
  _hostOverview,
  (hostOverview) => {
    if (hostOverview && hostOverview.machineIdentity) {
      return hostOverview.machineIdentity.machineOsType;
    }
    return 'windows';
  }
);

export const hostWithStatus = createSelector(
  _hostOverview, _hostAgentStatus, _agentStatus,
  (hostOverview, hostAgentStatus, agentStatus) => {
    if (hostAgentStatus) {
      if (hostOverview.id === (agentStatus ? agentStatus.agentId : null)) {
        return {
          ...hostOverview,
          agentStatus
        };
      } else {
        return {
          ...hostOverview,
          agentStatus: hostAgentStatus
        };
      }
    }

  }
);

export const hostName = createSelector(
  _hostOverview,
  (hostOverview) => hostOverview && hostOverview.machineIdentity ? hostOverview.machineIdentity.machineName : ''
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
  _hostOverview,
  (hostOverview) => {
    if (hostOverview && hostOverview.machineIdentity) {
      const { machineIdentity: { agentVersion } } = hostOverview;
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
  [_policyDetails, _hostOverview],
  (policyDetails, hostOverview) => {
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
        const weekDay = runOnDaysOfWeek ? week[runOnDaysOfWeek[0]] : '';
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
        ...hostOverview, scheduleConfig
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
        ...hostOverview, scheduleConfig
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
          const weekDay = runOnDaysOfWeek ? week[runOnDaysOfWeek[0]] : '';
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
    const windowsLogPolicy = policy && policy.windowsLogPolicy ? policy.windowsLogPolicy : null;
    if (windowsLogPolicy) {
      const { enabled, sendTestLog } = windowsLogPolicy;
      return {
        ...windowsLogPolicy,
        enabled: enabled ? 'Enabled' : 'Disabled',
        sendTestLog: sendTestLog ? 'Enabled' : 'Disabled'
      };
    }
  }
);

export const getPoliciesPropertyData = createSelector(
  [_policyDetails, _hostOverview, _getscheduledScanConfig, _getWindowsLogPolicy],
  (policyDetails, hostOverview, scheduledScanConfig, windowsLogPolicy) => {
    const policyStatus = hostOverview.groupPolicy ? hostOverview.groupPolicy.policyStatus : null;
    const { policy, evaluatedTime, message } = policyDetails;
    const edrPolicy = policy && policy.edrPolicy ? policy.edrPolicy : null;
    let policiesPropertyData = {};
    if (windowsLogPolicy) {
      policiesPropertyData = {
        ...policy,
        policyStatus,
        evaluatedTime,
        message,
        windowsLogPolicy
      };
    }
    if (edrPolicy) {
      const { blockingConfig, serverConfig, transportConfig } = edrPolicy;
      let newTransportConfig = {};
      if (transportConfig) {
        const { primary } = transportConfig;
        const { httpsBeaconIntervalInSeconds, udpBeaconIntervalInSeconds, rar = {} } = primary;
        let config = {};
        if (rar) {
          const { servers = [{}] } = rar;
          const [{ httpsBeaconIntervalInSeconds }] = servers;
          const httpsBeaconInterval = httpsBeaconIntervalInSeconds ? secondsToMinutesConverter(httpsBeaconIntervalInSeconds) : '';
          config = { ...servers[0], httpsBeaconInterval };
        }
        newTransportConfig = {
          primary: {
            ...primary,
            httpsBeaconInterval: secondsToMinutesConverter(httpsBeaconIntervalInSeconds),
            udpBeaconInterval: secondsToMinutesConverter(udpBeaconIntervalInSeconds),
            rar: {
              ...rar,
              config
            }
          }
        };
      }
      policiesPropertyData = {
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
    return policiesPropertyData;
  }
);

export const selectedSnapshot = createSelector(
  [_scanTime, _snapShots],
  (scanTime, snapShots) => snapShots ? snapShots.find((snapshot) => snapshot.scanStartTime === scanTime) : null);

export const showWindowsLogPolicy = createSelector([_getWindowsLogPolicy], (windowsLogPolicy) => !!windowsLogPolicy);

export const channelFiltersConfig = createSelector(
  [_getWindowsLogPolicy],
  (windowsLogPolicy) => {
    let config = null;
    if (windowsLogPolicy) {
      const { channelFilters } = windowsLogPolicy;
      if (Array.isArray(channelFilters) && channelFilters.length) {
        config = {
          sectionName: 'Channel Filters Settings',
          fields: channelFilters.map((filter) => {
            return {
              labelKey: `${filter.channel} ${filter.filterType}`,
              field: `${filter.eventId}`,
              isStandardString: true
            };
          })
        };
      }
    }
    return config;
  }
);

export const hostOverviewServerId = createSelector(
  [_hostOverview], (hostOverview) => hostOverview.serviceId
);

export const policiesUnavailableMessage = createSelector(
  [_activePropertyPanelTab, getPoliciesPropertyData],
  (tab, policiesPropertyData) => tab === 'POLICIES' && _.isEmpty(policiesPropertyData) ? 'Policy unavailable' : null
);

export const mftDownloadButtonStatusDetails = createSelector(
  [_hostDetails],
  ({ machineIdentity = {} }) => {
    const { machineOsType, agentMode, agentVersion } = machineIdentity;
    let isMFTEnabled = false;
    if (isOSWindows(machineOsType) && isModeAdvance(agentMode) && isAgentVersionAdvanced(agentVersion)) {
      isMFTEnabled = true;
    }
    return { isDisplayed: isMFTEnabled };
  }
);

export const getRARStatus = createSelector(
  [_hostDetails],
  ({ agentStatus = {} }) => (agentStatus.lastSeen === 'RelayServer')
);