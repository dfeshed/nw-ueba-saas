import reselect from 'reselect';
const { createSelector } = reselect;
import _ from 'lodash';

const _hostDetails = (state) => state.endpoint.overview.hostDetails || [];

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

export const getWindowsPatches = createSelector(
  [ _machineData ],
  (_machineData) => {
    if (_machineData && _machineData.systemPatches) {
      return _machineData.systemPatches.map((patch) => ({ 'windowsPatch': patch }));
    }
    return [];
  }
);

export const getSecurityProducts = createSelector(
  [ _machineData ],
  (_machineData) => {
    if (_machineData) {
      return _machineData.securityProducts;
    }
    return [];
  }
);