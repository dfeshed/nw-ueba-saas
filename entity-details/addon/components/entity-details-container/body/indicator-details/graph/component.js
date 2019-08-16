import Component from '@ember/component';
import { connect } from 'ember-redux';
import am4themesAnimated from '@amcharts/amcharts4/themes/animated';
import computed from 'ember-computed-decorators';
import * as am4core from '@amcharts/amcharts4/core';
import { inject as service } from '@ember/service';
import {
  indicatorMapSettings,
  getIncidentData,
  historicalData,
  brokerId,
  indicatorGraphError
} from 'entity-details/reducers/indicators/selectors';
import pieChartCreator from 'entity-details/utils/chart-creators/pie-chart-creator';
import columnChartCreator from 'entity-details/utils/chart-creators/column-chart-creator';
import heatmapChartCreator from 'entity-details/utils/chart-creators/heatmap-chart-creator';
import chartDataAdapter from 'entity-details/utils/chart-data-adapter';

am4core.useTheme(am4themesAnimated);

const stateToComputed = (state) => ({
  indicatorMapSettings: indicatorMapSettings(state),
  incidentDetails: getIncidentData(state),
  brokerId: brokerId(state),
  historicalData: historicalData(state),
  indicatorGraphError: indicatorGraphError(state)
});

const EventsGraphComponent = Component.extend({
  classNames: ['entity-details-container-body-indicator-details_graph'],
  timezone: service(),
  i18n: service(),

  init() {
    this._super(...arguments);
    this.chart = null;
  },

  @computed('indicatorMapSettings', 'historicalData')
  historicalDataForGraph(indicatorMapSettings, historicalData) {
    // Init will not be called when user jumps between indicators. So need to clear container div before drawing chart.
    this.chart = null;
    const timeZoneId = this.get('timezone.selected.zoneId') || 'UTC';
    const locale = this.get('i18n.locale') || 'en';
    document.getElementById('chartComponentPlaceholder').innerHTML = '';
    if (!indicatorMapSettings || !historicalData) {
      return;
    }
    const chartType = indicatorMapSettings.chartSettings.type;
    const settings = chartDataAdapter(indicatorMapSettings, historicalData, timeZoneId, locale);
    switch (chartType) {
      case 'pie':
        this.chart = pieChartCreator(settings, this.get('incidentDetails'), this.get('brokerId'));
        break;
      case 'column':
        this.chart = columnChartCreator(indicatorMapSettings, this.get('incidentDetails'), this.get('brokerId'));
        break;
      case 'heatmap':
        this.chart = heatmapChartCreator(indicatorMapSettings, this.get('incidentDetails'), this.get('brokerId'));
        break;
    }
    return true;
  }
});

export default connect(stateToComputed)(EventsGraphComponent);
