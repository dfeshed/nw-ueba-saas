import { Promise, all } from 'rsvp';
import { Socket } from 'streaming-data/services/data-access';

export function patchSocket(callback) {
  const origFunc = Socket.createStream;
  Socket.createStream = function(method, modelName, query) {
    try {
      callback(method, modelName, query);
    } finally {
      Socket.createStream = origFunc;
    }
    return origFunc.apply(this, arguments);
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
    return stream;
  };
}
