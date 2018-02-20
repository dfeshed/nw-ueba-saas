import FlashObject from 'ember-cli-flash/flash/object';
import FlashService from 'ember-cli-flash/services/flash-messages';

FlashObject.reopen({ init() {} });
FlashService.reopen({
  defaultTypes: ['error', 'success']
});