import Controller from '@ember/controller';
import Ember from 'ember';

const {
  Logger
} = Ember;

export default Controller.extend({
  eventId: '12345678',
  index: 5,
  total: 107,
  linkToFileAction(file) {
    // Dummy handler, for troubleshooting
    Logger.info('linkToFileAction invoked with file:', file);
  }
});
