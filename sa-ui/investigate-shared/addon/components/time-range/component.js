import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import layout from './template';

const TimeRangeComponent = Component.extend({
  layout,

  classNames: 'rsa-investigate-time-range',

  startTime: null,

  endTime: null,

  @computed('startTime')
  _startTimeMilli(startTime) {
    return !startTime ? startTime : startTime * 1000;
  },

  @computed('endTime')
  _endTimeMilli(endTime) {
    return !endTime ? endTime : endTime * 1000;
  }

});

export default TimeRangeComponent;
