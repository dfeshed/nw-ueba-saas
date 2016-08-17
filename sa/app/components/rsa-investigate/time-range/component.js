/**
 * @file Time Range component
 * Displays the time range for an investigation.
 * Initially just a readonly display, but eventually may grow into an interactive time range picker.
 * @public
 */
import Ember from 'ember';

const { computed, Component } = Ember;

function computedMillisecFromSec(propName) {
  return computed(propName, function() {
    let time = this.get(propName);
    return !time ? time : time * 1000;
  });
}

export default Component.extend({
  classNames: 'rsa-investigate-time-range',

  /**
   * Starting timestamp value, in UTC seconds.
   * @type {number}
   * @public
   */
  startTime: undefined,

  /**
   * Ending timestamp value, in UTC seconds.
   * @type {number}
   * @public
   */
  endTime: undefined,

  // Computes query's `startTime` in millisec, so it can be formatted by moment js.
  _startTimeMilli: computedMillisecFromSec('startTime'),

  // Computes query's `startTime` in millisec, so it can be formatted by moment js.
  _endTimeMilli: computedMillisecFromSec('endTime')
});
