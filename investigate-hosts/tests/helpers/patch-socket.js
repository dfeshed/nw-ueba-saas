import { Socket } from 'streaming-data/services/data-access';

const origFunc = Socket.createStream;

export function patchSocket(callback) {
  Socket.createStream = function(method, modelName, query) {
    callback(method, modelName, query);
    return origFunc.apply(this, arguments);
  };
}

export function unpatchSocket() {
  Socket.createStream = origFunc;
}