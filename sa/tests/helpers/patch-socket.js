import { Promise, all } from 'rsvp';
import { registerWaiter } from '@ember/test';
import { Socket } from 'streaming-data/services/data-access';

function waitForStop(stream) {
  let counter = 0;
  registerWaiter(() => counter === 0);
  counter += 1;

  const origStop = stream.stop;
  stream.stop = function() {
    counter -= 1;
    let applied;
    try {
      applied = origStop.apply(this, arguments);
    } finally {
      stream.stop = origStop;
    }
    return applied;
  };
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

export function throwSocket() {
  const origFunc = Socket.createStream;
  Socket.createStream = function() {
    const stream = origFunc.apply(this, arguments);
    stream.fetchSocketClient = function() {
      const reset = new Promise((resolve) => {
        resolve();
      }).then(() => {
        Socket.createStream = origFunc;
      });
      const boom = new Promise((resolve, reject) => {
        reject();
      });
      return all([reset, boom]);
    };
    return waitForError(stream);
  };
}
