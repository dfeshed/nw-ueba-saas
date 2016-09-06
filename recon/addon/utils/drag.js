/**
 * @file Drag behavior
 * A configurable helper for detecting & handling user-initiated drag events in DOM.
 * @public
 */
import Ember from 'ember';
const { $, Object: EmberObject } = Ember;

export default EmberObject.extend({

  /**
   * Configurable hash of callbacks to be invoked by this object.
   * The hash keys are event types; the hash values are callback functions.
   * Support event types are: "dragstart", "dragmove" & "dragend".
   * This property should be set in order to wire up custom callbacks that respond to drag events.
   * The callbacks are optional. A callback that is not set will simply result in no callback for that
   * corresponding event.  If no callbacks are set, this object will effectively do nothing for the caller.
   * @type Object
   * @default null
   * @public
   */
  on: null,

  /**
   * Minimum number of mousemove events required to start a drag after a mousedown.
   * @type {number}
   * @public
   */
  dragBuffer: 3,

  /**
   * Indicates whether or not a mousedown event has been detected without a subsequent mouseup event.
   * @type Boolean
   * @default false
   * @readOnly
   * @public
   * @todo Consider implementing as an Ember.computed.readOnly
   */
  mouseIsDown: false,

  /**
   * Call this method from your object's mouseDown handler in order to enable it to support drags.
   * @param {object} e The event object.
   * @public
   */
  mousedown(e) {
    if (this.get('mouseIsDown')) {

      // An undetected mouseup must have occurred (e.g., outside the browser).
      // Abort any previous drag operation, and reset to start this new operation.
      this.teardown();
    }

    // Cache state.
    this.setProperties({
      mouseIsDown: true,
      mouseDownAt: [e.pageX, e.pageY],     // coords relative to doc, not some svg element
      mouseMoveCounter: 0
    });

    // Track mousemoves and mouseup to distinguish a click from a drag.
    this.attachBodyListeners({
      mousemove: this.mousemove.bind(this),
      mouseup: this.teardown.bind(this)
    });
  },

  /**
   * This method will automatically be called to listen for mousemoves after the mousedown() method is called.
   * Responsible for distinguishing between a click and a drag start. Counts the mousemoves after a mousedown; once
   * the count exceeds a certain minimum, then the operation is considered a drag start; otherwise if a mouseup occurs
   * before the minimum is met, the operation is considered a click.
   * @param {object} e The event object.
   * @private
   */
  mousemove(e) {
    this.incrementProperty('mouseMoveCounter');
    if (this.get('mouseMoveCounter') >= this.get('dragBuffer')) {

      // Initiate a drag.
      this.detachBodyListeners();
      this.dragstart(e);
    }
  },

  /**
   * This method will automatically be called after a mousedown followed by a minimum number of mousemoves.
   * Listens for mousemoves and mouseup during the drag.
   * @param {object} e The event object.
   * @private
   */
  dragstart(e) {
    this.set('isDragging', true);
    this.attachBodyListeners({
      mousemove: this.dragmove.bind(this),
      mouseup: this.dragend.bind(this)
    });
    this.hook('dragstart', e);
  },

  /**
   * This method will automatically be called when a mousmove is detected after a drag has started, signaling the
   * dragging object is moving.
   * @param {object} e The event object.
   * @private
   */
  dragmove(e) {
    let pos = [e.pageX, e.pageY];
    let origin = this.get('mouseDownAt');
    this.setProperties({
      mouseMoveAt: pos,
      delta: [pos[0] - origin[0], pos[1] - origin[1]]
    });
    this.hook('dragmove', e);
  },

  /**
   * This method will automatically be called when a mouseup is detected after a drag has started, signaling the
   * end of the drag.
   * @param {object} e The event object.
   * @private
   */
  dragend(e) {
    this.set('mouseUpAt', [e.pageX, e.pageY]);
    this.hook('dragend', e);
    this.teardown();
  },

  /**
   * Cleanup state after a drag operation is finished or aborted.
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
   * Attaches temporarily listeners to document.body. Used during drag.
   * @private
   */
  attachBodyListeners(hash) {
    $(document.body).on(hash);
    this.set('bodyListeners', hash);
  },

  /**
   * Detaches temporarily listeners to document.body. Used during drag.
   * @private
   */
  detachBodyListeners() {
    let hash = this.get('bodyListeners') || {};
    Object.keys(hash).forEach((eventName) => {
      let handler = hash[eventName];
      if (handler) {
        $(document.body).off(eventName, handler);
      }
    });
    this.set('bodyListeners', null);
  },

  /**
   * Invokes the configurable hook for the given event (if any), passing in the event object.
   * @param {string} type The event type.
   * @param {object} e The event object.
   * @private
   */
  hook(type, e) {
    let callback = (this.get('on') || {})[type];
    if (callback) {
      callback.apply(this, [e]);
    }
  }

  // @todo implement destroy, which should call teardown and reset 'on' to null
});
