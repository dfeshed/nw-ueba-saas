import TabList from 'context/config/dynamic-tab';
import get from 'ember-metal/get';

const isNotEmpty = (contextData, dataSourceGroup, defaultEnable) => {
  if (!contextData) {
    return defaultEnable;
  }
  const dsGroup = contextData[dataSourceGroup];
  return (dsGroup && dsGroup.resultList) ? (dsGroup.resultList.length > 0) : defaultEnable;
};

const isNotError = (contextData, dataSourceGroup) => {
  return !(contextData && contextData[dataSourceGroup] && contextData[dataSourceGroup].errorMessage);
};

const isDataSourceEnabled = (contextData, dataSourceGroup, defaultEnable) => {
  if (dataSourceGroup === 'Endpoint') {
    return isNotEmpty(contextData, 'Machines', defaultEnable) || !isNotError(contextData, 'Machines') ||
      isNotEmpty(contextData, 'Modules', defaultEnable) || !isNotError(contextData, 'Modules') ||
      isNotEmpty(contextData, 'IOC', defaultEnable) || !isNotError(contextData, 'IOC');
  }
  return isNotEmpty(contextData, dataSourceGroup, defaultEnable) || !isNotError(contextData, dataSourceGroup);
};

const getData = (lookupData, { dataSourceGroup, sortColumn, sortOrder }) => {
  if (!lookupData || !lookupData[dataSourceGroup]) {
    return;
  }
  if (sortColumn) {
    lookupData[dataSourceGroup].resultList.sort((a, b) => {
      const aProp = get(a, sortColumn);
      const bProp = get(b, sortColumn);
      if (aProp < bProp) {
        return sortOrder === 'desc' ? 1 : -1;
      } else if (aProp > bProp) {
        return sortOrder === 'desc' ? -1 : 1;
      } else {
        return 0;
      }
    });
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

export {
  isDataSourceEnabled,
  getData,
  getHeaderData,
  getTabs
};