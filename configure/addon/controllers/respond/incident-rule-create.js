import Controller from '@ember/controller';

export default Controller.extend({
  actions: {
    controllerTransitionToRules() {
      this.send('transitionToRules');
    }
  }
});
