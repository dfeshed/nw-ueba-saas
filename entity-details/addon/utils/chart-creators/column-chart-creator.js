/**
 * This util will returns AM chart object for Column Chart and render the same.
 * @private
 */

import * as Am4core from '@amcharts/amcharts4/core';
import * as Am4charts from '@amcharts/amcharts4/charts';

export default (settings) => {
  const chart = Am4core.create('chartdiv', Am4charts.XYChart);
  chart.data = settings.chartSettings.dataProvider;
  /* Create axes */
  const categoryAxis = chart.xAxes.push(new Am4charts.CategoryAxis());
  categoryAxis.dataFields.category = 'category';
  categoryAxis.renderer.minGridDistance = 10;
  categoryAxis.dateFormatter = new Am4core.DateFormatter();
  categoryAxis.dateFormatter.dateFormat = 'MM-dd';

  /* Create value axis */
  chart.yAxes.push(new Am4charts.ValueAxis());

  /* Create series */
  const columnSeries = chart.series.push(new Am4charts.ColumnSeries());
  columnSeries.name = 'Value';
  columnSeries.dataFields.valueY = 'value';
  columnSeries.dataFields.categoryX = 'category';

  columnSeries.columns.template.tooltipText = '[#fff font-size: 15px]{name} in {categoryX}:\n[/][#fff font-size: 20px]{valueY}[/] [#fff]{additional}[/]';
  columnSeries.columns.template.propertyFields.fillOpacity = 'fillOpacity';
  columnSeries.columns.template.propertyFields.stroke = 'stroke';
  columnSeries.columns.template.propertyFields.strokeWidth = 'strokeWidth';
  columnSeries.columns.template.propertyFields.strokeDasharray = 'columnDash';
  columnSeries.tooltip.label.textAlign = 'middle';

  const scrollbarX = new Am4charts.XYChartScrollbar();
  scrollbarX.series.push(columnSeries);
  chart.scrollbarX = scrollbarX;
  return chart;
};
