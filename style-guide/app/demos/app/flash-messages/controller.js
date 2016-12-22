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
      this.get('flashMessages').info('Info message', {
        iconName: 'information-circle'
      });
    },

    triggerSuccessMessage() {
      this.get('flashMessages').success('Success message', {
        iconName: 'check-circle-2'
      });
    },

    triggerWarningMessage() {
      this.get('flashMessages').warning('Warning message', {
        iconName: 'report-problem-circle'
      });
    },

    triggerErrorMessage() {
      this.get('flashMessages').error('Error message', {
        iconName: 'delete-1',
        iconStyle: 'filled'
      });
    }
  }
});
