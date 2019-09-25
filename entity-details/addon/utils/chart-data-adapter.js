/**
 * This utils update data based on data provider for AM chart rendering.
 * @private
 */

import _ from 'lodash';

export default (settings, chartData, zoneId, localeId, baselineData) => {
  let list = settings.dataAdapter ? _.map(chartData, (data) => settings.dataAdapter(data, zoneId, localeId)) : chartData;
  if (baselineData) {
    settings.hasBaselineData = true;
    baselineData = settings.dataAdapter ? _.map(baselineData, (data) => settings.dataAdapter(data, zoneId, localeId, 'baselineData-')) : baselineData;
    list = _.merge(list, baselineData);
  }
  // digest data if adapter provided by settings
  list = settings.dataAggregator ? settings.dataAggregator(list) : list;
  settings.chartSettings.dataProvider = settings.sortData ? settings.sortData(list) : list;
  return settings;
};