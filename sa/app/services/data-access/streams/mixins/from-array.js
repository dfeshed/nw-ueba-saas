/**
 * @file Stream.fromArray extension.
 * Enables a Stream class to instantiate a Stream using an Array as its data source.
 * Each array item (both pre-existing items and any items subsequently added later using `pushObject(s)`) will be
 * streamed to the stream's observers.
 * @public
 */
import Ember from 'ember';

const {
  Mixin,
  K
} = Ember;

export default Mixin.create({

  /**
   * The array whose items will be streamed to observers.
   * @type {[]}
   * @public
   */
  sourceArray: null,

  /**
   * If true, indicates that any new additions to the source Array will be streamed to observers.
   * Initially, before calling `.start()`, `isStreaming` is false. After calling `.start()`, `isStreaming` will change to
   * true and remain true until `.stop()` is called.
   * @type boolean
   * @readonly
   * @public
   */
  isStreaming: false,

  /**
   * Instantiates a new Stream object, with methods for submitting & cancelling a request for a websocket data stream.
   * @param {[]} arr The array whose items will be streamed.
   * @returns {object} This instance, for chaining.
   * @public
   */
  fromArray(arr) {
    this.set('sourceArray', arr);
    return this;
  },

  // Extend start by calling _startFromArray if we have a sourceArray.
  start() {
    if (!this.get('isStreaming') && this.get('sourceArray')) {
      this._startFromArray();
      this._startedFromArray = true;
    } else {
      this._super();
    }
  },

  // Extend stop by calling _stopFromArray if we are streaming a sourceArray.
  stop() {
    if (this._startedFromArray) {
      this._stopFromArray();
      this._startedFromArray = false;
    } else {
      this._super();
    }
  },

  /**
   * Streams all the source array's items to observes, then attaches listeners to source array to stream any subsequent
   * additions to the source array.
   * @returns {object} This instance, for chaining.
   * @private
   */
  _startFromArray() {
    let source = this.get('sourceArray');

    // If we are have already started, ignore & exit.
    if (this.get('isStreaming') || !source) {
      return this;
    }

    // Stream the source array's pre-existing items, if any.
    this.arrayDidChange(source, 0, 0, source.length);

    // Listen for future additions to sourceArray.
    source.addArrayObserver(this);

    this.set('isStreaming', true);
    return this;
  },

  /**
   * If the stream is streaming, detaches listeners from source Array; otherwise, exits successfully.
   * @returns {object} This instance, for chaining.
   * @private
   */
  _stopFromArray() {
    if (this.get('isStreaming')) {
      this.get('sourceArray').removeArrayObserver(this);
      this.set('isStreaming', false);
    }
    return this;
  },

  /**
   * Handler for additions to `sourceArray`.
   * This method will be notified after additions to `sourceArray` once
   * we call `sourceArray.addArrayObserver(this)`.  It is responsible for streaming any additions to this stream
   * instance's observers (via `this.next(..)`).
   * This method is also called manually one time when streaming begins, in order to stream all the array's initial items.
   * @see http://emberjs.com/api/classes/Ember.Array.html#method_addArrayObserver
   * @private
   */
  arrayDidChange(observedObj, start, removeCount, addCount) {
    if (addCount) {
      let me = this;
      observedObj.slice(start, start + addCount).forEach((item) => {
        me.next(item, me);
      });
    }
  },

  /**
   * Handler for additions to `sourceArray`. This method will be notified before additions to `sourceArray` once
   * we call `sourceArray.addArrayObserver(this)`.
   * @see http://emberjs.com/api/classes/Ember.Array.html#method_addArrayObserver
   * @private
   */
  arrayWillChange: K

});
