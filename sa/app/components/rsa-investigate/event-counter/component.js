import Ember from 'ember';
import safeCallback from 'sa/utils/safe-callback';

const { computed, Component } = Ember;

export default Component.extend({
  tagName: 'span',
  classNames: 'rsa-investigate-event-counter',
  classNameBindings: ['status'],

  /**
   * The number to be displayed, if it is available.
   * @type {number}
   * @public
   */
  count: undefined,

  /**
   * Status of the request to fetch this count. Could be any of the `status` values defined for streaming or promised
   * requests, such as: either 'wait', 'resolved', 'rejected', 'streaming', 'idle', 'error' or 'complete'.
   * @type {string}
   * @public
   */
  status: undefined,

  /**
   * Configurable action to be invoked when user clicks Go button.  Responsible for fetching this count value.
   * If this action is not specified, then the Go button will not be shown.
   * @type {function}
   * @public
   */
  goAction: undefined,

  /**
   * Configurable action to be invoked when user clicks Stop button.  Responsible for cancelling the fetch of this count value.
   * If this action is not specified, then the Stop button will not be shown.
   * @type {function}
   * @public
   */
  stopAction: undefined,

  /**
   * Configurable action to be invoked when user clicks Retry button. Responsible for retrying to fetch this count value
   * after an error has occurred.  If this action is not given, the Retry button is hidden.
   * @type {function}
   * @public
   */
  retryAction: undefined,

  // Computes to true if `status` is set to a value that indicates that this count is being actively being fetched.
  _isStatusBusy: computed('status', function() {
    return !!String(this.get('status')).match(/wait|streaming/);
  }),

  // Computes to true if `status` is set to a value that indicates that an error occurred fetching this count.
  _isStatusError: computed('status', function() {
    return !!String(this.get('status')).match(/error|rejected/);
  }),

  // Computes to true if `status` is set to a value that indicates that the count is final.
  _isStatusFinished: computed('status', function() {
    return !!String(this.get('status')).match(/resolved|complete/);
  }),

  actions: {
    safeCallback
  }
});
