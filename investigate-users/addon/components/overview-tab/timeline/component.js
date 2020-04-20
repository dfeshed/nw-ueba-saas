import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getAlertsForTimeline, getFilter, alertsForTimelineError } from 'investigate-users/reducers/alerts/selectors';
import moment from 'moment';
import computed from 'ember-computed-decorators';
import { next } from '@ember/runloop';
import { updateFilter } from 'investigate-users/actions/alert-details';
import * as Am4core from '@amcharts/amcharts4/core';
import * as Am4charts from '@amcharts/amcharts4/charts';
import { lookup } from 'ember-dependency-lookup';

const stateToComputed = (state) => ({
  filter: getFilter(state),
  alerts: getAlertsForTimeline(state),
  alertsForTimelineError: alertsForTimelineError(state)
});

const dispatchToActions = {
  updateFilter
};


const OverviewAlertTimelineComponent = Component.extend({

  classNames: 'user-overview-tab_alerts_timeline',

  _renderAlertsTimeLine(data) {
    const i18n = lookup('service:i18n');
    const _that = this;

    // Create chart instance
    // Themes end
    const chart = Am4core.create('chartdivForAlerts', Am4charts.XYChart);
    // Add data
    chart.data = data;

    const title = chart.titles.create();
    title.text = `${i18n.t('investigateUsers.alerts.all')}`;
    title.align = 'left';
    // Create axes
    const categoryAxis = chart.xAxes.push(new Am4charts.CategoryAxis());
    categoryAxis.dataFields.category = 'day';

    const valueAxis = chart.yAxes.push(new Am4charts.ValueAxis());
    valueAxis.renderer.labels.template.disabled = true;
    valueAxis.min = 0;

    // Create series
    function createSeries(field, name, fillColor) {
      // Set up series
      const series = chart.series.push(new Am4charts.ColumnSeries());
      series.name = name;
      series.dataFields.valueY = field;
      series.dataFields.categoryX = 'day';
      series.sequencedInterpolation = true;

      // Make it stacked
      series.stacked = true;
      // Configure columns
      series.columns.template.width = Am4core.percent(10);
      series.columns.template.fill = fillColor;
      series.columns.template.tooltipText = '[bold white font-size:14px]{valueY} {name} Alerts raised on {categoryX.formatDate(\'dd-MMM\')}';
      series.columns.template.events.on('hit', (ev) => {
        const severity = ev.target.dataItem.component.dataFields.valueY;
        const dateTime = ev.target.dataItem.dataContext.originalTime;
        _that.applyFilter(severity, dateTime);
      });
      return series;
    }

    createSeries('Critical', 'Critical', '#C91818');
    createSeries('High', 'High', '#E64A19');
    createSeries('Medium', 'Medium', '#FFC107');
    createSeries('Low', 'Low', '#689F38');
    // Legend
    chart.legend = new Am4charts.Legend();
    chart.legend.position = 'top';
    chart.legend.contentAlign = 'right';
    const markerTemplate = chart.legend.markers.template;
    markerTemplate.width = 15;
    markerTemplate.height = 15;
    markerTemplate.strokeOpacity = 0;
    chart.paddingBottom = 15;
  },

  applyFilter(severity, dateTime) {
    this.send('updateFilter', null, true);
    this.get('applyAlertsFilter')(this.get('filter').merge({
      severity: [severity],
      alert_start_range: `${dateTime},${moment(dateTime).add(1, 'days').unix() * 1000}`,
      showCustomDate: true
    }));
  },

  @computed('alerts')
  alertsTimeline(alerts) {
    if (alerts && alerts.length > 0) {
      next(this, () => {
        this._renderAlertsTimeLine(alerts);
      });
      return true;
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(OverviewAlertTimelineComponent);