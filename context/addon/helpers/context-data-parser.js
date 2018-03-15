import { set } from '@ember/object';
import { isEmpty } from '@ember/utils';
import { helper } from '@ember/component/helper';
import { warn } from '@ember/debug';

const windowsTimestampToMilliseconds = (windowsTimestamp) => windowsTimestamp ? windowsTimestamp / 10000 - 11644473600000 : null;

const _enrichADFields = (resultList) => {
  if (!resultList) {
    return;
  }
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
  const results = dataSourceData.resultList;
  if (firstDSEntry) {
    contextDataForDS = { ...contextDataForDS, resultList: [] };
  }
  if (dataSourceData.errorMessage && isEmpty(results)) {
    contextDataForDS.resultList = [];
    contextDataForDS.errorMessage = dataSourceData.errorMessage;
  }
  if (!isEmpty(results) && isEmpty(contextDataForDS.errorMessage)) {
    const latestUpdatedDate = dataSourceData.dataSourceLastModifiedOn > dataSourceData.contentLastModifiedOn ? dataSourceData.dataSourceLastModifiedOn : dataSourceData.contentLastModifiedOn;
    set(contextDataForDS, 'dataSourceLastModifiedOn', contextDataForDS.dataSourceLastModifiedOn > latestUpdatedDate ? contextDataForDS.dataSourceLastModifiedOn : latestUpdatedDate);
    dataSourceData.dataSourceLastModifiedOn = latestUpdatedDate;
    contextDataForDS.resultList = contextDataForDS.resultList.concat([dataSourceData]);
  }
  return contextDataForDS;
};


const _enrichDataSourceData = (contextDataForDS, dataSourceData) => {
  const firstDSEntry = !contextDataForDS;
  contextDataForDS = (firstDSEntry) ? dataSourceData : contextDataForDS.asMutable();
  switch (dataSourceData.dataSourceGroup) {
    case 'Modules': {
      if (dataSourceData.resultMeta && dataSourceData.resultMeta.iocScore_gte) {
        dataSourceData.header = ` (IIOC Score > ${dataSourceData.resultMeta.iocScore_gte})`;
      }
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

const _populateContextData = (dataSourceData, lookupData) => {
  const contextDataForDS = lookupData[dataSourceData.dataSourceGroup];
  lookupData = lookupData.set(dataSourceData.dataSourceGroup, _enrichDataSourceData(contextDataForDS ? contextDataForDS : null, dataSourceData));
  return lookupData;
};

const _updateHostCountForMachine = (contextDataForDS, dataSourceData) => {
  if (dataSourceData.Modules && dataSourceData.Modules.resultMeta && dataSourceData.Machines && !isEmpty(dataSourceData.Machines.resultList)) {
    dataSourceData.Machines.resultList[0].set('total_modules_count', dataSourceData.Modules.resultMeta.total_modules_count);
  }
};

export function contextDataParser([data, [lookupData]]) {
  if (!data) {
    return;
  }
  data.forEach((dataSourceData) => {
    if (dataSourceData.dataSourceGroup) {
      lookupData = _populateContextData(dataSourceData, lookupData);
      _updateHostCountForMachine(dataSourceData, lookupData);
    } else {
      warn(`DataSource group for ${dataSourceData.dataSourceName} is not configured`, { id: 'context.helpers.context-data-parser' });
    }
  });
  return lookupData;
}

export default helper(contextDataParser);
