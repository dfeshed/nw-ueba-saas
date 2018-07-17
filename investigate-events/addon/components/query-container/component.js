import Component from '@ember/component';
import { connect } from 'ember-redux';

import { setQueryTimeRange, setService } from 'investigate-events/actions/interaction-creators';

const stateToComputed = (state) => ({
  startTime: state.investigate.queryNode.startTime,
  endTime: state.investigate.queryNode.endTime,
  services: state.investigate.services,
  serviceId: state.investigate.queryNode.serviceId
});

const dispatchToActions = {
  setQueryTimeRange,
  setService
};

const QueryContainer = Component.extend({
  classNames: ['rsa-investigate-query-container', 'rsa-button-group'],
  tagName: 'nav'
});

export default connect(stateToComputed, dispatchToActions)(QueryContainer);
