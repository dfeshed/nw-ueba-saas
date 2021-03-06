import reselect from 'reselect';
import securityConfig from './config';
import { secondsToMinutesConverter } from 'investigate-hosts/reducers/details/selector-utils';
import { isOSWindows, isModeAdvance, isAgentVersionAdvanced } from 'investigate-hosts/reducers/utils/mft-utils';
import { isBrokerView } from 'investigate-shared/selectors/broker-load-more/selectors';
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

const _setEnableDisableValues = (property, key = 'enabled') => {
  return property && property[key] ? 'Enabled' : 'Disabled';
};

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
      const { blockingConfig, isolationConfig, serverConfig, transportConfig } = edrPolicy;
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
            enabled: _setEnableDisableValues(blockingConfig)
          },
          isolationConfig: {
            enabled: _setEnableDisableValues(isolationConfig)
          },
          serverConfig: {
            requestScanOnRegistration: _setEnableDisableValues(serverConfig, 'requestScanOnRegistration')
          },
          transportConfig: newTransportConfig,
          scheduledScanConfig
        }
      };
    }
    return policiesPropertyData;
  }
);

// create Admin USM policy model and export it to policy-details selectors
export const policyAdminUsm = createSelector(
  [getPoliciesPropertyData],
  (data) => {
    if (data.edrPolicy) {
      const inSeconds = data.edrPolicy.transportConfig.primary.httpsBeaconIntervalInSeconds;
      const httpsBeaconIntervalInMinutes = inSeconds ? Math.trunc(inSeconds / 60) : '';
      const runOnDaysOfWeekVal = data.edrPolicy.scheduledScanConfig.recurrentSchedule.runOnDaysOfWeek;
      const week = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'];
      const weekDay = runOnDaysOfWeekVal ? runOnDaysOfWeekVal.map((day) => week[day]) : [];
      const scanStartTime = data.edrPolicy.scheduledScanConfig.recurrentSchedule.runAtTime;
      const runAtTime = scanStartTime.substring(0, scanStartTime.lastIndexOf(':'));
      const fileDownloadEnabled = _setEnableDisableValues(data.edrPolicy.fileDownloadConfig);
      return {
        general: {
          policyStatus: data.policyStatus,
          evaluatedTime: data.evaluatedTime,
          errorDescription: data.message
        },
        sources: {
          hasWindowsLogPolicy: data.windowsLogPolicy != undefined,
          hasFilePolicy: data.filePolicy != undefined
        },
        edrPolicy: {
          name: data.edrPolicy.name,
          scanType: data.edrPolicy.scheduledScanConfig.enabled,
          scanStartDate: data.edrPolicy.scheduledScanConfig.recurrentSchedule.scheduleStartDate,
          scanStartTime: runAtTime,
          recurrenceInterval: data.edrPolicy.scheduledScanConfig.recurrentSchedule.recurrence.interval,
          recurrenceUnit: data.edrPolicy.scheduledScanConfig.recurrentSchedule.recurrence.unit,
          runOnDaysOfWeek: weekDay,
          cpuMax: data.edrPolicy.scheduledScanConfig.scanOptions.cpuMax,
          cpuMaxVm: data.edrPolicy.scheduledScanConfig.scanOptions.cpuMaxVm,
          scanMbr: data.edrPolicy.scheduledScanConfig.scanOptions.scanMbr,
          blockingEnabled: data.edrPolicy.blockingConfig.enabled,
          isolationEnabled: data.edrPolicy.isolationConfig.enabled,
          fileDownloadEnabled,
          fileDownloadCriteria: data.edrPolicy?.fileDownloadConfig?.criteria,
          maxFileDownloadSize: data.edrPolicy?.fileDownloadConfig?.maxSize,
          maxFileDownloadSizeUnit: data.edrPolicy.maxFileDownloadSizeUnit,
          requestScanOnRegistration: data.edrPolicy.serverConfig.requestScanOnRegistration,
          primaryAddress: data.edrPolicy.transportConfig.primary.address,
          primaryHttpsPort: data.edrPolicy.transportConfig.primary.httpsPort,
          primaryHttpsBeaconInterval: httpsBeaconIntervalInMinutes,
          primaryHttpsBeaconIntervalUnit: 'MINUTES',
          primaryUdpPort: data.edrPolicy.transportConfig.primary.udpPort,
          primaryUdpBeaconInterval: data.edrPolicy.transportConfig.primary.udpBeaconIntervalInSeconds,
          primaryUdpBeaconIntervalUnit: 'SECONDS',
          agentMode: data.edrPolicy.agentMode,
          offlineDiskStorageSizeInMb: data.edrPolicy.storageConfig.diskCacheSizeInMb,
          rarPolicyServer: data.edrPolicy.transportConfig.primary.rar.config.address,
          rarPolicyPort: data.edrPolicy.transportConfig.primary.rar.config.httpsPort,
          rarPolicyBeaconInterval: data.edrPolicy.transportConfig.primary.rar.config.httpsBeaconInterval,
          customConfig: data.edrPolicy.customConfig
        },
        windowsLogPolicy: data.windowsLogPolicy ? { // windowsLogPolicy props must mantain same order as in USM model
          enabled: data.windowsLogPolicy.enabled,
          primaryDestination: data.windowsLogPolicy.primaryDestination,
          secondaryDestination: data.windowsLogPolicy.secondaryDestination,
          protocol: data.windowsLogPolicy.protocol,
          sendTestLog: data.windowsLogPolicy.sendTestLog,
          customConfig: data.windowsLogPolicy.customConfig,
          channelFilters: data.windowsLogPolicy.channelFilters ? data.windowsLogPolicy.channelFilters : []
        } : {},
        filePolicy: data.filePolicy ? {
          enabled: data.filePolicy.enabled,
          primaryDestination: data.filePolicy.primaryDestination,
          secondaryDestination: data.filePolicy.secondaryDestination,
          protocol: data.filePolicy.protocol,
          sendTestLog: data.filePolicy.sendTestLog,
          customConfig: data.filePolicy.customConfig,
          sources: data.filePolicy.sources ? data.filePolicy.sources : []
        } : {}
      };
    } else {
      return {};
    }
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

export const isAgentMigrated = createSelector(
  [_hostDetails, isBrokerView],
  ({ groupPolicy }, isBrokerView) => {
    const { managed } = groupPolicy ? groupPolicy : { managed: false };
    return !managed && (!isBrokerView);
  }
);

export const mftDownloadButtonStatusDetails = createSelector(
  [_hostOverview],
  (hostOverview) => {
    const { machineIdentity = {}, agentStatus: { lastSeen } } = hostOverview.agentStatus ? hostOverview : { agentStatus: {} };

    const { machineOsType, agentMode, agentVersion } = machineIdentity;
    let isMFTEnabled = false;
    if (isOSWindows(machineOsType) && isModeAdvance(agentMode) && isAgentVersionAdvanced(agentVersion) && lastSeen !== 'RelayServer') {
      isMFTEnabled = true;
    }
    return { isDisplayed: isMFTEnabled };
  }
);

export const isolationStatus = createSelector(
  [_hostOverview, _policyDetails],
  (hostOverview, policyDetails) => {
    const { enabled = false } = policyDetails?.policy?.edrPolicy?.isolationConfig || {};
    const { machineIdentity = {}, agentStatus: { isolationStatus } } = hostOverview.agentStatus ? hostOverview : { agentStatus: {} };
    // Isolated Key is needed to check if machine has been isolated and exclusion list can be edited.
    const isIsolated = isolationStatus?.isolated || false;
    const { machineOsType, agentMode, agentVersion } = machineIdentity;
    let isIsolationEnabled = false;
    if (isOSWindows(machineOsType) && isModeAdvance(agentMode) && isAgentVersionAdvanced(agentVersion)) {
      isIsolationEnabled = true;
    }
    return { isIsolationEnabled: (enabled && isIsolationEnabled), isIsolated };
  }
);

export const getRARStatus = createSelector(
  [_hostDetails],
  ({ agentStatus = {} }) => (agentStatus.lastSeen === 'RelayServer')
);

export const manualFileDownloadVisualStatus = createSelector(
  [_hostOverview],
  (hostOverview) => {
    const minorVersionNumber = 5;
    const { machineIdentity = {} } = hostOverview;

    const { agentMode, agentVersion } = machineIdentity;
    let isManualFileDownloadAllowed = false;
    if (isModeAdvance(agentMode) && isAgentVersionAdvanced(agentVersion, minorVersionNumber)) {
      isManualFileDownloadAllowed = true;
    }
    return { isDisplayed: isManualFileDownloadAllowed };
  }
);
