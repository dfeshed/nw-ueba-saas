/**
 * @file Stream State class
 * Wraps a stream into an Ember Object so that the stream's status, progress and collated responses can be observed.
 * @public
 */
import Ember from 'ember';

const { $, computed, Object: EmberObject } = Ember;

export default EmberObject.extend({
  /**
   * Either 'idle', 'streaming', 'complete' or 'error'.
   * @type {string}
   * @public
   */
  status: 'idle',

  /**
   * Running list of data objects returned from the stream.
   * @type {object[]}
   * @public
   */
  data: computed(() => {
    return [];
  }),

  /**
   * The error reason, if any.
   * @type {*}
   * @public
   */
  reason: undefined,

  /**
   * If known, the `data` length before this stream began.
   * Used for computing `percent`.
   * @type {number}
   * @public
   */
  anchor: 0,

  /**
   * If known, the `data` length that we are expecting to reach once this stream request is finished responding.
   * Used for computing `percent`.
   * @type {number}
   * @public
   */
  goal: 0,

  /**
   * A computed percentage (integer) that represents `data`'s length, relative to `anchor` and `goal`.
   * @type {number}
   * @public
   */
  percent: computed('anchor', 'goal', 'data.length', 'status', function() {
    if (this.get('status') === 'complete') {
      return 100;
    }
    let { anchor, goal } = this.getProperties('anchor', 'goal');
    let spread = goal - anchor;
    let len = this.get('data.length') || 0;
    if (spread && $.isNumeric(spread)) {
      return parseInt(100 * (len - anchor) / spread, 10);
    } else {
      return 0;
    }
  }),

  /**
   * Resets state data. Used for deallocating memory.
   * @public
   */
  reset() {
    this.setProperties({
      status: 'idle',
      data: [],
      reason: undefined,
      anchor: 0,
      goal: 0
    });
  }
});
