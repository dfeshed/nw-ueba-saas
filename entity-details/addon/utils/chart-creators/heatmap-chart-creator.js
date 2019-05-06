/**
 * This util will returns AM chart object for Heatmap Chart and render the same.
 * @private
 */

import * as Am4core from '@amcharts/amcharts4/core';
import * as Am4charts from '@amcharts/amcharts4/charts';
import _ from 'lodash';
import { lookup } from 'ember-dependency-lookup';


export default (settings) => {
  const i18n = lookup('service:i18n');

  const chart = Am4core.create('chartComponentPlaceholder', Am4charts.XYChart);
  chart.maskBullets = false;
  chart.plotContainer.stroke = '#67b7dc';
  chart.padding = 20;

  const title = chart.titles.create();
  title.text = `[bold white]${i18n.t(`investigateUsers.alerts.indicator.indicatorNames.${settings.title}.chartTitle`)}[/]`;
  title.fontSize = '1rem';
  title.marginTop = 10;

  const xAxis = chart.xAxes.push(new Am4charts.CategoryAxis());
  const yAxis = chart.yAxes.push(new Am4charts.CategoryAxis());

  xAxis.dataFields.category = 'weekday';
  yAxis.dataFields.category = 'hour';
  xAxis.renderer.labels.template.fill = 'white';
  yAxis.renderer.labels.template.fill = 'white';
  yAxis.renderer.labels.template.adapter.add('text', (text) => {
    return `${text}:00`;
  });
  xAxis.renderer.grid.template.disabled = true;
  xAxis.renderer.minGridDistance = 5;

  yAxis.renderer.inversed = true;

  _.merge(chart, settings.chartSettings);

  const series = chart.series.push(new Am4charts.ColumnSeries());
  series.dataFields.categoryX = 'weekday';
  series.dataFields.categoryY = 'hour';
  series.dataFields.value = 'value';
  series.sequencedInterpolation = true;
  series.defaultState.transitionDuration = 3000;
  series.legendSettings.labelText = '[bold white]{value}[/]';
  series.columns.template.propertyFields.fill = 'color';
  series.columns.template.propertyFields.stroke = 'color';

  const bgColor = new Am4core.InterfaceColorSet().getFor('background');

  const columnTemplate = series.columns.template;
  columnTemplate.strokeWidth = 1;
  columnTemplate.strokeOpacity = 0.2;
  columnTemplate.stroke = Am4core.color('#ffffff');

  // columnTemplate.stroke = bgColor;
  columnTemplate.tooltipText =
    "{weekday}, {hour}: {value.workingValue.formatNumber('#.')}";
  columnTemplate.width = Am4core.percent(100);
  columnTemplate.height = Am4core.percent(100);

  series.heatRules.push({
    target: columnTemplate,
    property: 'fill',
    min: Am4core.color(bgColor),
    max: chart.colors.getIndex(0)
  });

  // heat legend
  const heatLegend = chart.bottomAxesContainer.createChild(Am4charts.HeatLegend);
  heatLegend.width = Am4core.percent(100);
  heatLegend.series = series;
  heatLegend.valueAxis.renderer.labels.template.fontSize = 9;
  heatLegend.valueAxis.renderer.labels.template.fill = Am4core.color('white');

  heatLegend.valueAxis.renderer.minGridDistance = 10;

  // heat legend behavior
  series.columns.template.events.on('over', (event) => {
    handleHover(event.target);
  });

  series.columns.template.events.on('hit', (event) => {
    handleHover(event.target);
  });

  function handleHover(column) {
    if (!isNaN(column.dataItem.value)) {
      heatLegend.valueAxis.showTooltipAt(column.dataItem.value);
    } else {
      heatLegend.valueAxis.hideTooltip();
    }
  }

  series.columns.template.events.on('out', () => {
    heatLegend.valueAxis.hideTooltip();
  });
  chart.data = settings.chartSettings.dataProvider;
  return chart;
};
