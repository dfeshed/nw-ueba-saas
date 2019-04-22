import Controller from '@ember/controller';

export default Controller.extend({

  actions: {
    // let router handle this
    controllerTransitionToSources() {
      this.send('transitionToSources');
    }
  }

});
