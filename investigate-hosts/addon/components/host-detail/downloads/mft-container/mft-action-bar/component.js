import Component from '@ember/component';
import { inject as service } from '@ember/service';
import { toggleMftView } from 'investigate-hosts/actions/data-creators/downloads';
import { connect } from 'ember-redux';
import { alias } from 'ember-computed-decorators';

const stateToComputed = (state) => ({
  focusedHost: state.endpoint.detailsInput.agentId,
  serverId: state.endpointQuery.serverId
});

const dispatchToActions = {
  toggleMftView
};

const mftActionBar = Component.extend({
  tagName: 'box',
  classNames: ['mft-action-bar'],
  accessControl: service(),
  @alias('focusedHost')
  machineId: null
});
export default connect(stateToComputed, dispatchToActions)(mftActionBar);