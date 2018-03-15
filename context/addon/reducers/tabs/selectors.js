import { createSelector } from 'reselect';
import { getLookupData } from 'context/reducers/context/selectors';
import { isDataSourceEnabled } from 'context/util/context-data-modifier';

const _getToolTipText = (lookupData, { dataSourceType, isConfigured }) => {
  if (!isConfigured) {
    return 'context.notConfigured';
  }
  if (!isDataSourceEnabled(lookupData, dataSourceType)) {
    return 'context.noResults';
  }
};

const _getLoadingIcon = (lookupData, { dataSourceType }) => {
  if (!lookupData) {
    return false;
  }
  return (dataSourceType === 'Endpoint') ? (lookupData.IOC || lookupData.Machines || lookupData.Modules) : lookupData[dataSourceType];
};

const _tabsCurrentState = (state, context) => {
  const lookupData = getLookupData(context);
  return (state.tabs || []).map((tab) => ({
    ...tab,
    toolTipText: _getToolTipText(lookupData, tab),
    loadingIcon: !_getLoadingIcon(lookupData, tab) && tab.isConfigured
  }));
};

const _activeTabName = (state) => state.activeTabName;

const _dataSources = (state) => state.dataSources;

/*
 * When the active tab changes, updates `isActive` & `tabClass` of each tab.
 * Returns the same value as `tabsActive`, just updates tab properties.
 */
export const getTabsCurrentState = createSelector(
 [_tabsCurrentState, _activeTabName],
 (tabsCurrentState, activeTabName) => {
   if (tabsCurrentState) {
     return tabsCurrentState.map((tab) => ({
       ...tab,
       isActive: tab.field === activeTabName,
       tabClass: tab.field === activeTabName ? 'tab-active-background' : ''
     }));
   }
 });

 /*
  * When the active tab changes, updates `isActive` & `tabClass` of each tab.
  * Returns the same value as `tabsActive`, just updates tab properties.
  */
export const getActiveDataSource = createSelector(
 [_dataSources, _activeTabName],
 (dataSources, activeTabName) => {
   return dataSources ? dataSources.find((dataSource) => activeTabName === dataSource.dataSourceType) : null;
 });

  /*
   * When the active tab changes, updates `isActive` & `tabClass` of each tab.
   * Returns the same value as `tabsActive`, just updates tab properties.
   */
export const onLiveConnectTab = createSelector(
  [_activeTabName],
  (activeTabName) => {
    return ['LiveConnect-Ip', 'LiveConnect-Domain', 'LiveConnect-File'].includes(activeTabName);
  });
