/**
 * This util will returns AM chart object for Column Chart and render the same.
 * @private
 */

import * as Am4core from '@amcharts/amcharts4/core';
import * as Am4charts from '@amcharts/amcharts4/charts';
import { lookup } from 'ember-dependency-lookup';
import { navigateToInvestigate } from 'entity-details/utils/pivot-utils';

export default (settings, { entityType, entityName, dataEntitiesIds }, brokerId) => {
  const i18n = lookup('service:i18n');
  const chart = Am4core.create('chartComponentPlaceholder', Am4charts.XYChart);
  chart.data = settings.chartSettings.dataProvider;
  chart.colors.step = 10;
  /* Create axes */
  const categoryAxis = chart.xAxes.push(new Am4charts.CategoryAxis());
  categoryAxis.dataFields.category = 'category';
  categoryAxis.renderer.minGridDistance = 100;
  categoryAxis.renderer.labels.template.fill = '#FFFFFF';
  categoryAxis.renderer.cellStartLocation = 0.2;
  categoryAxis.renderer.cellEndLocation = 0.8;

  const title = chart.titles.create();
  title.text = `[bold white]${i18n.t(`investigateUsers.alerts.indicator.indicatorNames.${settings.title}.chartTitle`)}[/]`;
  title.fontSize = '1rem';
  title.marginTop = 10;

  /* Create value axis */
  const valueAxis = chart.yAxes.push(new Am4charts.ValueAxis());
  valueAxis.renderer.labels.template.fill = '#FFFFFF';
  valueAxis.title.text = `[bold white]${i18n.t(`investigateUsers.alerts.indicator.indicatorNames.${settings.title}.axisYtitle`)}[/]`;

  /* Create series */
  const columnSeries = chart.series.push(new Am4charts.ColumnSeries());
  columnSeries.name = 'Value';
  columnSeries.dataFields.valueY = 'value';
  columnSeries.dataFields.categoryX = 'category';
  columnSeries.dataFields.categoryXValue = 'originalCategory';
  columnSeries.columns.template.events.on('hit', (ev) => {
    const eventTime = ev.target.dataItem.categoryXValue / 1000;
    const column = { linkField: null, field: null, additionalFilter: null };
    navigateToInvestigate(entityType, entityName, dataEntitiesIds[0], eventTime, null, column, brokerId);
  });
  columnSeries.columns.template.tooltipText = '[#fff font-size: 15px]{name} in {categoryX}: [/][#fff font-size: 20px]{valueY}[/] [#fff]{additional}[/]';
  columnSeries.columns.template.propertyFields.fill = 'color';
  columnSeries.columns.template.propertyFields.stroke = 'color';

  if (settings.hasBaselineData) {

    columnSeries.legendSettings.labelText = '[#0288D1 font-size: 15px]This SSL Subject[/]';

    chart.legend = new Am4charts.Legend();
    chart.legend.labels.template.fill = '#FFF';
    chart.legend.markers.template.disabled = true;
    /* Create series */
    const columnSeriesGlobal = chart.series.push(new Am4charts.ColumnSeries());
    columnSeriesGlobal.legendSettings.labelText = '[#757575 font-size: 15px]All SSL Subjects[/]';
    columnSeriesGlobal.dataFields.valueY = 'baselineData-value';
    columnSeriesGlobal.columns.template.tooltipText = '[#fff font-size: 15px]{name} in {categoryX}: [/][#fff font-size: 20px]{valueY}[/] [#fff]{additional}[/]';
    columnSeriesGlobal.dataFields.categoryX = 'category';
    columnSeriesGlobal.dataFields.categoryXValue = 'originalCategory';
    columnSeriesGlobal.tooltip.label.textAlign = 'middle';
    columnSeriesGlobal.columns.template.propertyFields.fill = 'baselineData-color';
    columnSeriesGlobal.columns.template.propertyFields.stroke = 'baselineData-color';
  }
  const scrollbarX = new Am4charts.XYChartScrollbar();
  chart.scrollbarX = scrollbarX;
  chart.scrollbarX.minHeight = 15;
  return chart;
};
