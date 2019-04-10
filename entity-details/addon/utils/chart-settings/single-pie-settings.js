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
    styleSettings: {},
    colorIndex: 0,
    templates: {
      titles: {
        'Title-1':
          "{{ anomalyTypeFieldName  | buildPieKey: 'title' " +
          '| translate: this}}'
      }
    },
    sortData: (data) => {
      return _.orderBy(data, ['anomaly', 'value'], ['asc', 'desc']);
    },
    dataAdapter: (dataItem) => {
      const chartItem = {
        category: dataItem.keys[0],
        originalCategory: dataItem.keys[0],
        value: dataItem.value
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
        maxWidth: 400
      },

      balloonText:
        "<span style='word-break: break-all'>[[title]]</span><br><span style='font-size:14px;padding-left:5px'><b>[[value]]</b> ([[percents]]%)</span>",
      innerRadius: '60%',
      labelRadius: 10,
      pullOutRadius: 10,
      radius: '40%',
      startRadius: 0,
      colors: ['#0A335C', '#0D6ECD', '#0D8ECF', '#1689FA'],
      colorField: 'color',
      hideLabelsPercent: 10,
      maxLabelWidth: 199,
      pullOutDuration: 0,
      pullOutEffect: 'easeOutSine',
      startAlpha: 1,
      titleField: 'category',
      valueField: 'value',
      color: '#eee',
      creditsPosition: 'bottom-right',
      fontFamily: 'Open Sans',
      fontSize: 12,
      processCount: 999,
      titles: [
        {
          id: 'Title-1',
          fontFamily: "'Open Sans', sans-serif",
          color: 'red',
          size: 12
        }
      ]
    }
  };
};
