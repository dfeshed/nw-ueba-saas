import { isEmpty } from '@ember/utils';
import { helper } from '@ember/component/helper';
import { warn } from '@ember/debug';
import _ from 'lodash';

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
/**
 * List data is a special case where data for every List represents a data source similar to
 * Incidents and Alerts. But for display CH want to combined all list info under one table.
 * This _enrichListData will take all List data source data in loop and keep adding the same in result list of first data
 * source data.
 * @type {Function}
 * @private
 */
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
    /**
     * All Lists are different data sources so CH has to mention last updated for List which is recently updated.
     * This logic will check for latest of Last updated or content last updated and modify footer value accordingly.
     * @private
     */
    const latestUpdatedDate = dataSourceData.dataSourceLastModifiedOn > dataSourceData.contentLastModifiedOn ? dataSourceData.dataSourceLastModifiedOn : dataSourceData.contentLastModifiedOn;
    const timeQuerySubmitted = contextDataForDS.dataSourceLastModifiedOn > latestUpdatedDate ? contextDataForDS.dataSourceLastModifiedOn : latestUpdatedDate;
    const resultMeta = contextDataForDS.resultMeta.asMutable ? contextDataForDS.resultMeta.asMutable() : contextDataForDS.resultMeta;
    _.set(resultMeta, 'timeQuerySubmitted', timeQuerySubmitted);
    _.set(dataSourceData.resultMeta, 'timeQuerySubmitted', timeQuerySubmitted);
    contextDataForDS.resultMeta = resultMeta;

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
  return lookupData.set(dataSourceData.dataSourceGroup, _enrichDataSourceData(contextDataForDS ? contextDataForDS : null, dataSourceData));
};

const _updateHostCountForMachine = (lookupData) => {
  if (lookupData.Modules && lookupData.Modules.resultMeta && lookupData.Machines && !isEmpty(lookupData.Machines.resultList)) {
    return lookupData.setIn(['Machines', 'resultList', '0', 'total_modules_count'], lookupData.Modules.resultMeta.total_modules_count);
  }
  return lookupData;
};

export function contextDataParser([data, [lookupData]]) {
  if (!data) {
    return;
  }
  data.forEach((dataSourceData) => {
    if (dataSourceData.dataSourceGroup) {
      lookupData = _populateContextData(dataSourceData, lookupData);
      lookupData = _updateHostCountForMachine(lookupData);
    } else {
      warn(`DataSource group for ${dataSourceData.dataSourceName} is not configured`, { id: 'context.helpers.context-data-parser' });
    }
  });
  return lookupData;
}

export default helper(contextDataParser);
