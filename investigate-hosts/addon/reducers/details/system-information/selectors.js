import reselect from 'reselect';
const { createSelector } = reselect;
import _ from 'lodash';
import COLUMNS from './columns';

const _hostDetails = (state) => state.endpoint.overview.hostDetails || [];

const _selectedTab = (state) => {
  const { endpoint } = state;
  if (endpoint && endpoint.visuals) {
    return state.endpoint.visuals.activeSystemInformationTab || 'HOST_ENTRIES';
  }
  return 'HOST_ENTRIES';
};

const _machineData = createSelector(
  _hostDetails,
  (hostDetails) => hostDetails.machine
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

export const hostFileEntries = createSelector(
  [ _machineData ],
  (_machineData) => {
    if (_machineData) {
      return _machineData.hostFileEntries;
    }
    return [];
  }
);

export const _mountedPaths = createSelector(
  [ _machineData ],
  (_machineData) => {
    if (_machineData) {
      return _machineData.mountedPaths;
    }
    return [];
  }
);

export const _networkShares = createSelector(
  [ _machineData ],
  (_machineData) => {
    if (_machineData) {
      return _machineData.networkShares;
    }
    return [];
  }
);

export const bashHistories = createSelector(
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

export const _windowsPatches = createSelector(
  [ _machineData ],
  (_machineData) => {
    if (_machineData && _machineData.systemPatches) {
      return _machineData.systemPatches.map((patch) => ({ 'windowsPatch': patch }));
    }
    return [];
  }
);

export const _securityProducts = createSelector(
  [ _machineData ],
  (_machineData) => {
    if (_machineData) {
      return _machineData.securityProducts;
    }
    return [];
  }
);

export const selectedSystemInformationData = createSelector(
  [ _securityProducts, _windowsPatches, bashHistories, _networkShares, hostFileEntries, _mountedPaths, _selectedTab ],
  (securityProducts, windowsPatches, bashHistories, networkShares, hostFileEntries, mountedPaths, selectedTab) => {
    const { columns } = COLUMNS[selectedTab];
    let data = null;
    switch (selectedTab) {
      case 'SECURITY_PRODUCTS':
        data = securityProducts;
        break;
      case 'WINDOWS_PATCHES':
        data = windowsPatches;
        break;
      case 'BASH_HISTORY':
        data = bashHistories;
        break;
      case 'NETWORK_SHARES':
        data = networkShares;
        break;
      case 'HOST_ENTRIES':
        data = hostFileEntries;
        break;
      case 'MOUNTED_PATH':
        data = mountedPaths;
        break;
    }
    return {
      data,
      columns
    };
  }
);