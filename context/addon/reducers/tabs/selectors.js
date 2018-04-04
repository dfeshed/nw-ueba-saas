import { createSelector } from 'reselect';
import { getLookupData } from 'context/reducers/context/selectors';
import { isDataSourceEnabled, getData } from 'context/util/context-data-modifier';
import { isEmpty } from '@ember/utils';

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

const _getArcherDataSource = (state) => {
  const dataSources = _dataSources(state);
  return dataSources ? dataSources.find((dataSource) => dataSource.field === 'Archer') : null;
};

const _archerData = (state, context) => {
  const lookupData = getLookupData(context);
  const archerDS = _getArcherDataSource(state);
  if (!isEmpty(archerDS)) {
    return getData(lookupData, archerDS.details);
  }
};

const _archerLookupData = (state, context) => {
  const lookupData = getLookupData(context);
  return lookupData && lookupData.Archer ? lookupData.Archer : null;
};

export const getArcherErrorMessage = createSelector(
  [_getArcherDataSource, _archerLookupData],
  (archerDataSource, archerLookupData) => {
    let errorType, errorMessage;
    if (isEmpty(archerDataSource) || !archerDataSource.isConfigured) {
      errorType = 'Warning';
      errorMessage = 'context.error.archer.notConfigured';
    } else if (archerLookupData && !isEmpty(archerLookupData.errorMessage)) {
      errorType = 'Error';
      errorMessage = 'context.error.archer.notReachable';
    } else if (archerLookupData && isEmpty(archerLookupData.resultList)) {
      errorType = 'Warning';
      errorMessage = 'context.error.archer.noData';
    }
    return { errorType, errorMessage };
  });

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


export const getArcherUrl = createSelector(
  [_archerData],
  (archerData) => {
    return !isEmpty(archerData) ? archerData[0].Url : '';
  });
