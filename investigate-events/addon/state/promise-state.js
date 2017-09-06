/**
 * @file Promise State class
 * Wraps a promise into an Ember Object so that the promise's state and resolved/rejected value can be observed.
 * @public
 */
import Ember from 'ember';

const { Object: EmberObject } = Ember;

export default EmberObject.extend({
  /**
   * Either undefined (promise hasn't been executed yet), 'wait' (promise is in progress), 'resolved' or 'rejected'.
   * @type {string}
   * @public
   */
  status: undefined,

  /**
   * The data that the promise resolves with, if any.
   * @type {*}
   * @public
   */
  data: undefined,

  /**
   * The error reason that the promise rejects with, if any.
   * @type {*}
   * @public
   */
  reason: undefined,

  /**
   * Resets state data. Used for deallocating memory.
   * @public
   */
  reset() {
    this.setProperties({
      status: undefined,
      data: undefined,
      reason: undefined
    });
  }
});
