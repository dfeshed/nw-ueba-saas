import Ember from 'ember';
import layout from './template';

const {
  Component
} = Ember;

export default Component.extend({
  layout,
  classNames: 'rsa-context-panel__grid',

  actions: {
    activate(option) {
      this.sendAction('activatePanel', option);
    }
  }
});