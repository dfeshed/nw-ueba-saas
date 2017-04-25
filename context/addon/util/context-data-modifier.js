import TabList from 'context/config/dynamic-tab';

const getCount = (contextData, dataSourceGroup) => {
  return (contextData && contextData[dataSourceGroup] && contextData[dataSourceGroup].resultList) ? contextData[dataSourceGroup].resultList.length : 0;
};

const getData = (lookupData, { dataSourceGroup, sortColumn, sortOrder }, activeTabName) => {
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
  const data = lookupData[dataSourceGroup].resultList;
  return (activeTabName === 'overview') ? data.slice(0, 3) : data;
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
  const dataSourcesNames = dataSources.map((dataSource) => {
    return dataSource.dataSourceType;
  });
  // Map over the columns and build the tab count text for each tab
  return tabList.columns.filter((tab) => tab.tabRequired && (tab.dataSourceType === 'overview' || dataSourcesNames.includes(tab.dataSourceType))).map((tab) => ({
    ...tab,
    panelId: `tabs${tab.field.camelize()}`
  }));
};

const getTabEnabled = ([ lookupData ], dataSourceType) => {
  if (dataSourceType === 'overview') {
    return true;
  }
  if (0 === getCount(lookupData, dataSourceType)) {
    return false;
  }
  return true;
};

export {
  getCount,
  getData,
  getHeaderData,
  needToDisplay,
  getTabs,
  getTabEnabled
};