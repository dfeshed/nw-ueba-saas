import Ember from 'ember';

const { Controller } = Ember;

export default Controller.extend({

  actions: {
    removeLabel(toRemove) {
      alert(toRemove);
    }
  }
});
