/**
 * This util will returns AM chart object for Pie Chart and render the same.
 * @private
 */


import * as Am4core from '@amcharts/amcharts4/core';
import * as Am4charts from '@amcharts/amcharts4/charts';
import _ from 'lodash';
import { lookup } from 'ember-dependency-lookup';

const createChildDivForPie = () => {
  const chartComponentPlaceholder = document.getElementById('chartComponentPlaceholder');
  const chartDiv = document.createElement('div');
  chartDiv.id = 'chartComponentPlaceholderChart';
  chartDiv.className = 'entity-details-container-body-indicator-details_graph_placeHolder_chart';
  const legendDiv = document.createElement('div');
  legendDiv.className = 'entity-details-container-body-indicator-details_graph_placeHolder_legends';
  const legendDivContainer = document.createElement('div');
  legendDivContainer.id = 'chartComponentPlaceholderLegend';
  legendDiv.appendChild(legendDivContainer);
  chartComponentPlaceholder.appendChild(chartDiv);
  chartComponentPlaceholder.appendChild(legendDiv);
};

export default (settings) => {
  const i18n = lookup('service:i18n');
  createChildDivForPie();
  let chart = Am4core.create('chartComponentPlaceholderChart', Am4charts.PieChart3D);

  chart.legend = new Am4charts.Legend();
  chart.legend.fill = Am4core.color('#FFF');
  chart.hiddenState.properties.opacity = 0; // this creates initial fade-in;
  const title = chart.titles.create();
  title.text = `[bold white]${i18n.t(`investigateUsers.alerts.indicator.indicatorNames.${settings.title}.name`)}[/]`;
  title.fontSize = '1rem';
  title.marginTop = 10;
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

  const legendContainer = Am4core.create('chartComponentPlaceholderLegend', Am4core.Container);
  legendContainer.width = Am4core.percent(100);
  legendContainer.height = Am4core.percent(100);
  chart.legend.parent = legendContainer;

  chart.events.on('datavalidated', resizeLegend);
  chart.events.on('maxsizechanged', resizeLegend);

  function resizeLegend() {
    document.getElementById('chartComponentPlaceholderLegend').style.height = `${chart.legend.contentHeight}px`;
  }
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