import Ember from 'ember';

const {
  Component
} = Ember;

export default Component.extend({
  classNames: 'rsa-context-panel',


  actions: {
    activate(option) {
      this.sendAction('activatePanel', option);
    }
  }
});
