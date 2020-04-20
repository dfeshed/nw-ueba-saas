import Controller from '@ember/controller';
import { inject as service } from '@ember/service';

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
