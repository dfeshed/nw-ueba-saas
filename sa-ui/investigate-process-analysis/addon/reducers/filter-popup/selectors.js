import reselect from 'reselect';
const { createSelector } = reselect;

const EVENT_TABS = [
  {
    label: 'investigateProcessAnalysis.tabs.all',
    name: 'all'
  },
  {
    name: 'network',
    icon: 'rsa-icon-network'
  },
  {
    name: 'file',
    icon: 'rsa-icon-common-file-empty'
  },
  {
    name: 'registry',
    icon: 'rsa-icon-cell-border-bottom'
  }
];

const _activeFilterTab = (state) => state.processAnalysis.filterPopup.activeFilterTab;

export const getFilterTabs = createSelector(
  [_activeFilterTab],
  (activeFilterTab) => {
    return EVENT_TABS.map((tab) => ({
      ...tab,
      selected: tab.name === activeFilterTab
    }));
  }
);