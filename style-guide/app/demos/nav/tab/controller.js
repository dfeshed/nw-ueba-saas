import Ember from 'ember';

const { Controller } = Ember;

export default Controller.extend({

  optionOneIsActive: true,

  optionTwoIsActive: false,

  optionThreeIsActive: false,

  actions: {
    activate(value) {
      this.set('optionOneIsActive', false);
      this.set('optionTwoIsActive', false);
      this.set('optionThreeIsActive', false);
      this.set(value, true);
    }
  }

});
