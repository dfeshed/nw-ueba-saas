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
  _endTimeMilli: computedMillisecFromSec('endTime'),

  // Computes whether or not `startTime` and `endTime` point to the same calendar date (time is ignored).
  _datesMatch: computed('_startTimeMilli', '_endTimeMilli', function() {
    let st = this.get('_startTimeMilli');
    let et = this.get('_endTimeMilli');
    if (!st || !et) {
      return false;
    }
    st = new Date(st);
    et = new Date(et);

    return (st.getUTCDate() === et.getUTCDate()) &&
      (st.getUTCMonth() === et.getUTCMonth()) &&
      (st.getUTCFullYear() === et.getUTCFullYear());
  })

});
