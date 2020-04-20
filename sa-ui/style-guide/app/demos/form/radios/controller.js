import Controller from '@ember/controller';

export default Controller.extend({

  valueStore: null,

  actions: {
    valueChanged(val) {
      this.set('valueStore', val);
    }
  }
});
