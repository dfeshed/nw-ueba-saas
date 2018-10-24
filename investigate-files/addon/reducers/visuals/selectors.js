import { createSelector } from 'reselect';

const DATASOURCE_TABS = [
  {
    label: 'investigateFiles.tabs.riskProperties',
    name: 'RISK_PROPERTIES'
  },
  {
    label: 'investigateFiles.tabs.fileDetails',
    name: 'FILE_DETAILS'
  },
  {
    label: 'investigateFiles.tabs.hosts',
    name: 'HOSTS'
  }
];

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
const _activeDataSourceTab = (state) => state.files.fileList.activeDataSourceTab || 'RISK_PROPERTIES';
const _riskState = (state) => state.files.risk || {};

export const riskState = createSelector(
    [_riskState],
    (riskState) => {
      return riskState;
    }
);

export const getFileDetailTabs = createSelector(
  [_activeFileDetailTab],
  (activeFileDetailTab) => {
    return FILE_DETAIL_TABS.map((tab) => ({ ...tab, selected: tab.name === activeFileDetailTab }));
  }
);

export const getDataSourceTab = createSelector(
    [_activeDataSourceTab],
    (activeDataSourceTab) => {
      return DATASOURCE_TABS.map((tab) => ({ ...tab, selected: tab.name === activeDataSourceTab }));
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