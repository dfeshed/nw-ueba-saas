import reselect from 'reselect';
import { getSelectedAgentIds } from 'investigate-hosts/util/util';
import { isBrokerView } from 'investigate-shared/selectors/endpoint-server/selectors';
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
const _servers = (state) => state.endpointServer.serviceData || [];
const _activeHostListPropertyTab = (state) => state.endpoint.machines.activeHostListPropertyTab || 'HOST_DETAILS';
const _hostTotal = (state) => state.endpoint.machines.totalItems || 0;
const _expressionList = (state) => state.endpoint.filter.expressionList || [];
const _hasNext = (state) => state.endpoint.machines.hasNext;
const _focusedHost = (state) => state.endpoint.machines.focusedHost;

export const allAreMigratedHosts = createSelector(
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

export const areSomeScanning = createSelector(
  _hostList, _selectedHostList, _hostDetailId,
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
  _selectedHostList,
  (selectedHostList) => selectedHostList.every((host) => host && host.version && host.version.startsWith('4.4'))
);

export const hasEcatAgents = createSelector(
  _selectedHostList,
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
  [tooManyHostsSelected, allAreEcatAgents, allAreMigratedHosts],
  (tooManyHostsSelected, allAreEcatAgents, allAreMigratedHosts) => {
    return tooManyHostsSelected || allAreEcatAgents || allAreMigratedHosts;
  }
);

export const extractAgentIds = createSelector(
  _selectedHostList,
  (selectedHostList) => {
    return getSelectedAgentIds(selectedHostList);
  }
);

export const scanCount = createSelector(
  _selectedHostList,
  (selectedHostList) => selectedHostList.length
);

export const isExportButtonDisabled = createSelector(
  [_hostList, _servers, _serverId],
  (hostList, servers, serverId) => {
    const isEndpointBroker = servers.some((s) => s.id === serverId && s.name === 'endpoint-broker-server');
    const disabled = !hostList.length || isEndpointBroker;
    return {
      isEndpointBroker,
      disabled
    };
  }
);

export const hostListPropertyTabs = createSelector(
  [_activeHostListPropertyTab],
  (activeHostListPropertyTab) => {
    return HOST_LIST_PROPERTY_TABS.map((tab) => ({ ...tab, selected: tab.name === activeHostListPropertyTab }));
  }
);

export const hostTotalLabel = createSelector(
  [_hostTotal, _expressionList, _hasNext, isBrokerView],
  (total, expressionList, hasNext, isBrokerView) => {
    if (total >= 1000) {
      if (isBrokerView || (expressionList && expressionList.length && hasNext)) {
        return '1000+';
      }
    }
    return `${total}`;
  }
);
export const nextLoadCount = createSelector(
  [_hostList],
  (hostList) => {
    const ONE_PAGE_MAX_LENGTH = 100;
    const loadCount = hostList.length >= ONE_PAGE_MAX_LENGTH ? ONE_PAGE_MAX_LENGTH : hostList.length;
    return loadCount;
  }
);

export const isInsightsAgent = createSelector(
  [_focusedHost],
  (focusedHost) => {
    if (focusedHost) {
      return focusedHost.machineIdentity ? focusedHost.machineIdentity.agentMode === 'insights' : false;
    }
    return false;
  }
);

export const actionsDisableMessage = createSelector(
  [noHostsSelected, tooManyHostsSelected, allAreEcatAgents, allAreMigratedHosts],
  (noHostsSelected, tooManyHostsSelected, allAreEcatAgents, allAreMigratedHosts) => {
    if (noHostsSelected) {
      return 'Action disabled - No host is selected.';
    } else if (tooManyHostsSelected) {
      return 'Action disabled - More than 100 hosts are selected.';
    } else if (allAreEcatAgents) {
      return 'Action disabled - 4.4 agent(s) selected.';
    } else if (allAreMigratedHosts) {
      return 'Action disabled - Selected host(s) not managed by the current server';
    }
  }
);

const _isOSWindows = function(value = '') {
  return value.toLowerCase() === 'windows';
};

const _isModeAdvance = function(value = '') {
  return value.toLowerCase() === 'advanced';
};

const _isAgentVersionAdvanced = function(version = '') {
  const agentVersion = version.trim().split('.');
  return (agentVersion.length >= 2) ? (agentVersion[0] == 11 && agentVersion[1] >= 4) || (agentVersion[0] > 11) : false;
};

const _isMachineOSWindows = createSelector(
  [_selectedHostList],
  (selectedHostList) => {
    const { machineIdentity = { machineOsType: '' } } = selectedHostList.length ? selectedHostList[0] : { machineIdentity: { machineOsType: '' } };
    return _isOSWindows(machineIdentity.machineOsType);
  });

const _agentMode = createSelector(
  [_selectedHostList],
  (selectedHostList) => {
    const { machineIdentity = { agentMode: '' } } = selectedHostList.length ? selectedHostList[0] : { machineIdentity: { agentMode: '' } };
    return _isModeAdvance(machineIdentity.agentMode);
  });

const _agentVersion = createSelector(
  [_selectedHostList],
  (selectedHostList) => {
    const { version } = selectedHostList.length ? selectedHostList[0] : { version: '' };
    return _isAgentVersionAdvanced(version);
  });

export const mftDownloadButtonStatus = createSelector(
  [_isMachineOSWindows, _agentMode, _agentVersion],
  (isMachineOSWindows, agentMode, agentVersion) => ({ isDisplayed: (isMachineOSWindows && agentMode && agentVersion) }));

export const processedHostList = createSelector(
  [ _hostList ],
  (machines) => {
    return machines.map((machine) => {
      let hasScanStatus = false;
      let canStartScan = false;
      let isMFTEnabled = false;
      const { machineOsType, agentMode, agentVersion } = machine.machineIdentity;
      if (machine.agentStatus) {
        const { scanStatus } = machine.agentStatus;
        hasScanStatus = true;
        canStartScan = scanStatus === 'idle' || scanStatus === 'cancelPending';
      }
      if (_isOSWindows(machineOsType) && _isModeAdvance(agentMode) && _isAgentVersionAdvanced(agentVersion)) {
        isMFTEnabled = true;
      }
      return {
        ...machine,
        canStartScan,
        hasScanStatus,
        isMFTEnabled
      };
    });
  }
);