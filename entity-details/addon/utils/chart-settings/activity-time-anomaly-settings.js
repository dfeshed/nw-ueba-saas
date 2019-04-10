/**
 * This util will returns AM chart settings for time anomalies.
 * @private
 */

import moment from 'moment';
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

/**
 * Used as hours in a day axis.
 */
const HoursInDay = [
  0,
  1,
  2,
  3,
  4,
  5,
  6,
  7,
  8,
  9,
  10,
  11,
  12,
  13,
  14,
  15,
  16,
  17,
  18,
  19,
  20,
  21,
  22,
  23
];

export default (anomalyTypeFieldName) => {
  return {
    params: {
      feature: anomalyTypeFieldName,
      function: 'distinctEventsByTime'
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
    chartSettings: {
      type: 'column',
      xAxis: {
        categories: weekDaysUS.reverse(),
        title: {
          text: 'Week days'
        },
        labels: {
          formatter: () => {
            return `pascalCase${this.value}`;
          }
        }
      },
      yAxis: {
        title: {
          text: 'Hours'
        },
        categories: HoursInDay
      },
      colorAxis: {
        min: 1,
        minColor: '#8fbde4',
        maxColor: '#2766a9'
      },
      title: {
        text:
          '{{dataEntitiesIds[0]|entityIdToName}} Authentication Times (Last 90 days)'
      },
      series: [
        {
          name:
            '{{dataEntitiesIds[0]|entityIdToName}} Authentication Times (Last 90 days) '
        }
      ]
    }
  };
};