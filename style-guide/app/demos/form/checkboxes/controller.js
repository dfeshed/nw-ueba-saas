import Ember from 'ember';

const {
  Controller
} = Ember;

export default Controller.extend({

  isChecked: false,

  actions: {
    toggleIsChecked() {
      this.toggleProperty('isChecked');
    }
  }
});
