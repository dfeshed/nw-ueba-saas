import TabList from 'context/config/dynamic-tab';
import get from 'ember-metal/get';
import { isEmpty } from 'ember-utils';

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
  if (!lookupData || !lookupData[dataSourceGroup] || isEmpty(lookupData[dataSourceGroup].resultList)) {
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

const getTimeWindow = (dsData, i18n) => {
  if (dsData && dsData.resultMeta && dsData.resultMeta.timeQuerySubmitted) {
    let timeWindow = i18n.t('context.timeUnit.allData');
    const timeCount = dsData.resultMeta['timeFilter.timeUnitCount'];
    if (timeCount) {
      let timeUnitString = dsData.resultMeta['timeFilter.timeUnit'];
      const timeUnit = timeCount > 1 ? `${timeUnitString}S` : `${timeUnitString}`;
      timeUnitString = i18n.t(`context.timeUnit.${timeUnit}`);
      timeWindow = `${timeCount} ${timeUnitString}`;
    }
    return {
      lastUpdated: dsData.resultMeta.timeQuerySubmitted,
      timeWindow
    };
  }
};

const getErrorMessage = (dsData, i18n) => {
  if (dsData && !isEmpty(dsData.errorMessage)) {
    let errorMessage = i18n.t(`context.error.${dsData.errorMessage}`);
    if (dsData.errorParameters) {
      errorMessage = errorMessage.string;
      for (const [key, value] of Object.entries(dsData.errorParameters)) {
        errorMessage = errorMessage.replace(`{${key}}`, value);
      }
    }
    return errorMessage;
  } else if (dsData && isEmpty(dsData.resultList)) {
    return i18n.t('context.error.noData');
  }
  return '';
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

const getSortedData = (data, icon, field) => {
  if (field === '_id' || field === 'incidentId') {
    const sorted = data.sort((a, b) => {
      if (isEmpty(a[field])) {
        return -1;
      }
      if (isEmpty(b[field])) {
        return 1;
      }
      return a[field].replace('INC-', '') - b[field].replace('INC-', '');
    });
    return (icon === 'arrow-down-8' ? sorted.reverse() : sorted).concat([]);
  }

  if (icon) {
    const sorted = data.sortBy(field);
    return icon === 'arrow-down-8' ? sorted.reverse() : sorted;
  } else {
    return data;
  }
};

const pivotToInvestigateUrl = (entityType, entityId) => {
  let query = '';
  switch (entityType) {
    case 'IP':
      query = `ip.src=${entityId}||ip.dst=${entityId}`;
      break;
    case 'DOMAIN':
      query = `alias.host='${entityId}'`;
      break;
    case 'HOST':
      query = `device.host='${entityId}'`;
      break;
    case 'USER':
      query = `username='${entityId}'||user.src='${entityId}'||user.dst='${entityId}'||ad.username.src='${entityId}'||ad.username.dst='${entityId}'`;
      break;
    case 'FILE_NAME':
      query = `filename='${entityId}'`;
      break;
    case 'FILE_HASH':
      query = `checksum='${entityId}'`;
      break;
    case 'MAC_ADDRESS':
      // ECAT may provide long MACs with 8-pairs of hex values (e.g., 11-11-11-11-11-11-11-11)
      // but Core only supports 6-pairs of hex values (e.g., 11-11-11-11-11-11)
      if (String(entityId).length > 17) {
        return '';
      }
      // ECAT can provide hyphenated format, but Core requires colon format instead
      entityId = String(entityId).replace(/\-/g, ':');
      query = `eth.src=${entityId}||eth.dst=${entityId}`;
      break;
    default:
      return '';
  }
  return `/investigation/choosedevice/navigate/query/${query}`;
};

export {
  isDataSourceEnabled,
  getData,
  getTimeWindow,
  getErrorMessage,
  getTabs,
  getSortedData,
  pivotToInvestigateUrl
};
