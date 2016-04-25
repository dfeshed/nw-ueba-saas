/**
 * @file Thread class
 * Ember-friendly alternative to using window.setInterval, using Ember.run.later instead.
 * Exposes an API for invoking a configurable function repeatedly, each time passing in an argument from
 * a configurable queue. The pause in between each invocation is configurable, as is the number of invocations
 * in between each pause.
 * @public
 */
import Ember from 'ember';

export default Ember.Object.extend({

  /**
   * Pause (in millisec) between incremental calls of the `next` callback.
   * If zero or invalid, `next` will be called for all items in the queue in a single synchronous batch.
   * @type number
   * @public
   */
  interval: 0,

  /**
   * Maximum number of callbacks to `next` before a pause.
   * If zero or invalid, `next` will be called for all items in the queue in a single synchronous batch.
   * @type number
   * @public
   */
  rate: 0,

  /**
   * Optional pause (in millisec) before the first call of the `next` callback once `start` is called.
   * If zero or invalid, `next` will be called immediately (synchronously) once `start` is called.
   * @type number
   * @public
   */
  delay: 0,

  /**
   * Array of data to be passed into `next` callbacks; one per each invocation.
   * @type [*]
   * @public
   */
  queue: null,

  /**
   * Configurable callback to be invoked repeatedly by this thread. Each call to `next` will be passed in
   * the next entry from `queue`, which is taken from the start of `queue` (i.e., index = 0).
   * @type function
   * @public
   */
  onNext: null,

  /**
   * Configurable callback to be invoked repeatedly by this thread. Each call to `nextBatch` will be passed in
   * the next batch of entries from `queue`, which are taken from the start of `queue` (i.e., index = 0).
   * Unlike `next`, `nextBatch` receives an array of entries in order to support bulk processing.
   * @type function
   * @public
   */
  onNextBatch: null,

  /**
   * Configurable callback to be invoked once this thread has finished iterating through its `queue`.
   * If the thread is stopped, this callback will not be invoked.
   * @type function
   * @public
   */
  onCompleted: null,

  /**
   * Triggers the start of callbacks to `next`.
   * @public
   */
  start() {

    // Performance optimization: Cache frequently used vars.
    this._onNext = this.get('onNext');
    this._onNextBatch = this.get('onNextBatch');
    this._onCompleted = this.get('onCompleted');
    this._queue = this.get('queue') || [];
    if ((!this._onNext && !this._onNextBatch) || !this._queue) {
      return;
    }
    this._interval = parseInt(this.get('interval'), 10) || 0;
    this._rate = (!this._interval ? 0 : parseInt(this.get('rate'), 10)) || this._queue.length;

    // Kick off the thread.
    let delay = parseInt(this.get('delay'), 10) || 0;
    if (delay) {
      this._timer = Ember.run.later(this, '_step', delay);
    } else {
      this._step();
    }
  },

  /**
   * Invokes `next` callback for the first `rate` number of items in `queue`. If more items remain in `queue`,
   * sets an Ember timeout to repeat this function later after `interval` milliseconds.
   * @private
   */
  _step() {
    let batch = this._queue.splice(0, this._rate);

    // Call next for each member of batch.
    if (this._onNext) {
      batch.map(this._onNext);
    }

    // Call nextBatch for the entire batch.
    if (this._onNextBatch) {
      this._onNextBatch(batch);
    }

    if (this._queue.length) {
      this._timer = Ember.run.later(this, '_step', this._interval);
    } else {
      if (this._onCompleted) {
        this._onCompleted();
      }
    }
  },

  /**
   * Stops the callbacks to `next`.
   * @public
   */
  stop() {
    if (this._timer) {
      Ember.run.cancel(this._timer);
      this._timer = null;
    }
  }
});
