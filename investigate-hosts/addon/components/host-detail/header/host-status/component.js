import Component from '@ember/component';
import { connect } from 'ember-redux';
import { lastScanTime, hostWithStatus } from 'investigate-hosts/reducers/details/overview/selectors';
import { setSelectedHost } from 'investigate-hosts/actions/ui-state-creators';
import { isInsightsAgent } from 'investigate-hosts/reducers/hosts/selectors';

const stateToComputed = (state) => ({
  hostDetails: hostWithStatus(state),
  lastScanTime: lastScanTime(state),
  isInsightsAgent: isInsightsAgent(state)
});

const dispatchToActions = {
  setSelectedHost
};

const HostStatus = Component.extend({

  tagName: 'hbox',

  classNames: ['host-overview host-item flexi-fit col-xs-12']

});

export default connect(stateToComputed, dispatchToActions)(HostStatus);
