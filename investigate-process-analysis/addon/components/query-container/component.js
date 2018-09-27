import Component from '@ember/component';
import { connect } from 'ember-redux';
import { setSelectedService, setQueryTimeRange } from 'investigate-process-analysis/actions/creators/services-creators';

const dispatchToActions = {
  setSelectedService,
  setQueryTimeRange
};

const stateToComputed = (state) => ({
  services: state.processAnalysis.services,
  serviceId: state.processAnalysis.query.serviceId,
  startTime: state.processAnalysis.query.startTime,
  endTime: state.processAnalysis.query.endTime
});

const QueryContainer = Component.extend({

  tagName: 'hbox',

  classNames: 'query-container',

  timeRangeInvalid: false,

  actions: {
    timeRangeError() {
      this.set('timeRangeInvalid', true);
    },

    timeRangeSelection(range) {
      this.set('timeRangeInvalid', false);
      this.send('setQueryTimeRange', range);
    },

    customTimeRange(start, end) {
      this.set('timeRangeInvalid', false);
      this.send('setQueryTimeRange', { startTime: start, endTime: end }, true);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(QueryContainer);
