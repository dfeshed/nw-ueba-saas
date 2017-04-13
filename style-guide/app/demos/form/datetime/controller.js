import Ember from 'ember';

const {
  Controller,
  Logger
} = Ember;

export default Controller.extend({
  actions: {
    selectEvent(date) {
      Logger.debug('Select Event has fired.', date);
    },
    openEvent() {
      Logger.debug('Open Event has fired.');
    },
    closeEvent() {
      Logger.debug('Close Event has fired.');
    },
    drawEvent(pikadayObj) {
      Logger.debug('Draw Event has fired.', pikadayObj);
    }
  }
});