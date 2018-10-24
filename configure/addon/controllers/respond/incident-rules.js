import Controller from '@ember/controller';

export default Controller.extend({
  actions: {
    controllerTransitionToRule(ruleId) {
      this.send('transitionToRule', ruleId);
    }
  }
});
