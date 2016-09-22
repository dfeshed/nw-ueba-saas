import Ember from 'ember';

const { $, Logger } = Ember;

export default function safeCallback(fn) {
  if (!$.isFunction(fn)) {
    Logger.warn(`Invalid callback invoked in ${this}. Ignoring request.`);
  } else {
    let args = [...arguments].slice(1);
    fn(...args);
  }
}
