import Component from '@ember/component';
import { connect } from 'ember-redux';
import { inject } from '@ember/service';
import computed from 'ember-computed-decorators';
import { getEventSourcesTotalEventRate } from 'ngcoreui/reducers/logcollector/dashboard-card/eventsources-card-selectors';
import * as dashboardCardSelectors from 'ngcoreui/reducers/logcollector/dashboard-card/dashboard-card-selectors';
import * as dashboardCardCreators from 'ngcoreui/actions/creators/logcollector/dashboard-card-creators';

const stateToComputed = (state) => ({
  logCollectorInEPS: dashboardCardSelectors.getLogCollectorTotalEventRate(state),
  logCollectorOutEPS: getEventSourcesTotalEventRate(state),
  tcpRate: dashboardCardSelectors.getTCPRate(state)
});

const logCollectorChart = Component.extend({
  redux: inject(),

  label: null,
  animate: true,

  tagName: 'vbox',
  classNames: ['dashboard-card', 'border-panel'],

  domainExtents: {
    y: { fixed: [ 0, 100 ] }
  },

  LCInEPSdata: Array(30).fill('0'),
  LCOutEPSdata: Array(30).fill('0'),
  tcpRatedata: Array(30).fill('0'),

  @computed('logCollectorInEPS', 'logCollectorOutEPS', 'tcpRate')
  getLogCollectorEPS(logCollectorInEPS, logCollectorOutEPS, tcpRate) {
    const dataList = [];

    this.LCInEPSdata.push(logCollectorInEPS);
    this.LCInEPSdata = this.LCInEPSdata.slice(-30);
    const pointsArrIn = dashboardCardSelectors.getPointsArray(this.LCInEPSdata);
    dataList.push(pointsArrIn);

    this.LCOutEPSdata.push(logCollectorOutEPS);
    this.LCOutEPSdata = this.LCOutEPSdata.slice(-30);
    const pointsArrOut = dashboardCardSelectors.getPointsArray(this.LCOutEPSdata);
    dataList.push(pointsArrOut);

    this.tcpRatedata.push(tcpRate);
    this.tcpRatedata = this.tcpRatedata.slice(-30);
    const tcpPoints = dashboardCardSelectors.getPointsArray(this.tcpRatedata);
    dataList.push(tcpPoints);

    return dataList;
  },

  didInsertElement() {
    this._super(...arguments);
    this.get('redux').dispatch(dashboardCardCreators.startStreamingTcpRate());
  },

  willDestroy() {
    this.get('redux').dispatch(dashboardCardCreators.stopStreamingTcpRate());
  }
});

export default connect(stateToComputed)(logCollectorChart);
