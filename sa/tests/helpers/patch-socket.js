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
