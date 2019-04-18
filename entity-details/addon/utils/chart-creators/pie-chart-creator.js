/**
 * This util will returns AM chart object for Pie Chart and render the same.
 * @private
 */


import * as Am4core from '@amcharts/amcharts4/core';
import * as Am4charts from '@amcharts/amcharts4/charts';
import _ from 'lodash';

export default (settings) => {
  let chart = Am4core.create('chartComponentPlaceholder', Am4charts.PieChart3D);

  chart.legend = new Am4charts.Legend();
  chart.legend.fill = Am4core.color('#FFF');
  chart.hiddenState.properties.opacity = 0; // this creates initial fade-in;
  chart = _.merge(chart, settings.chartSettings);
  chart.data = settings.chartSettings.dataProvider;
  const { colors } = settings.chartSettings;
  let colorIndex = -1;
  const series = chart.series.push(new Am4charts.PieSeries3D());
  series.dataFields.value = 'value';
  series.dataFields.category = 'keys';
  series.legendSettings.labelText = '[bold white]{category}[/]';
  series.legendSettings.valueText = '[bold white]{value}[/]';
  series.labels.template.fill = Am4core.color('#ebebeb');
  series.colors.list = _.map(chart.data, ({ color }) => {
    if (color) {
      return Am4core.color(color);
    } else {
      colorIndex = colorIndex === colors.length ? 0 : ++colorIndex;
      return Am4core.color(colors[colorIndex]);
    }
  });
  return chart;
};