import { computed } from '@ember/object';
import UsmRadios from './../usm-radios/component';

export default UsmRadios.extend({
  initialValueBackup: false,

  init() {
    this._super(...arguments);
    this.set('initialValueBackup', this.get('radioButtonValue'));
  },

  isWarningMessage: computed('initialValueBackup', 'radioButtonValue', function() {
    // Warning message is to be displayed only when the initial value is enabled and the user is trying to disable it.
    return this.initialValueBackup && !this.radioButtonValue;
  })
});