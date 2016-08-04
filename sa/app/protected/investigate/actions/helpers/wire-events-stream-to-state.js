import hasherizeEventMeta from './hasherize-event-meta';

/**
 * Attaches callbacks to a given stream that will update the properties of a given observable object. Namely, the
 * object's `status` and `reason` properties will be updated. Additionally, any records that are streamed in
 * will be appended to the object's `data` array.
 * @param {object} stream The stream object to attach callbacks to.
 * @param {object} state The state object whose properties will be written to.
 * @param {number} goal The stream will stop once the data size meets/exceeds this goal.
 * @param {number} [anchor=0] A basis for computing the progress of the stream, relative to goal.
 * @public
 */
export default function(stream, state, goal, anchor = 0) {
  let data = state.get('data');

  stream.subscribe({
    onNext(response) {
      let arr = response && response.data;
      if (arr) {
        arr.forEach(hasherizeEventMeta);
        data.pushObjects(arr);
        if (goal && data.length >= goal) {
          state.set('status', 'idle');
          stream.stop();
        }
      }
    },
    onError(response) {
      state.setProperties({
        status: 'error',
        reason: response && response.code
      });
    },
    onCompleted() {
      state.set('status', 'complete');
    }
  });

  state.setProperties({
    stream,
    status: 'streaming',
    reason: undefined,
    anchor,
    goal
  });

  stream.start();
}
