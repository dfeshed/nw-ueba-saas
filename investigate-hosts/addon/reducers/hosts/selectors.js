import reselect from 'reselect';
import { isValidExpression } from 'investigate-hosts/reducers/filters/selectors';
import { getSelectedAgentIds } from 'investigate-hosts/util/util';

const { createSelector } = reselect;

const SUPPORTED_SERVICES = [ 'broker', 'concentrator', 'decoder', 'log-decoder', 'archiver' ];

const _hostList = (state) => state.endpoint.machines.hostList || [];
const _selectedHostList = (state) => state.endpoint.machines.selectedHostList || [];
const _serviceList = (state) => state.endpoint.machines.listOfServices;
const _hostExportLinkId = (state) => state.endpoint.machines.hostExportLinkId;
const _hostDetailId = (state) => state.endpoint.detailsInput ? state.endpoint.detailsInput.agentId : null;
const _agentId = (state) => state.endpoint.detailsInput.agentId;
const _totalItems = (state) => state.endpoint.machines.totalItems;
const _columnSort = (state) => state.endpoint.machines.hostColumnSort;

const _agentVersion = createSelector(
  [ _hostDetailId, _hostList ],
  (hostDetailId, hostList) => {
    if (hostDetailId) {
      const host = hostList.find((host) => host.id === hostDetailId);
      if (host && host.machine) {
        return host.machine.agentVersion;
      }
    }
  }
);

export const serviceList = createSelector(
  _serviceList,
  (serviceList) => {
    if (serviceList) {
      return serviceList.filter((service) => SUPPORTED_SERVICES.includes(service.name));
    }
    return null;
  }
 );

export const hostListForScanning = createSelector(
  [ _selectedHostList, _hostDetailId, _agentVersion ],
  (selectedHostList, hostDetailId, agentVersion) => {
    if (hostDetailId) {
      if (agentVersion && !agentVersion.startsWith('4.4')) {
        return [{ id: hostDetailId, version: agentVersion }];
      } else {
        return [];
      }
    }
    return selectedHostList;
  }
);

export const areSomeScanning = createSelector(
  _hostList, hostListForScanning, _hostDetailId,
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
  hostListForScanning,
  (selectedHostList) => selectedHostList.every((host) => host && host.version && host.version.startsWith('4.4'))
);

export const hasEcatAgents = createSelector(
  hostListForScanning,
  (selectedHostList) => selectedHostList.some((host) => host && host.version && host.version.startsWith('4.4'))
);

export const hostCountForDisplay = createSelector(
  [ _totalItems, isValidExpression],
  (totalItems, isValidExpression) => {
    // For performance reasons api returns 1000 as totalItems when filter is applied, even if result is more than 1000
    // Make sure we append '+' to indicate user that more machines are present
    if (isValidExpression && totalItems >= 1000) {
      return `${totalItems}+`;
    }
    return `${totalItems}`;
  }
);

export const sortField = createSelector(
  _columnSort,
  (_columnSort) => {
    if (_columnSort.length) {
      return _columnSort[0].key;
    }
  }
);
export const isSortDescending = createSelector(
  _columnSort,
  (_columnSort) => {
    if (_columnSort.length) {
      return _columnSort[0].descending;
    }
  }
);

export const warningMessages = createSelector(
  [hasEcatAgents, areSomeScanning],
  (hasEcatAgents, areSomeScanning) => {
    const messages = [];
    if (areSomeScanning) {
      messages.push('investigateHosts.hosts.initiateScan.modal.infoMessage');
    }
    if (hasEcatAgents) {
      messages.push('investigateHosts.hosts.initiateScan.modal.ecatAgentMessage');
    }
    return messages;
  }
);

export const isScanStartButtonDisabled = createSelector(
  [tooManyHostsSelected, hostListForScanning, allAreEcatAgents],
  (tooManyHostsSelected, hostListForScanning, allAreEcatAgents) => {
    return !hostListForScanning.length || tooManyHostsSelected || allAreEcatAgents;
  }
);

export const extractAgentIds = createSelector(
  hostListForScanning,
  (hostListForScanning) => {
    return getSelectedAgentIds(hostListForScanning);
  }
);

export const scanCount = createSelector(
  hostListForScanning,
  (hostListForScanning) => hostListForScanning.length
);
