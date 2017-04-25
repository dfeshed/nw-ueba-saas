import { helper } from 'ember-helper';
import Ember from 'ember';

const {
  set,
  Logger,
  isEmpty
} = Ember;

const windowsTimestampToMilliseconds = (windowsTimestamp) => windowsTimestamp ? windowsTimestamp / 10000 - 11644473600000 : null;

const _enrichADFields = (resultList) => {
  return resultList.map((list) => {
    const address = [list.physicalDeliveryOfficeName, list.city, list.state, list.country, list.postalCode];
    const location = address.join(' ');
    return {
      ...list,
      location,
      lastLogonTimestamp: windowsTimestampToMilliseconds(list.lastLogonTimestamp),
      lastLogon: windowsTimestampToMilliseconds(list.lastLogon)
    };
  });
};

const _enrichListData = (contextDataForDS, dataSourceData, firstDSEntry) => {
  if (!isEmpty(contextDataForDS)) {
    if (firstDSEntry) {
      contextDataForDS.resultList = [];
    }
    set(contextDataForDS, 'dataSourceLastModifiedOn', contextDataForDS.dataSourceLastModifiedOn > contextDataForDS.contentLastModifiedOn ? contextDataForDS.dataSourceLastModifiedOn : contextDataForDS.contentLastModifiedOn);
    contextDataForDS.resultList = contextDataForDS.resultList.concat([dataSourceData]);
    return contextDataForDS;
  }
};


const _enrichDataSourceData = (contextDataForDS, dataSourceData) => {
  const firstDSEntry = !contextDataForDS;
  contextDataForDS = contextDataForDS || dataSourceData;
  switch (dataSourceData.dataSourceGroup) {
    case 'Modules': {
      if (dataSourceData.resultMeta.iocScore_gte) {
        dataSourceData.header = ` (IIOC Score > ${dataSourceData.resultMeta.iocScore_gte})`;
      }
      contextDataForDS.resultList = (contextDataForDS.resultList || []).concat(dataSourceData.resultList);
      break;
    }
    case 'Users': {
      contextDataForDS.resultList = (firstDSEntry) ? _enrichADFields(dataSourceData.resultList) : (contextDataForDS.resultList || []).concat(_enrichADFields(dataSourceData.resultList));
      break;
    }
    case 'LIST': {
      contextDataForDS = _enrichListData(contextDataForDS, dataSourceData, firstDSEntry);
      break;
    }
  }
  return contextDataForDS;
};
const _enrichDataSourceDataForSameGroup = (contextDataForDS, dataSourceData) => {
  contextDataForDS = _enrichDataSourceData(contextDataForDS, dataSourceData);
  return contextDataForDS;
};
const _populateContextData = (dataSourceData, lookupData) => {
  const contextDataForDS = lookupData[dataSourceData.dataSourceGroup];
  if (contextDataForDS) {
    lookupData[dataSourceData.dataSourceGroup] = _enrichDataSourceDataForSameGroup(contextDataForDS, dataSourceData);
  } else {
    lookupData[dataSourceData.dataSourceGroup] = _enrichDataSourceData(null, dataSourceData);
  }
};

const _updateHostCountForMachine = (contextDataForDS, dataSourceData) => {
  if (dataSourceData.Modules && dataSourceData.Modules.resultMeta && dataSourceData.Machines && !isEmpty(dataSourceData.Machines.resultList)) {
    set(dataSourceData.Machines.resultList[0], 'total_modules_count', dataSourceData.Modules.resultMeta.total_modules_count);
  }
};

export function contextDataParser([data, [lookupData]]) {
  if (!data) {
    return;
  }
  lookupData = lookupData || {};
  data.forEach((dataSourceData) => {
    if (dataSourceData.dataSourceGroup) {
      _populateContextData(dataSourceData, lookupData);
      _updateHostCountForMachine(dataSourceData, lookupData);
    } else {
      Logger.error('DataSource group for', dataSourceData.dataSourceName, 'is not configured');
    }
  });
  return lookupData;
}

export default helper(contextDataParser);