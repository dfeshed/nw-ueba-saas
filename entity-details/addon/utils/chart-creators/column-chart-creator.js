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
  /* Create axes */
  const categoryAxis = chart.xAxes.push(new Am4charts.CategoryAxis());
  categoryAxis.dataFields.category = 'category';
  categoryAxis.renderer.minGridDistance = 100;
  categoryAxis.renderer.labels.template.fill = '#FFFFFF';

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
  columnSeries.columns.template.width = 5;

  const scrollbarX = new Am4charts.XYChartScrollbar();
  scrollbarX.series.push(columnSeries);
  chart.scrollbarX = scrollbarX;
  chart.scrollbarX.minHeight = 40;
  return chart;
};
