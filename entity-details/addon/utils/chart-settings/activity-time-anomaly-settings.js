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
    dataAdapter: (dataItem) => {
      const chartItem = {
        category: moment(parseInt(dataItem.keys[0], 10)).format('DD MMM HH:mm'),
        originalCategory: dataItem.keys[0],
        value: dataItem.value
      };

      if (dataItem.anomaly) {
        chartItem.color = '#CC3300';
      }
      return chartItem;
    },
    chartSettings: {
      type: 'column'
    }
  };
};