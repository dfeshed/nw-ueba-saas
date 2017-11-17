import reselect from 'reselect';
const { createSelector } = reselect;
import _ from 'lodash';

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
        const { networkInterfaces, users, securityConfigurations } = machine;
        const ipList = networkInterfaces ? networkInterfaces.filter((ele) => (ele.ipv4 || ele.ipv6) && ele.ipv4 !== '127.0.0.1') : [];
        const ipAddresses = ipList.map((ip) => {
          return `${ip.ipv4 || ''} / ${ip.ipv6 || ''} | MAC Address: ${ip.macAddress || ''}`;
        });
        const loggedInUsers = users || [];
        const securityConfig = securityConfigurations || [];
        return {
          ...machine,
          ipAddresses,
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

const _machineData = createSelector(
  _hostDetails,
  (hostDetails) => hostDetails.machine
);

export const getHostFileEntries = createSelector(
  [ _machineData ],
  (_machineData) => {
    if (_machineData) {
      return _machineData.hostFileEntries;
    }
    return [];
  }
);

export const getMountedPaths = createSelector(
  [ _machineData ],
  (_machineData) => {
    if (_machineData) {
      return _machineData.mountedPaths;
    }
    return [];
  }
);

export const getNetworkShares = createSelector(
  [ _machineData ],
  (_machineData) => {
    if (_machineData) {
      return _machineData.networkShares;
    }
    return [];
  }
);

export const getBashHistories = createSelector(
  [ _machineData ],
  (_machineData) => {
    if (_machineData) {
      const { bashHistory } = _machineData;
      if (bashHistory) {
        const history = bashHistory.map((item) => {
          const { username: userName, commands } = item;
          const modified = commands.map((command) => {
            return {
              userName,
              command
            };
          });
          return modified;
        });
        return _.flatten(history).reverse();
      }
    }
    return [];
  }
);

export const downloadLink = createSelector(
  [ _downloadId ],
  (downloadId) => {
    if (downloadId) {
      return `${location.origin}/endpoint/files/download/${downloadId}`;
    }
  }
);
