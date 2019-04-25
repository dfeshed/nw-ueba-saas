/**
 * This util will returns AM chart settings for time based grouped anomalies.
 * @private
 */
import _ from 'lodash';
/**
 * Used as weekDaysUs axis
 */
const weekDaysUS = [
  'MONDAY',
  'TUESDAY',
  'WEDNESDAY',
  'THURSDAY',
  'FRIDAY',
  'SATURDAY',
  'SUNDAY'
];

export default (anomalyTypeFieldName) => {
  return {
    params: {
      feature: anomalyTypeFieldName,
      function: 'hourlyCountGroupByDayOfWeek'
    },
    title: anomalyTypeFieldName,
    sortData: (data) => {
      return _.sortBy(data, [(obj) => weekDaysUS.indexOf(obj.weekday) && obj.hour]);
    },
    dataAdapter: (dataItem) => {
      const chartItem = {
        weekday: dataItem.keys[0],
        hour: parseInt(dataItem.keys[1], 2),
        value: dataItem.value
      };

      if (dataItem.anomaly) {
        chartItem.color = '#CC3300';
      }
      return chartItem;
    },
    chartSettings: {
      type: 'heatmap'
    }
  };
};