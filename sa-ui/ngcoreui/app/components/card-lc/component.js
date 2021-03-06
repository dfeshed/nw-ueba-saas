import Component from '@ember/component';
import { COLUMNS_CONFIG } from './columnsConfig';
import { connect } from 'ember-redux';
import { inject } from '@ember/service';
import * as dashboardCardSelectors from 'ngcoreui/reducers/logcollector/dashboard-card/dashboard-card-selectors';
import * as dashboardCardCreators from 'ngcoreui/actions/creators/logcollector/dashboard-card-creators';

const stateToComputed = (state) => ({
  protocolDataList: dashboardCardSelectors.getLogCollectorData(state)
});

const cardLC = Component.extend({
  redux: inject(),

  didInsertElement() {
    this._super(...arguments);
    this.get('redux').dispatch(dashboardCardCreators.refreshProtocols());
    this.refreshIntervalId = setInterval(() => this.get('redux').dispatch(dashboardCardCreators.refreshProtocols()),
      5000);
  },

  willDestroyElement() {
    clearInterval(this.refreshIntervalId);
    this._super(...arguments);
  },

  classNames: ['card-lc', 'border-panel-lc'],
  columns: COLUMNS_CONFIG
});

export default connect(stateToComputed)(cardLC);
