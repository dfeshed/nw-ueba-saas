/**
 * @file Drag Behavior utility
 * A configurable helper for detecting & handling user-initiated drag events in DOM.
 *
 * How to use:
 *
 * 1. Create an instance of this behavior utility. Pass in a hash of whatever `callbacks` you want, like so:
 * ```js
 * import DragBehavior from 'respond/utils/behaviors/drag';
 *
 * let myBehavior = DragBehavior.create({
 *  callbacks: {
 *    // These are functions that do whatever you want. Maybe they manipulate DOM, set object properties,
 *    // or fire redux actions. Doesn't matter, it's totally up to you & your use case.
 *    dragstart: ..,
 *    dragmove: ..,
 *    dragend: ..
 *  }
 * });
 * ```
 *
 * 2. Now have your Component call your behavior's `mouseDidDown()` whenever it hears a mousedown. That's it!
 *
 * ```js
 * // From a Component, you can just do this:
 * export default Component.extend({
 *  mouseDown(e) {
 *    myBehavior.mouseDidDown(e);
 *  }
 * });
 *
 * // Alternatively, if you want to use on an arbitrary DOM node, invoke with jQuery:
 * $('.my-drag-handle').on('mousedown', function(e) {
 *   myBehavior.mouseDidDown(e);
 * });
 * ```
 *
 * Note: We may eventually replace this utility with some 3rd party addon from the Ember community.
 *
 * @public
 */
import $ from 'jquery';

import EmberObject from '@ember/object';
import { run } from '@ember/runloop';

export default EmberObject.extend({

  /**
   * Configurable minimum limit of mousemove events that are required before starting a drag.
   * Setting this property to zero may cause the browser to confuse clicks as drag starts.
   *
   * @type {Number}
   * @public
   */
  minMouseMoves: 3,

  /**
   * Configurable hash of callbacks to be invoked by this object.
   *
   * The hash keys are event types; the hash values are callback functions.
   * Support event types are: 'dragstart', 'dragmove' & 'dragend'.
   *
   * This property should be set in order to wire up custom callbacks that respond to drag events.
   * The callbacks are optional. A callback that is not set will simply result in no callback for that
   * corresponding event.  If no callbacks are set, this object will effectively do nothing for the caller.
   *
   * @type {Object}
   * @default null
   * @public
   */
  callbacks: null,

  /**
   * Indicates whether or not a mousedown event has been detected without a subsequent mouseup event.
   * @type {Boolean}
   * @default false
   * @readOnly
   * @private
   */
  mouseIsDown: false,

  /**
   * Notifies this utility that a mousedown event has occurred, thus initializing various listeners.
   *
   * Call this method from your object's mouseDown handler in order to enable it to support drags.
   *
   * @param {object} e The DOM mousedown event object.
   * @public
   */
  mouseDidDown(e) {
    if (this.get('mouseIsDown')) {

      // An undetected mouseup must have occurred (e.g., outside the browser).
      // Abort any previous drag operation, and reset to start this new operation.
      this.teardown();
    }

    // Cache state.
    this.setProperties({
      mouseIsDown: true,
      mouseDownAt: [e.pageX, e.pageY],     // coords relative to entire doc, not some target element
      mouseMoveCounter: 0
    });

    // Track mousemoves and mouseup to distinguish a click from a drag.
    this.attachBodyListeners({
      mousemove: run.bind(this, 'mouseDidMove'),
      mouseup: run.bind(this, 'teardown')
    });
  },

  /**
   * Handler for mousemoves after the mousedown() method is called.
   * Responsible for distinguishing between a click and a drag start.
   *
   * Counts the mousemoves after a mousedown; once the count exceeds a certain minimum, then the operation is
   * considered a drag start; otherwise if a mouseup occurs before the minimum is met, the operation is considered
   * a click.
   *
   * @param {object} e The event object.
   * @private
   */
  mouseDidMove(e) {
    this.incrementProperty('mouseMoveCounter');
    if (this.get('mouseMoveCounter') >= this.get('minMouseMoves')) {

      // Initiate a drag.
      this.detachBodyListeners();
      this.dragDidStart(e);
    }
  },

  /**
   * Initializes listeners for mousemoves & mouseup during a drag.
   *
   * This method will automatically be called after a mousedown followed by a minimum number of mousemoves.
   *
   * @param {object} e The event object.
   * @private
   */
  dragDidStart(e) {
    this.set('isDragging', true);
    this.attachBodyListeners({
      mousemove: run.bind(this, 'dragDidMove'),
      mouseup: run.bind(this, 'dragDidEnd')
    });
    this.notify('dragstart', e);
  },

  /**
   * Handles dragmove events by notifying callbacks.
   *
   * @param {object} e The event object.
   * @private
   */
  dragDidMove(e) {
    const pos = [e.pageX, e.pageY];
    const origin = this.get('mouseDownAt');
    this.setProperties({
      mouseMoveAt: pos,
      delta: [pos[0] - origin[0], pos[1] - origin[1]]
    });
    this.notify('dragmove', e);
  },

  /**
   * Handles a mouseup after a drag has started, signaling the end of the drag.
   *
   * @param {object} e The event object.
   * @private
   */
  dragDidEnd(e) {
    this.set('mouseUpAt', [e.pageX, e.pageY]);
    this.notify('dragend', e);
    this.teardown();
  },

  /**
   * Cleans up state after a drag operation is finished or aborted.
   *
   * @private
   */
  teardown() {
    this.detachBodyListeners();
    this.setProperties({
      mouseIsDown: false,
      mouseDownAt: null,
      mouseMoveCounter: 0,
      isDragging: false
    });
  },

  /**
   * Attaches given set of DOM listeners to document.body.
   *
   * The given hash of listeners is then cached in a local property, so that they can easily
   * be detached later by calling `detachBodyListeners()`.
   *
   * @param {object} hash Collection of listeners, keyed by DOM event name.
   * @private
   */
  attachBodyListeners(hash) {
    $(document.body).on(hash);
    this.set('bodyListeners', hash);
  },

  /**
   * Detaches the last set of DOM listeners that were attached by `attachBodyListeners()`.
   *
   * Assumes the last set of DOM listeners can be found in a local property. Clears that property.
   * @private
   */
  detachBodyListeners() {
    const hash = this.get('bodyListeners') || {};
    $(document.body).off(hash);
    this.set('bodyListeners', null);
  },

  /**
   * Invokes the configurable notify for the given event (if any), passing in the event object.
   * @param {string} type The event type.
   * @param {object} e The event object.
   * @private
   */
  notify(type, e) {
    const callback = (this.get('callbacks') || {})[type];
    if ($.isFunction(callback)) {
      callback(e, this);
    }
  },

  destroy() {
    this.teardown();
    this._super();
  }
});
