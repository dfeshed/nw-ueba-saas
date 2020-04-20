/**
 * This util will returns AM chart settings for time based grouped anomalies.
 * @private
 */
import _ from 'lodash';
const hoursInDay = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23];
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
        hour: parseInt(dataItem.keys[1], 10),
        value: dataItem.value,
        strokeColor: '#000000'
      };

      if (dataItem.anomaly) {
        chartItem.color = '#CC3300';
      }
      return chartItem;
    },
    dataAggregator: (list) => {
      const aggregatedData = [];
      _.forEach(weekDaysUS, (day) => {
        _.forEach(hoursInDay, (hour) => {
          const hourValue = _.find(list, (val) => val.weekday === day && val.hour === hour);
          if (hourValue) {
            aggregatedData.push(hourValue);
          } else {
            aggregatedData.push({
              weekday: day,
              hour,
              value: 0,
              color: '#202020',
              strokeColor: '#000000'
            });
          }
        });
      });
      return aggregatedData;
    },
    chartSettings: {
      type: 'heatmap'
    }
  };
};