/**
 * @file Time Range component
 * Displays the time range for an investigation.
 * Initially just a readonly display, but eventually may grow into an interactive time range picker.
 * @public
 */
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import Component from 'ember-component';

const stateToComputed = (state) => ({
  startTime: state.investigate.queryNode.startTime,
  endTime: state.investigate.queryNode.endTime
});

const TimeRangeComponent = Component.extend({
  classNames: 'rsa-investigate-time-range',

  @computed('startTime')
  _startTimeMilli(startTime) {
    return !startTime ? startTime : startTime * 1000;
  },

  @computed('endTime')
  _endTimeMilli(endTime) {
    return !endTime ? endTime : endTime * 1000;
  }

});

export default connect(stateToComputed)(TimeRangeComponent);