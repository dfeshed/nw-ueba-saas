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
    componentClass: 'host-detail/overview',
    subTabName: null
  },
  {
    label: 'investigateHosts.tabs.process',
    name: 'PROCESS',
    componentClass: 'host-detail/process',
    subTabName: null
  },
  {
    label: 'investigateHosts.tabs.autoruns',
    name: 'AUTORUNS',
    componentClass: 'host-detail/autoruns',
    subTabName: 'AUTORUNS'
  },
  {
    label: 'investigateHosts.tabs.files',
    name: 'FILES',
    componentClass: 'host-detail/files',
    subTabName: null
  },
  {
    label: 'investigateHosts.tabs.drivers',
    name: 'DRIVERS',
    componentClass: 'host-detail/drivers',
    subTabName: null
  },
  {
    label: 'investigateHosts.tabs.libraries',
    name: 'LIBRARIES',
    componentClass: 'host-detail/libraries',
    subTabName: null
  },
  {
    label: 'investigateHosts.tabs.downloads',
    name: 'DOWNLOADS',
    componentClass: 'host-detail/downloads',
    subTabName: null
  },
  {
    label: 'investigateHosts.tabs.systemInformation',
    name: 'SYSTEM',
    componentClass: 'host-detail/system-information',
    subTabName: null
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
const _listAllFiles = (state) => state.endpoint.visuals.listAllFiles;
const _isProcessDetailsView = (state) => state.endpoint.visuals.isProcessDetailsView;

export const riskState = createSelector(
  [_riskState],
  (riskState) => {
    return riskState;
  }
);

export const isSnapShotDisable = createSelector(
  _activeHostDetailTab, _listAllFiles, _isProcessDetailsView,
  (activeHostDetailTab, listAllFiles, isProcessDetailsView) => {
    return ((activeHostDetailTab === 'FILES' && listAllFiles) || isProcessDetailsView);
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
      /* Tabs that are specific to Windows; Anomalies and Downloads*/
      const windowsSpecificTabs = [{
        label: 'investigateHosts.tabs.anomalies',
        name: 'ANOMALIES',
        componentClass: 'host-detail/anomalies',
        subTabName: 'IMAGEHOOKS'
      }];
      HOST_DETAILS_TABS_CLONE.splice(6, 0, ...windowsSpecificTabs);
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

export const isActiveTabDownloads = createSelector(
  [_activeHostDetailTab],
  (activeHostDetailTab) => activeHostDetailTab === 'DOWNLOADS'
);
