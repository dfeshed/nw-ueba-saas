import Controller from '@ember/controller';

export default Controller.extend({

  actions: {
    removeLabel(toRemove) {
      alert(toRemove);
    }
  }
});
