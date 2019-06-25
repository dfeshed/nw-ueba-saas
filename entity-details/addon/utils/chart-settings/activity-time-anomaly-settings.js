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
      return _.orderBy(data, ['originalCategory'], ['asc', 'desc']);
    },
    dataAdapter: (dataItem) => {
      const chartItem = {
        category: moment(parseInt(dataItem.keys[0], 10)).format('DD MMM'),
        originalCategory: dataItem.keys[0],
        value: dataItem.value
      };

      if (dataItem.anomaly) {
        chartItem.color = '#CC3300';
      }
      return chartItem;
    },
    dataAggregator: (list) => {
      const groupedData = [];
      _.forEach(list, (value) => {
        const groupedValue = _.find(groupedData, (val) => val.category === value.category);
        if (groupedValue) {
          groupedValue.value = groupedValue.value + value.value;
          if (value.color) {
            groupedValue.color = value.color;
          }
        } else {
          groupedData.push(value);
        }
      });
      return groupedData;
    },
    chartSettings: {
      type: 'column'
    }
  };
};