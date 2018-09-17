import { createSelector } from 'reselect';

const FILE_DETAIL_TABS = [
  {
    label: 'investigateFiles.tabs.overview',
    name: 'OVERVIEW',
    componentClass: 'file-details/overview'
  },
  {
    label: 'investigateFiles.tabs.analysis',
    name: 'ANALYSIS'
  }];

const _activeFileDetailTab = (state) => state.files.visuals.activeFileDetailTab;

export const getFileDetailTabs = createSelector(
  [_activeFileDetailTab],
  (activeFileDetailTab) => {
    return FILE_DETAIL_TABS.map((tab) => ({ ...tab, selected: tab.name === activeFileDetailTab }));
  }
);

export const selectedTabComponent = createSelector(
  [getFileDetailTabs],
  (listOfFileTabs) => {
    const selectedTab = listOfFileTabs.findBy('selected', true);
    if (selectedTab) {
      return selectedTab.componentClass;
    }
    return 'file-details/overview'; // Default selected tab
  }
);