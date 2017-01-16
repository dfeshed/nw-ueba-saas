import Ember from 'ember';

const {
  Controller
} = Ember;

export default Controller.extend({

  valueStore: null,

  actions: {
    valueChanged(val) {
      this.set('valueStore', val);
    }
  }
});
