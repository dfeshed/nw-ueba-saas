/**
 * This util will returns AM chart settings for count anomalies.
 * @private
 */

import _ from 'lodash';

export default (anomalyTypeFieldName) => {
  return {
    params: {
      feature: anomalyTypeFieldName,
      function: 'Count'
    },
    title: anomalyTypeFieldName,
    sortData: (data) => {
      return _.orderBy(data, ['anomaly', 'value'], ['asc', 'desc']);
    },
    dataAdapter: (dataItem) => {
      const chartItem = {
        category: dataItem.keys[0],
        originalCategory: dataItem.keys[0],
        value: dataItem.value,
        anomaly: dataItem.anomaly
      };
      if (dataItem.anomaly) {
        chartItem.color = '#CC3300';
      }
      return chartItem;
    },
    handlers: {
      clickGraphItem: () => {
        // indicatorChartTransitionUtil.go('pie', indicator, item);
      }
    },
    chartSettings: {
      type: 'pie',
      legend: {
        position: 'right',
        marginRight: 40,
        autoMargins: false,
        textClickEnabled: true,
        color: 'red'
      },
      balloon: {
        maxWidth: 200
      },
      innerRadius: '60%',
      labelRadius: 10,
      pullOutRadius: 10,
      radius: '40%',
      startRadius: 0,
      colors: ['#0A335C', '#0D6ECD', '#0D8ECF', '#1689FA']
    }
  };
};
