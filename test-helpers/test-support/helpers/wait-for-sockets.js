import { registerWaiter } from '@ember/test';
import { Socket } from 'streaming-data/services/data-access';

const originalCreateStreamFunc = Socket.createStream;

const revertPatch = (connections) => {
  Socket.createStream = originalCreateStreamFunc;
  if (connections !== 0) {
    throw new Error(`Socket was unpatched but <${connections}> open connections were found`);
  }
};

const updateCounter = (excluded, method) => {
  return function(mutation) {
    if (!excluded.includes(method)) {
      mutation();
    }
  };
};

export function waitForSockets(excluding) {
  if (Socket.createStream !== originalCreateStreamFunc) {
    throw 'A previous call to waitForSockets() never reverted the patch after the test was completed. To fix this, call the function returned from this method after your test is completed';
  }

  let counter = 0;
  registerWaiter(() => counter === 0);

  const decrementStop = function(stream, updateFunc) {
    const origStop = stream.stop;
    stream.stop = function() {
      updateFunc(() => counter -= 1);

      let applied;
      try {
        applied = origStop.apply(this, arguments);
      } finally {
        stream.stop = origStop;
      }
      return applied;
    };
    return stream;
  };

  const excluded = excluding || ['stream'];
  Socket.createStream = function(method) {
    const updateFunc = updateCounter(excluded, method);
    updateFunc(() => counter += 1);
    return decrementStop(originalCreateStreamFunc.apply(this, arguments), updateFunc);
  };
  return revertPatch.bind(null, counter);
}
