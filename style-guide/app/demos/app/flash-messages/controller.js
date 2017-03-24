import Ember from 'ember';

const {
  Controller,
  inject: {
    service
  }
} = Ember;

export default Controller.extend({

  flashMessages: service(),

  actions: {
    triggerInfoMessage() {
      this.get('flashMessages').info('Info message');
    },

    triggerSuccessMessage() {
      this.get('flashMessages').success('Success message');
    },

    triggerWarningMessage() {
      this.get('flashMessages').warning('Warning message');
    },

    triggerErrorMessage() {
      this.get('flashMessages').error('Error message');
    }
  }
});
