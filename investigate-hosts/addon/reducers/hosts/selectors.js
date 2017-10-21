import reselect from 'reselect';
const { createSelector } = reselect;

const _hostList = (state) => state.endpoint.machines.hostList || [];
const _selectedHostList = (state) => state.endpoint.machines.selectedHostList || [];
const _hostExportLinkId = (state) => state.endpoint.machines.hostExportLinkId;
const _hostDetailId = (state) => state.detailsInput ? state.detailsInput.agentId : null;
const _agentId = (state) => state.endpoint.detailsInput.agentId;

export const areSomeScanning = createSelector(
  [ _hostList, _selectedHostList, _hostDetailId ],
  (machines, selectedHostList, hostDetailId) => {
    if (hostDetailId) {
      return false;
    }
    if (selectedHostList.length) {
      return machines.some((item) => {
        return selectedHostList.includes(item.id) && (item.agentStatus && item.agentStatus.scanStatus !== 'idle');
      });
    }
    return false;
  }
);

export const hostExportLink = createSelector(
  _hostExportLinkId,
  (hostExportLinkId) => {
    if (hostExportLinkId) {
      return `${location.origin}/endpoint/machine/download/${hostExportLinkId}`;
    }
    return null;
  }
);

export const processedHostList = createSelector(
  [ _hostList, _selectedHostList ],
  (machines, selectedHostList) => {
    return machines.map((machine) => {
      let hasScanStatus = false;
      let canStartScan = false;
      if (machine.agentStatus) {
        const { scanStatus } = machine.agentStatus;
        hasScanStatus = true;
        canStartScan = scanStatus === 'idle' || scanStatus === 'cancelPending';
      }
      return {
        ...machine,
        canStartScan,
        hasScanStatus,
        selected: selectedHostList.includes(machine.id)
      };
    });
  }
);

export const isAllHostSelected = createSelector(
  _selectedHostList, _hostList,
  (selectedHostList, hostList) => {
    if (selectedHostList && selectedHostList.length) {
      return hostList.length === selectedHostList.length;
    }
    return false;
  }
);

export const noHostsSelected = createSelector(
  _selectedHostList,
  (selectedHostList) => {
    return !selectedHostList.length;
  }
);

export const hostListForScanning = createSelector(
  [_selectedHostList, _hostDetailId],
  (selectedHostList, hostDetailId) => {
    if (hostDetailId) {
      return [hostDetailId];
    }
    return selectedHostList;
  }
);

export const hasMachineId = createSelector(
  _agentId,
  (agentId) => {
    return !!agentId;
  }
);
