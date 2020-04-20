/**
 * This utils update data based on data provider for AM chart rendering.
 * @private
 */

import _ from 'lodash';

export default (settings, chartData, zoneId, localeId, baselineData) => {
  let list = settings.dataAdapter ? _.map(chartData, (data) => settings.dataAdapter(data, zoneId, localeId)) : chartData;
  if (baselineData) {
    // This will be excuted only if chart have base line value.
    settings.hasBaselineData = true;
    // Map data according to data adapter.
    baselineData = settings.dataAdapter ? _.map(baselineData, (data) => settings.dataAdapter(data, zoneId, localeId, 'baselineData-')) : baselineData;
    // Merge both array before as we can have different time line value in these arrays.
    list = _.union(list, baselineData);
    const objectmap = {};
    // This logic is to remove duplicate with merge all properties as an object.
    _.forEach(list, (timelineValue) => {
      if (objectmap[timelineValue.originalCategory]) {
        objectmap[timelineValue.originalCategory] = _.assign({}, objectmap[timelineValue.originalCategory], timelineValue);
      } else {
        objectmap[timelineValue.originalCategory] = timelineValue;
      }
    });
    // Convert object back to list of unique value with merged properties.
    list = _.values(objectmap);
  }
  // digest data if adapter provided by settings
  list = settings.dataAggregator ? settings.dataAggregator(list) : list;
  settings.chartSettings.dataProvider = settings.sortData ? settings.sortData(list) : list;
  return settings;
};