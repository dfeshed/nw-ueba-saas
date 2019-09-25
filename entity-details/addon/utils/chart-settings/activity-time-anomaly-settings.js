/**
 * This util will returns AM chart settings for time anomalies.
 * @private
 */

import moment from 'moment';
import _ from 'lodash';

export default (anomalyTypeFieldName) => {
  return {
    params: {
      feature: anomalyTypeFieldName,
      function: 'distinctEventsByTime'
    },
    title: anomalyTypeFieldName,
    sortData: (data) => {
      return _.orderBy(data, ['originalCategory'], ['asc']);
    },
    dataAdapter: (dataItem, zoneId, localeId, keyPrefix = '') => {
      const timezoneDate = moment(parseInt(dataItem.keys[0], 10)).locale(localeId).tz(zoneId).format('DD MMM HH:mm');
      const chartItem = {};
      chartItem[`${keyPrefix}category`] = timezoneDate;
      chartItem[`${keyPrefix}originalCategory`] = dataItem.keys[0];
      chartItem[`${keyPrefix}value`] = dataItem.value;
      chartItem[`${keyPrefix}radius`] = 1;

      if (dataItem.anomaly) {
        chartItem[`${keyPrefix}color`] = '#CC3300';
        chartItem[`${keyPrefix}radius`] = 5;
      }
      return chartItem;
    },
    chartSettings: {
      type: 'column'
    }
  };
};