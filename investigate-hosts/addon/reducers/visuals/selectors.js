import reselect from 'reselect';

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


const { createSelector } = reselect;
const _activeHostDetailTab = (state) => state.endpoint.visuals.activeHostDetailTab || 'OVERVIEW';
const _activeAutorunTab = (state) => state.endpoint.visuals.activeAutorunTab || 'AUTORUNS';

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

export const getHostDetailTabs = createSelector(
  [_activeHostDetailTab],
  (activeHostDetailTab) => {
    return HOST_DETAILS_TABS.map((tab) => ({ ...tab, selected: tab.name === activeHostDetailTab }));
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
