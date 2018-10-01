import reselect from 'reselect';
import securityConfig from './config';

const { createSelector } = reselect;

const _hostDetails = (state) => state.endpoint.overview.hostDetails || [];
const _snapShots = (state) => state.endpoint.detailsInput.snapShots;
const _exportJsonStatus = (state) => state.endpoint.overview.exportJSONStatus;
const _agentStatus = (state) => state.endpoint.machines.agentStatus;
const _downloadId = (state) => state.endpoint.overview.downloadId;
const _arrangeSecurityConfigsBy = (state) => state.endpoint.overview.arrangeSecurityConfigsBy;
const _policyDetails = (state) => state.endpoint.overview.policyDetails || {};
const _serverId = (state) => state.endpointQuery.serverId;

const _hostAgentStatus = createSelector(
  _hostDetails,
  (hostDetails) => {
    return hostDetails && hostDetails.agentStatus;
  }
);

export const machineOsType = createSelector(
  _hostDetails,
  (hostDetails) => {
    if (hostDetails && hostDetails.machine) {
      return hostDetails.machine.machineOsType;
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
  (hostDetails) => hostDetails && hostDetails.machine ? hostDetails.machine.machineName : ''
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
  [ _downloadId, _serverId ],
  (downloadId, serverId) => {
    if (downloadId) {
      if (serverId) {
        return `${location.origin}/rsa/endpoint/${serverId}/machine/download?id=${downloadId}`;
      }
      return `${location.origin}/rsa/endpoint/machine/download?id=${downloadId}`;
    }
  }
);

export const getNetworkInterfaces = createSelector(
  _hostDetails,
  ({ machine }) => {
    if (machine) {
      const networkInterfaces = machine.networkInterfaces || [];
      const validNetworkInterfaceList = networkInterfaces.filter((networkInterface) => networkInterface.ipv4 && ((networkInterface.ipv4.length === 1 && networkInterface.ipv4[0] !== '127.0.0.1') || networkInterface.ipv4.length > 1));
      const validIPList = validNetworkInterfaceList.map((networkInterface) => ({
        ...networkInterface,
        ipv4: networkInterface.ipv4 ? networkInterface.ipv4.filter((ip) => ip !== '127.0.0.1') : [],
        ipv6: networkInterface.ipv6 ? networkInterface.ipv6.filter((ip) => ip !== '::1') : []
      }));
      return validIPList.map(
        (ip) => ({ ipv6: ip.ipv6 || '', macAddress: ip.macAddress || '', ipv4: ip.ipv4 || '' })
      );
    } else {
      return [];
    }
  }
);

export const getLoggedInUsers = createSelector(
  _hostDetails,
  ({ machine }) => {
    if (machine) {
      return machine.users || [];
    } else {
      return [];
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

export const networkInterfacesCount = createSelector(
  getNetworkInterfaces,
  (networkInterfaces) => networkInterfaces.length
);

export const loggedInUsersCount = createSelector(
  getLoggedInUsers,
  (loggedInUsers) => loggedInUsers.length
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
    if (hostDetails && hostDetails.machine) {
      const { machine: { agentVersion } } = hostDetails;
      return agentVersion && agentVersion.startsWith('4.4');
    }
    return false;
  }
);

export const isMachineLinux = createSelector(
  machineOsType,
  (machineOsType) => machineOsType && (machineOsType.toLowerCase() === 'linux')
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
