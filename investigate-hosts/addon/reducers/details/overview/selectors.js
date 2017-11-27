import reselect from 'reselect';
const { createSelector } = reselect;

const _hostDetails = (state) => state.endpoint.overview.hostDetails || [];
const _snapShots = (state) => state.endpoint.detailsInput.snapShots;
const _exportJsonStatus = (state) => state.endpoint.overview.exportJSONStatus;
const _agentStatus = (state) => state.endpoint.machines.agentStatus;
const _downloadId = (state) => state.endpoint.overview.downloadId;

const _hostAgentStatus = createSelector(
  _hostDetails,
  (hostDetails) => {
    return hostDetails && hostDetails.agentStatus;
  }
);

export const processHost = createSelector(
  _hostDetails,
  (hostDetails) => {
    if (hostDetails) {
      const { machine } = hostDetails;
      if (machine) {
        const { users, securityConfigurations } = machine;
        const loggedInUsers = users || [];
        const securityConfig = securityConfigurations || [];
        return {
          ...machine,
          loggedInUsers,
          securityConfig
        };
      }
    } else {
      return {};
    }
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
  [ _downloadId ],
  (downloadId) => {
    if (downloadId) {
      return `${location.origin}/endpoint/files/download/${downloadId}`;
    }
  }
);

export const getNetworkInterfaces = createSelector(
  _hostDetails,
  (hostDetails) => {
    if (hostDetails) {
      const { machine } = hostDetails;
      if (machine) {
        const networkInterfaces = machine.networkInterfaces || [];
        const validNetworkInterfaceList = networkInterfaces.filter((networkInterface) => (networkInterface.ipv4.length === 1 && networkInterface.ipv4[0] !== '127.0.0.1') || networkInterface.ipv4.length > 1);
        const validIPList = validNetworkInterfaceList.map((networkInterface) => ({
          ...networkInterface,
          ipv4: networkInterface.ipv4.filter((ip) => ip !== '127.0.0.1'),
          ipv6: networkInterface.ipv6.filter((ip) => ip !== '::1')
        }));
        return validIPList.map(
          (ip) => `${ip.ipv4 || ''} / ${ip.ipv6 || ''} | MAC Address: ${ip.macAddress || ''}`
        );
      }
    } else {
      return [];
    }
  }
);
