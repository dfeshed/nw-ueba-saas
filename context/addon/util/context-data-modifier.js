import TabList from 'context/config/dynamic-tab';
import { isEmpty } from 'ember-utils';

const getResultListSize = (contextData, dataSourceGroup) => {
  return (contextData && contextData[dataSourceGroup] && contextData[dataSourceGroup].resultList) ? contextData[dataSourceGroup].resultList.length : 0;
};

const getCount = (contextData, dataSourceGroup) => {
  if (dataSourceGroup === 'Endpoint') {
    return getResultListSize(contextData, 'Machines') +
      getResultListSize(contextData, 'Modules') +
      getResultListSize(contextData, 'IOC');
  }
  return getResultListSize(contextData, dataSourceGroup);
};

const getData = (lookupData, { dataSourceGroup, sortColumn, sortOrder }) => {
  if (!lookupData || !lookupData[dataSourceGroup]) {
    return;
  }
  if (sortColumn) {
    if (sortOrder) {
      lookupData[dataSourceGroup].resultList.sort((a, b) => (b[sortColumn] - a[sortColumn]));
    } else {
      lookupData[dataSourceGroup].resultList.sort((a, b) => (a[sortColumn] - b[sortColumn]));
    }
  }
  return lookupData[dataSourceGroup].resultList;
};

const getHeaderData = (dsData, i18n) => {
  let headerData = {};
  if (dsData && dsData.resultMeta && dsData.resultMeta.timeQuerySubmitted) {
    let timeWindow = i18n.t('context.timeUnit.allData');
    const timeCount = dsData.resultMeta['timeFilter.timeUnitCount'];
    if (timeCount) {
      let timeUnitString = dsData.resultMeta['timeFilter.timeUnit'];
      const timeUnit = timeCount > 1 ? `${timeUnitString}S` : `${timeUnitString}`;
      timeUnitString = i18n.t(`context.timeUnit.${timeUnit}`);
      timeWindow = `${timeCount} ${timeUnitString}`;
    }
    headerData = {
      lastUpdated: dsData.resultMeta.timeQuerySubmitted,
      timeWindow
    };
  }
  if (dsData && dsData.errorMessage) {
    let errorMessage = i18n.t(`context.error.${dsData.errorMessage}`);
    if (errorMessage.string) {
      if (dsData.errorParameters) {
        errorMessage = errorMessage.string;
        for (const [key, value] of Object.entries(dsData.errorParameters)) {
          errorMessage = errorMessage.replace(`{${key}}`, value);
        }
      }
      headerData.errorMessage = errorMessage;
    }
  }
  return headerData;
};

const needToDisplay = (contextData, lookupData, dSDetails) => {
  if (contextData || !lookupData || !lookupData[dSDetails.dataSourceGroup]) {
    return true;
  }
  if (0 === getCount(lookupData, dSDetails.dataSourceGroup)) {
    return false;
  }
  return true;
};

const getTabs = (meta, dataSources) => {
  const tabList = TabList.find((tab) => tab.tabType === meta);
  // Map over the columns and build the tab count text for each tab
  return tabList.columns.filter((tab) => tab.tabRequired).map((tab) => {
    const dataSourceDetails = dataSources.find((dataSource) => dataSource.dataSourceType === tab.dataSourceType);
    return {
      ...tab,
      isConfigured: dataSourceDetails ? dataSourceDetails.isConfigured : false,
      panelId: `tabs${tab.field.camelize()}`
    };
  });
};

const getTabEnabled = ([ lookupData ], dataSourceType) => {
  if (0 === getCount(lookupData, dataSourceType)) {
    return false;
  }
  return true;
};

const getActiveTabName = (activeTabName, data) => {
  if (activeTabName != null || !data) {
    return activeTabName;
  }
  const firstDataSourceWithData = data.find((dataSource) => {
    return dataSource && !isEmpty(dataSource.resultList);
  });

  return firstDataSourceWithData ? firstDataSourceWithData.dataSourceGroup : activeTabName;
};

const noDataToDisplayMessage = (dataSourceList, [lookupData]) => {
  if (!lookupData || !dataSourceList) {
    return;
  }
  const dataSourcesConfigured = dataSourceList.filter((dataSource) => {
    return dataSource.isConfigured;
  });
  const dataSourcesWithNoData = dataSourcesConfigured.filter((dataSource) => {
    return lookupData[dataSource.dataSourceType] ? getCount(lookupData, dataSource.dataSourceType) === 0 : false;
  });
  return dataSourcesWithNoData && dataSourcesWithNoData.length === dataSourcesConfigured.length ? 'context.noData' : '';
};

export {
  getCount,
  getData,
  getHeaderData,
  needToDisplay,
  getTabs,
  getTabEnabled,
  getActiveTabName,
  noDataToDisplayMessage
};