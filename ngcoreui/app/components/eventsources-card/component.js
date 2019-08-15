import Component from '@ember/component';
import { COLUMNS_CONFIG } from './columnsConfig';
import { connect } from 'ember-redux';
import { inject } from '@ember/service';
import * as eventSourcesCardSelectors from 'ngcoreui/reducers/logcollector/dashboard-card/eventsources-card-selectors';
import { refreshProtocols } from 'ngcoreui/actions/creators/logcollector/eventsources-card-creators';

const stateToComputed = (state) => ({
  eventSourcesStatsDataList: eventSourcesCardSelectors.getEventSourcesStatsData(state)
});

const esStatsCard = Component.extend({
  redux: inject(),

  classNames: ['card-lc', 'border-panel-lc'],
  columns: COLUMNS_CONFIG,

  didInsertElement() {
    this._super(...arguments);
    this.get('redux').dispatch(refreshProtocols());
    this.refreshIntervalId =
      setInterval(() => this.get('redux').dispatch(refreshProtocols()), 5000);
  },

  willDestroyElement() {
    clearInterval(this.refreshIntervalId);
    this._super(...arguments);
  }
});

export default connect(stateToComputed)(esStatsCard);
