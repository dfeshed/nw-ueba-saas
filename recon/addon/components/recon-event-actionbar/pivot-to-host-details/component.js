import Component from '@ember/component';
import layout from './template';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';

import { agentId, endpointServiceId } from 'recon/reducers/meta/selectors';

const stateToComputed = (state) => ({
  agentId: agentId(state),
  sid: endpointServiceId(state)
});

const PivotToHostDetail = Component.extend({
  layout,
  classNames: ['pivot-to-host-details'],

  @computed('agentId')
  isDisabled(agentId) {
    return !agentId;
  },

  actions: {
    goToHostDetails() {
      const { agentId, sid } = this.getProperties('agentId', 'sid');
      window.open(`${window.location.origin}/investigate/hosts/${agentId}?machineId=${agentId}&sid=${sid}&tabName=OVERVIEW`);
    }
  }
});

export default connect(stateToComputed)(PivotToHostDetail);
