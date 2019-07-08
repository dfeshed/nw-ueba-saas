import Component from '@ember/component';
import { COLUMNS_CONFIG } from './columnsConfig';
import { connect } from 'ember-redux';
import { inject } from '@ember/service';
import { protocolArray } from 'ngcoreui/reducers/logcollector/dashboard-card/dashboard-card-selectors';
import { initializeProtocols } from 'ngcoreui/actions/creators/logcollector/dashboard-card-creators';

const stateToComputed = (state) => ({
  protocolList: protocolArray(state)
});

const cardLC = Component.extend({
  redux: inject(),

  didInsertElement() {
    this._super(...arguments);
    this.get('redux').dispatch(initializeProtocols());
  },

  classNames: ['card-lc', 'border-panel-lc'],
  columns: COLUMNS_CONFIG
});

export default connect(stateToComputed)(cardLC);
