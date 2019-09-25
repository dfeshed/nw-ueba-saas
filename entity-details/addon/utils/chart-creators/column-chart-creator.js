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

  const title = chart.titles.create();
  title.text = `[bold white]${i18n.t(`investigateUsers.alerts.indicator.indicatorNames.${settings.title}.chartTitle`)}[/]`;
  title.fontSize = '1rem';
  title.marginTop = 10;

  /* Create value axis */
  const valueAxis = chart.yAxes.push(new Am4charts.ValueAxis());
  valueAxis.renderer.labels.template.fill = '#FFFFFF';
  valueAxis.title.text = `[bold white]${i18n.t(`investigateUsers.alerts.indicator.indicatorNames.${settings.title}.axisYtitle`)}[/]`;

  /* Create series */
  const lineSeries = chart.series.push(new Am4charts.LineSeries());
  lineSeries.name = 'Value';
  lineSeries.dataFields.valueY = 'value';
  lineSeries.tooltipText = '{valueY}';
  lineSeries.dataFields.categoryX = 'category';
  lineSeries.dataFields.categoryXValue = 'originalCategory';
  lineSeries.tooltip.label.textAlign = 'middle';
  const bullet = lineSeries.bullets.push(new Am4charts.Bullet());
  bullet.events.on('hit', (ev) => {
    const eventTime = ev.target.dataItem.categoryXValue / 1000;
    const column = { linkField: null, field: null, additionalFilter: null };
    navigateToInvestigate(entityType, entityName, dataEntitiesIds[0], eventTime, null, column, brokerId);
  });
  bullet.propertyFields.fill = 'color'; // tooltips grab fill from parent by default
  bullet.tooltipText = '[#fff font-size: 15px]{name} in {categoryX}:\n[/][#fff font-size: 20px]{valueY}[/] [#fff]{additional}[/]';
  const circle = bullet.createChild(Am4core.Circle);
  circle.propertyFields.radius = 'radius';
  circle.propertyFields.fill = 'color';
  circle.propertyFields.stroke = 'color';
  circle.strokeWidth = 2;

  if (settings.hasBaselineData) {
    /* Create series */
    const lineSeriesGlobal = chart.series.push(new Am4charts.LineSeries());
    lineSeriesGlobal.name = 'Baseline Value';
    lineSeriesGlobal.dataFields.valueY = 'baselineData-value';
    lineSeriesGlobal.tooltipText = '{valueY}';
    lineSeriesGlobal.dataFields.categoryX = 'baselineData-category';
    lineSeriesGlobal.dataFields.categoryXValue = 'baselineData-originalCategory';
    lineSeriesGlobal.tooltip.label.textAlign = 'middle';
    const bulletBase = lineSeriesGlobal.bullets.push(new Am4charts.Bullet());
    bulletBase.propertyFields.fill = 'baselineData-color'; // tooltips grab fill from parent by default
    bulletBase.tooltipText = '[#fff font-size: 15px]{name} in {categoryX}:\n[/][#fff font-size: 20px]{valueY}[/] [#fff]{additional}[/]';
    const circleBase = bulletBase.createChild(Am4core.Circle);
    circleBase.propertyFields.radius = 'radius';
    circleBase.propertyFields.fill = 'color';
    circleBase.propertyFields.stroke = 'color';
    circleBase.strokeWidth = 2;
  }
  const scrollbarX = new Am4charts.XYChartScrollbar();
  scrollbarX.series.push(lineSeries);
  chart.scrollbarX = scrollbarX;
  chart.scrollbarX.minHeight = 40;
  return chart;
};
