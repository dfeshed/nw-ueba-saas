import { lookup } from 'ember-dependency-lookup';

export function patchFlash(callback) {
  const flashMessages = lookup('service:flash-messages');
  const origFunc = flashMessages.add;
  flashMessages.add = function() {
    try {
      callback(arguments[0]);
    } finally {
      flashMessages.add = origFunc;
    }
    return origFunc.apply(this, arguments);
  };
}
