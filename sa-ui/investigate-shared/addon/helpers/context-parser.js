import { isEmpty } from '@ember/utils';
import { helper } from '@ember/component/helper';
import { warn } from '@ember/debug';

const _enrichDataSourceData = (contextDataForDS, dataSourceData) => {
  const firstDSEntry = !contextDataForDS;
  contextDataForDS = (firstDSEntry) ? dataSourceData : contextDataForDS.asMutable();
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
    if (dataSourceData.dataSourceGroup === 'Alerts' || dataSourceData.dataSourceGroup === 'Incidents') {
      lookupData = _populateContextData(dataSourceData, lookupData);
      lookupData = _updateHostCountForMachine(lookupData);
    } else if (!dataSourceData.dataSourceGroup) {
      warn(`DataSource group for ${dataSourceData.dataSourceName} is not configured`, { id: 'context.helpers.context-data-parser' });
    }
  });
  return lookupData;
}

export default helper(contextDataParser);
