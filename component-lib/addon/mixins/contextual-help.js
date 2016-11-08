import Ember from 'ember';

const {
  Mixin,
  inject: {
    service
  }
} = Ember;

export default Mixin.create({

  contextualHelp: service(),

  actions: {

    goToGlobalHelp() {
      this.get('contextualHelp').goToGlobalHelp();
    },

    goToHelp(module, topic) {
      this.get('contextualHelp').goToHelp(module, topic);
    }

  }
});
