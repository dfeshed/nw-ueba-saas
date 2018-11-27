import reselect from 'reselect';
import { isMachineWindows } from 'investigate-hosts/reducers/details/overview/selectors';

const AUTORUN_TABS = [
  {
    label: 'investigateHosts.tabs.autoruns',
    name: 'AUTORUNS',
    componentClass: 'host-detail/autoruns/autoruns'
  },
  {
    label: 'investigateHosts.tabs.services',
    name: 'SERVICES',
    componentClass: 'host-detail/autoruns/services'
  },
  {
    label: 'investigateHosts.tabs.tasks',
    name: 'TASKS',
    componentClass: 'host-detail/autoruns/tasks'
  }
];

const ANOMALIES_TABS = [
  {
    label: 'investigateHosts.tabs.hooks',
    name: 'IMAGEHOOKS',
    componentClass: 'host-detail/anomalies/hooks'
  },
  {
    label: 'investigateHosts.tabs.kernelHooks',
    name: 'KERNELHOOKS',
    componentClass: 'host-detail/anomalies/kernel-hooks'
  },
  {
    label: 'investigateHosts.tabs.threads',
    name: 'THREADS',
    componentClass: 'host-detail/anomalies/threads'
  },
  {
    label: 'investigateHosts.tabs.registryDiscrepancies',
    name: 'REGISTRYDISCREPANCY',
    componentClass: 'host-detail/anomalies/registry-discrepancies'
  }
];

const HOST_DETAILS_TABS = [
  {
    label: 'investigateHosts.tabs.overview',
    name: 'OVERVIEW',
    componentClass: 'host-detail/overview'
  },
  {
    label: 'investigateHosts.tabs.process',
    name: 'PROCESS',
    componentClass: 'host-detail/process'
  },
  {
    label: 'investigateHosts.tabs.autoruns',
    name: 'AUTORUNS',
    componentClass: 'host-detail/autoruns'
  },
  {
    label: 'investigateHosts.tabs.files',
    name: 'FILES',
    componentClass: 'host-detail/files'
  },
  {
    label: 'investigateHosts.tabs.drivers',
    name: 'DRIVERS',
    componentClass: 'host-detail/drivers'
  },
  {
    label: 'investigateHosts.tabs.libraries',
    name: 'LIBRARIES',
    componentClass: 'host-detail/libraries'
  },
  {
    label: 'investigateHosts.tabs.systemInformation',
    name: 'SYSTEM',
    componentClass: 'host-detail/system-information'
  }
];

const PROPERTY_PANEL_TABS = [
  {
    label: 'investigateHosts.tabs.hostDetails',
    name: 'HOST_DETAILS'
  },
  {
    label: 'investigateHosts.tabs.policyDetails',
    name: 'POLICIES'
  }
];

const { createSelector } = reselect;
const _activeHostDetailTab = (state) => state.endpoint.visuals.activeHostDetailTab || 'OVERVIEW';
const _activeAutorunTab = (state) => state.endpoint.visuals.activeAutorunTab || 'AUTORUNS';
const _activeAnomaliesTab = (state) => state.endpoint.visuals.activeAnomaliesTab || 'IMAGEHOOKS';
const _agentId = (state) => state.endpoint.detailsInput.agentId;
const _isWindowsFlag = (state) => isMachineWindows(state);
const _riskState = (state) => state.endpoint.risk || {};
const _activePropertyPanelTab = (state) => state.endpoint.visuals.activePropertyPanelTab || 'HOST_DETAILS';

export const riskState = createSelector(
  [_riskState],
  (riskState) => {
    return riskState;
  }
);

export const isOnOverviewTab = createSelector(
  _activeHostDetailTab,
  (activeHostDetailTab) => {
    return activeHostDetailTab === 'OVERVIEW';
  }
);

export const hasMachineId = createSelector(
  _agentId,
  (agentId) => {
    return !!agentId;
  },
);


export const getAutorunTabs = createSelector(
  [_activeAutorunTab],
  (activeAutorunTab) => {
    return AUTORUN_TABS.map((tab) => ({ ...tab, selected: tab.name === activeAutorunTab }));
  }
);

export const selectedAutorunTab = createSelector(
  [_activeAutorunTab],
  (activeAutorunTab) => {
    return AUTORUN_TABS.find((tab) => tab.name === activeAutorunTab);
  }
);

export const getAnomaliesTabs = createSelector(
  [_activeAnomaliesTab],
  (activeAnomaliesTab) => {
    return ANOMALIES_TABS.map((tab) => ({ ...tab, selected: tab.name === activeAnomaliesTab }));
  }
);

export const selectedAnomaliesTab = createSelector(
  [_activeAnomaliesTab],
  (activeAnomaliesTab) => {
    return ANOMALIES_TABS.find((tab) => tab.name === activeAnomaliesTab);
  }
);

export const getHostDetailTabs = createSelector(
  [_activeHostDetailTab, _isWindowsFlag],
  (activeHostDetailTab, isWindowsFlag) => {
    const HOST_DETAILS_TABS_CLONE = [...HOST_DETAILS_TABS];

    if (isWindowsFlag) {
      const anomaliesTab = {
        label: 'investigateHosts.tabs.anomalies',
        name: 'ANOMALIES',
        componentClass: 'host-detail/anomalies'
      };
      HOST_DETAILS_TABS_CLONE.splice(6, 0, anomaliesTab);
    }
    return HOST_DETAILS_TABS_CLONE.map((tab) => ({ ...tab, selected: tab.name === activeHostDetailTab }));
  }
);

export const selectedTabComponent = createSelector(
  [getHostDetailTabs],
  (listOfHostTabs) => {
    const selectedTab = listOfHostTabs.findBy('selected', true);
    if (selectedTab) {
      return selectedTab.componentClass;
    }
    return 'host-detail/overview'; // Default selected tab
  }
);

export const getPropertyPanelTabs = createSelector(
  [_activePropertyPanelTab],
  (activePropertyPanelTab) => {
    return PROPERTY_PANEL_TABS.map((tab) => ({ ...tab, selected: tab.name === activePropertyPanelTab }));
  }
);