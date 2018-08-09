import Component from '@ember/component';
import { connect } from 'ember-redux';

import { setService, setQueryTimeRange, setCustomTimeRange, setTimeRangeError } from 'investigate-events/actions/interaction-creators';

const stateToComputed = (state) => ({
  startTime: state.investigate.queryNode.startTime,
  endTime: state.investigate.queryNode.endTime,
  timeRangeInvalid: state.investigate.queryNode.timeRangeInvalid,
  services: state.investigate.services,
  serviceId: state.investigate.queryNode.serviceId
});

const dispatchToActions = {
  setService,
  setQueryTimeRange,
  setCustomTimeRange,
  setTimeRangeError
};

const QueryContainer = Component.extend({
  classNames: ['rsa-investigate-query-container', 'rsa-button-group'],
  tagName: 'nav'
});

export default connect(stateToComputed, dispatchToActions)(QueryContainer);
