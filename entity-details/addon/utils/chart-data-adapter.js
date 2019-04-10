/**
 * This utils update data based on data provider for AM chart rendering.
 * @private
 */

import _ from 'lodash';

export default (settings, chartData) => {
  const list = settings.sortData ? settings.sortData(chartData) : chartData;
  // digest data if adapter provided by settings
  settings.chartSettings.dataProvider = settings.dataAdapter ? _.map(list, settings.dataAdapter) : list;
  return settings;
};