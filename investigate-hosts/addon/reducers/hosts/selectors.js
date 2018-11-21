import reselect from 'reselect';
import { getSelectedAgentIds } from 'investigate-hosts/util/util';

const { createSelector } = reselect;

const SUPPORTED_SERVICES = [ 'broker', 'concentrator', 'decoder', 'log-decoder', 'archiver' ];

const HOST_LIST_PROPERTY_TABS = [
  {
    label: 'investigateHosts.tabs.hostDetails',
    name: 'HOST_DETAILS'
  },
  {
    label: 'investigateHosts.tabs.riskDetails',
    name: 'RISK'
  }
];

const _hostList = (state) => state.endpoint.machines.hostList || [];
const _selectedHostList = (state) => state.endpoint.machines.selectedHostList || [];
const _serviceList = (state) => state.endpoint.machines.listOfServices;
const _hostExportLinkId = (state) => state.endpoint.machines.hostExportLinkId;
const _hostDetailId = (state) => state.endpoint.detailsInput ? state.endpoint.detailsInput.agentId : null;
const _agentId = (state) => state.endpoint.detailsInput.agentId;
const _columnSort = (state) => state.endpoint.machines.hostColumnSort;
const _serverId = (state) => state.endpointQuery.serverId;
const _hostDetails = (state) => state.endpoint.overview.hostDetails || {};
const _servers = (state) => state.endpointServer.serviceData || [];
const _activeHostListPropertyTab = (state) => state.endpoint.machines.activeHostListPropertyTab || 'HOST_DETAILS';

const _allAreMigratedHosts = createSelector(
  _selectedHostList,
  (selectedHostList) => selectedHostList.every((host) => host && !host.managed)
);

const _hasMigratedHosts = createSelector(
  _selectedHostList,
  (selectedHostList) => selectedHostList.some((host) => host && !host.managed)
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
  [ _selectedHostList, _hostDetails ],
  (selectedHostList, hostDetails) => {
    if (hostDetails.id) {
      const { machine, id } = hostDetails;
      const { agentVersion } = machine;
      if (agentVersion && !agentVersion.startsWith('4.4')) {
        return [{ id, version: agentVersion }];
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
  [ _hostExportLinkId, _serverId ],
  (hostExportLinkId, serverId) => {
    if (hostExportLinkId) {
      if (serverId) {
        return `${location.origin}/rsa/endpoint/${serverId}/machine/property/download?id=${hostExportLinkId}`;
      }
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
  [hasEcatAgents, areSomeScanning, _hasMigratedHosts],
  (hasEcatAgents, areSomeScanning, hasMigratedHosts) => {
    const messages = [];
    if (areSomeScanning) {
      messages.push('investigateHosts.hosts.initiateScan.modal.infoMessage');
    }
    if (hasEcatAgents) {
      messages.push('investigateHosts.hosts.initiateScan.modal.ecatAgentMessage');
    }
    if (hasMigratedHosts) {
      messages.push('investigateHosts.hosts.initiateScan.modal.migratedHostMessage');
    }
    return messages;
  }
);

export const isScanStartButtonDisabled = createSelector(
  [tooManyHostsSelected, hostListForScanning, allAreEcatAgents, _allAreMigratedHosts],
  (tooManyHostsSelected, hostListForScanning, allAreEcatAgents, allAreMigratedHosts) => {
    return !hostListForScanning.length || tooManyHostsSelected || allAreEcatAgents || allAreMigratedHosts;
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

export const isExportButtonDisabled = createSelector(
  [_hostList, _servers, _serverId],
  (hostList, servers, serverId) => {
    const isEndpointBroker = servers.some((s) => s.id === serverId && s.name === 'endpoint-broker-server');
    return !hostList.length || isEndpointBroker;
  }
);

export const hostListPropertyTabs = createSelector(
  [_activeHostListPropertyTab],
  (activeHostListPropertyTab) => {
    return HOST_LIST_PROPERTY_TABS.map((tab) => ({ ...tab, selected: tab.name === activeHostListPropertyTab }));
  }
);