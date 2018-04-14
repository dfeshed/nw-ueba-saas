import FlashObject from 'ember-cli-flash/flash/object';

// NOOP function to replace Ember.K
const NOOP = () => {};

FlashObject.reopen({ init: NOOP });
