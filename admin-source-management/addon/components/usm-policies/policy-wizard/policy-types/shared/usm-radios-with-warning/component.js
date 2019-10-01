import computed from 'ember-computed-decorators';
import UsmRadios from './../usm-radios/component';

export default UsmRadios.extend({

  initialValueBackup: false,

  init() {
    this._super(...arguments);
    this.set('initialValueBackup', this.get('radioButtonValue'));
  },

  @computed('initialValueBackup', 'radioButtonValue')
  isWarningMessage(initialValueBackup, radioButtonValue) {
    // Warning message is to be displayed only when the initial value is enabled and the user is trying to disable it.
    return initialValueBackup && !radioButtonValue;
  }
});