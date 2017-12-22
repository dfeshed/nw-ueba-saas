import reselect from 'reselect';
const { createSelector } = reselect;


const SUPPORTED_SERVICES = [ 'broker', 'concentrator', 'decoder', 'log-decoder', 'archiver' ];

const _hostList = (state) => state.endpoint.machines.hostList || [];
const _selectedHostList = (state) => state.endpoint.machines.selectedHostList || [];
const _serviceList = (state) => state.endpoint.machines.listOfServices;
const _hostExportLinkId = (state) => state.endpoint.machines.hostExportLinkId;
const _hostDetailId = (state) => state.endpoint.detailsInput ? state.endpoint.detailsInput.agentId : null;
const _agentId = (state) => state.endpoint.detailsInput.agentId;

export const serviceList = createSelector(
  _serviceList,
  (serviceList) => {
    if (serviceList) {
      return serviceList.filter((service) => SUPPORTED_SERVICES.includes(service.name));
    }
    return null;
  }
 );

export const areSomeScanning = createSelector(
  [ _hostList, _selectedHostList, _hostDetailId ],
  (machines, selectedHostList, hostDetailId) => {
    if (hostDetailId) {
      return false;
    }
    if (selectedHostList.length) {
      return machines.some((item) => {
        return selectedHostList.some((host) => host.id === item.id) && (item.agentStatus && item.agentStatus.scanStatus !== 'idle');
      });
    }
    return false;
  }
);

export const hostExportLink = createSelector(
  _hostExportLinkId,
  (hostExportLinkId) => {
    if (hostExportLinkId) {
      return `${location.origin}/rsa/endpoint/machine/property/download?id=${hostExportLinkId}`;
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
        selected: selectedHostList.some((host) => host.id === machine.id)
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
      return [{ id: hostDetailId }];
    }
    return selectedHostList;
  }
);

export const hasMachineId = createSelector(
  _agentId,
  (agentId) => {
    return !!agentId;
  },
);

export const tooManyHostsSelected = createSelector(
  _selectedHostList,
  (selectedHostList) => {
    return selectedHostList.length > 100; // Maximum selected hosts allowed = 100
  },
);

export const warningClass = createSelector(
  tooManyHostsSelected,
  (tooManyHostsSelected) => {
    return tooManyHostsSelected ? 'danger' : 'standard';
  },
);

export const allAreEcatAgents = createSelector(
  [ _selectedHostList, noHostsSelected ],
  (selectedHostList, noHost) => noHost || selectedHostList.every((host) => host.version.startsWith('4.4'))
);

export const areAnyEcatAgents = createSelector(
  _selectedHostList,
  (selectedHostList) => selectedHostList.some((host) => host.version.startsWith('4.4'))
);