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
      chartItem.category = timezoneDate;
      chartItem.strokeColor = '#01579B';
      chartItem.originalCategory = dataItem.keys[0];
      chartItem[`${keyPrefix}value`] = dataItem.value;
      chartItem[`${keyPrefix}radius`] = 1;
      if (keyPrefix === '') {
        chartItem[`${keyPrefix}color`] = '#0288D1';
      } else {
        chartItem[`${keyPrefix}color`] = '#757575';
      }
      if (dataItem.anomaly) {
        chartItem[`${keyPrefix}color`] = '#A60808';
        chartItem.strokeColor = '#01579B';
        chartItem[`${keyPrefix}radius`] = 5;
      }
      return chartItem;
    },
    chartSettings: {
      type: 'column'
    }
  };
};