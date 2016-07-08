/**
 * @file Stream.toArray extension.
 * Enables a Stream instance to populate an array with its emitted responses.
 * Assumes that either responses contain payloads in `response.data` or they are the payload themselves.
 * @public
 */
import Ember from 'ember';

export default Ember.Mixin.create({

  /**
   * Specifies which array to stream data to.
   * Subscribes the given array to this stream, with a default callback that will copy the stream's properties
   * `count`, `total` & `progress` to the array, and then append the response payload(s) to the array's content.
   * @param {array} [arr] Optional array to be streamed to. If none given, a new array is created.
   * @returns {object} New subscription object for the array.
   * @public
   */
  toArray(arr) {
    arr = arr || [];
    return this.subscribe({
      array: arr,
      onNext(response, stream) {
        if (stream) {
          arr.setProperties(
            stream.getProperties('count', 'goal', 'progress', 'total', 'errorCode', 'page')
          );
        }
        let payload = ((typeof response === 'object') && response.hasOwnProperty('data')) ?
          response.data : response;
        payload = Ember.isArray(payload) ? payload : [payload];
        arr.pushObjects(payload);
      }
    });
  }
});
