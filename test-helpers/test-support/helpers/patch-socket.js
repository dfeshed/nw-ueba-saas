import { Promise, all } from 'rsvp';
import { registerWaiter } from '@ember/test';
import { Socket } from 'streaming-data/services/data-access';

const originalCreateStreamFunc = Socket.createStream;

function waitForStop(stream) {
  let counter = 0;
  registerWaiter(() => counter === 0);
  counter += 1;

  const origStop = stream.stop;
  const origCompleted = stream.completed;

  // patches the original stop/completed functions to adjust the wait counter
  const handle = function(originalFunc) {
    return function() {
      counter -= 1;
      let applied;
      try {
        applied = originalFunc.apply(this, arguments);
      } finally {
        // since we don't know which original function was replaced, restore them both
        stream.stop = origStop;
        stream.completed = origCompleted;
      }
      return applied;
    };
  };
  // Promise requests get stopped (i.e., stream.stop is called when they are done)
  stream.stop = handle(stream.stop);
  // Stream requests get completed (i.e., stream.completed is called with they are done)
  stream.completed = handle(stream.completed);

  return stream;
}

function waitForError(stream) {
  let counter = 0;
  registerWaiter(() => counter === 0);
  counter += 1;

  const origError = stream.error;
  stream.error = function() {
    counter -= 1;
    let applied;
    try {
      applied = origError.apply(this, arguments);
    } finally {
      stream.error = origError;
    }
    return applied;
  };
  return stream;
}

export function patchSocket(callback) {
  const origFunc = Socket.createStream;
  Socket.createStream = function(method, modelName, query) {
    try {
      callback(method, modelName, query);
    } finally {
      Socket.createStream = origFunc;
    }
    return waitForStop(origFunc.apply(this, arguments));
  };
}

export function revertPatch() {
  Socket.createStream = originalCreateStreamFunc;
}

/**
 * Patches the websocket createStream to simulate an exception. If the method/modelName of the specific websocket configuration
 * is passed in (optional arguments), then the exception is only thrown for that method/modelName (e.g., stream/alerts),
 * otherwise, the first socket connection request gets the exception
 * @method throwSocket
 * @param methodToThrow (optional)
 * @param modelNameToThrow (optional)
 * @param message (optional)
 * @returns function revertPatch() function for removing the patch in case it was never called during the test
 * @public
 */
export function throwSocket({ methodToThrow, modelNameToThrow, message } = {}) {
  if (Socket.createStream !== originalCreateStreamFunc) {
    throw 'A previous call to throwSocket() never reverted the patch after the test was completed. This may be because' +
    'the specified web socket call (method/modelName) was never invoked. To fix this, call revertPatch() after your test' +
    'is completed';
  }
  const origFunc = Socket.createStream;
  // If no methodToThrow or modelNameToThrow was provided, the next socket stream should throw an exception
  const targetNextCall = !methodToThrow && !modelNameToThrow;

  Socket.createStream = function(method, modelName) {
    const isTargetedCall = targetNextCall || (method === methodToThrow && modelName === modelNameToThrow);
    const stream = origFunc.apply(this, arguments);

    if (isTargetedCall) {
      stream.fetchSocketClient = function() {
        const reset = new Promise((resolve) => {
          resolve();
        }).then(() => {
          revertPatch();
        });
        const boom = new Promise((resolve, reject) => {
          reject(message);
        });
        return all([reset, boom]);
      };
      return waitForError(stream);
    }
    return stream;
  };
  return revertPatch;
}
