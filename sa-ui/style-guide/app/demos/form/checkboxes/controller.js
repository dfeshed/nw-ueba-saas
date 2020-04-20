import Controller from '@ember/controller';

export default Controller.extend({

  isChecked: false,

  actions: {
    toggleIsChecked() {
      this.toggleProperty('isChecked');
    }
  }
});
