import reselect from 'reselect';
import { getSelectedAgentIds } from 'investigate-hosts/util/util';
import { isBrokerView } from 'investigate-shared/selectors/endpoint-server/selectors';
import { isOSWindows, isModeAdvance, isAgentVersionAdvanced } from 'investigate-hosts/reducers/utils/mft-utils';
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
const _hostOverview = (state) => state.endpoint.overview.hostOverview || {};

const _allAreIdle = (state) => {
  const selectedHosts = state.endpoint.machines.selectedHostList || [];
  const idleHosts = selectedHosts.filter((item) => {
    return item ? item.scanStatus === 'idle' : false;
  });
  return idleHosts.length > 0;
};

const _allAreNotIdle = (state) => {
  const selectedHosts = state.endpoint.machines.selectedHostList || [];
  const activeHosts = selectedHosts.filter((item) => {
    return item ? item.scanStatus !== 'idle' : false;
  });
  return activeHosts.length > 0;
};

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
  [tooManyHostsSelected, allAreEcatAgents, allAreMigratedHosts, _allAreIdle],
  (tooManyHostsSelected, allAreEcatAgents, allAreMigratedHosts, allAreIdle) => {
    return tooManyHostsSelected || allAreEcatAgents || allAreMigratedHosts || !allAreIdle;
  }
);

export const isAgentMigrated = createSelector(
  [allAreMigratedHosts, isBrokerView],
  (allAreMigratedHosts, isBrokerView) => {
    return allAreMigratedHosts && (!isBrokerView);
  }
);

export const isScanStopButtonDisabled = createSelector(
  [tooManyHostsSelected, allAreEcatAgents, allAreMigratedHosts, _allAreNotIdle],
  (tooManyHostsSelected, allAreEcatAgents, allAreMigratedHosts, allAreNotIdle) => {
    return tooManyHostsSelected || allAreEcatAgents || allAreMigratedHosts || !allAreNotIdle;
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
  [noHostsSelected, tooManyHostsSelected, allAreEcatAgents, allAreMigratedHosts, _allAreIdle, _allAreNotIdle],
  (noHostsSelected, tooManyHostsSelected, allAreEcatAgents, allAreMigratedHosts, allAreIdle, allAreNotIdle) => {
    if (noHostsSelected) {
      return 'Select a host to perform this action.';
    } else if (tooManyHostsSelected) {
      return 'More than 100 hosts are selected.';
    } else if (allAreEcatAgents) {
      return '4.4 agent(s) selected.';
    } else if (allAreMigratedHosts) {
      return 'Selected hosts not managed by the current server.';
    } else if (allAreIdle || allAreNotIdle) {
      if (allAreIdle && !allAreNotIdle) {
        return 'Scan cannot be stopped as scan status is idle.';
      } else if (!allAreIdle && allAreNotIdle) {
        return 'Selected hosts are already being scanned.';
      }
      return '';
    }
  }
);

const _isMachineOSWindows = createSelector(
  [_selectedHostList],
  (selectedHostList) => {
    const { machineIdentity = { machineOsType: '' } } = selectedHostList.length ? selectedHostList[0] : { machineIdentity: { machineOsType: '' } };
    return isOSWindows(machineIdentity.machineOsType);
  });

const _agentMode = createSelector(
  [_selectedHostList],
  (selectedHostList) => {
    const { machineIdentity = { agentMode: '' } } = selectedHostList.length ? selectedHostList[0] : { machineIdentity: { agentMode: '' } };
    return isModeAdvance(machineIdentity.agentMode);
  });

const _agentVersion = createSelector(
  [_selectedHostList],
  (selectedHostList) => {
    const { version } = selectedHostList.length ? selectedHostList[0] : { version: '' };
    return isAgentVersionAdvanced(version);
  });

export const agentVersionSupported = createSelector(
  [_selectedHostList],
  (selectedHostList) => {
    const { version } = selectedHostList.length ? selectedHostList[0] : { version: '0.0' };
    const versionParts = version.split('.');
    return Number(versionParts[0]) > 10 && Number(versionParts[1]) > 3;
  });

export const mftDownloadButtonStatus = createSelector(
  [_isMachineOSWindows, _agentMode, _agentVersion],
  (isMachineOSWindows, agentMode, agentVersion) => {
    return { isDisplayed: (isMachineOSWindows && agentMode && agentVersion) };
  });
export const selectedHostDetails = createSelector(
  [_selectedHostList],
  (selectedHostList) => {
    if (selectedHostList.length > 0) {
      const [{ id, serviceId, agentStatus = {} }] = selectedHostList;
      const { isolationStatus } = agentStatus;
      const isIsolated = isolationStatus ? isolationStatus.isolated : '';
      return { id, serviceId, isIsolated };
    }
    return {};
  });

const _isolateStatus = createSelector(
  [_selectedHostList, _hostOverview],
  (selectedHostList, hostOverview) => {
    let status = {};
    if (hostOverview.agentStatus) {
      status = hostOverview.agentStatus.isolationStatus;
    } else if (selectedHostList.length > 0) {
      const [{ agentStatus: { isolationStatus } }] = selectedHostList;
      status = isolationStatus || {};
    }
    return status;
  });
export const isolationComment = createSelector(
  [_isolateStatus],
  (isolateStatus) => {
    return isolateStatus.comment;
  });
export const excludedIps = createSelector(
  [_isolateStatus],
  (isolateStatus) => {
    return isolateStatus.excludedIps;
  });
export const processedHostList = createSelector(
  [ _hostList ],
  (machines) => {
    return machines.map((machine) => {
      let hasScanStatus = false;
      let canStartScan = false;
      let isMFTEnabled = false;
      let isAgentRoaming = false;
      const { machineOsType, agentMode, agentVersion } = machine.machineIdentity;
      if (machine.agentStatus) {
        const { scanStatus, lastSeen } = machine.agentStatus;
        hasScanStatus = true;
        canStartScan = scanStatus === 'idle' || scanStatus === 'cancelPending';
        isAgentRoaming = (lastSeen === 'RelayServer');
      }
      if (isOSWindows(machineOsType) && isModeAdvance(agentMode) && isAgentVersionAdvanced(agentVersion)) {
        isMFTEnabled = true;
      }
      return {
        ...machine,
        canStartScan,
        hasScanStatus,
        isMFTEnabled,
        isAgentRoaming
      };
    });
  }
);