/**
 * This utils update data based on data provider for AM chart rendering.
 * @private
 */

import _ from 'lodash';

export default (settings, chartData) => {
  const list = settings.dataAdapter ? _.map(chartData, settings.dataAdapter) : chartData;
  // digest data if adapter provided by settings
  settings.chartSettings.dataProvider = settings.sortData ? settings.sortData(list) : list;
  return settings;
};