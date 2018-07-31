import Component from '@ember/component';
import layout from './template';
import { connect } from 'ember-redux';

import { agentId } from 'recon/reducers/meta/selectors';

const stateToComputed = (state) => ({
  agentId: agentId(state)
});

const PivotToHostDetail = Component.extend({
  layout,
  classNames: ['pivot-to-host-details'],
  actions: {
    goToHostDetails() {
      const agentId = this.get('agentId');
      window.open(`${window.location.origin}/investigate/hosts?machineId=${agentId}&tabName=OVERVIEW`);
    }
  }
});

export default connect(stateToComputed)(PivotToHostDetail);
