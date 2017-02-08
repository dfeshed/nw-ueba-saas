import Ember from 'ember';
import layout from './template';

const {
  Component
} = Ember;

export default Component.extend({
  layout,
  classNames: 'rsa-context-panel__context-data-table',

  actions: {
    activate(option) {
      this.sendAction('activatePanel', option);
    }
  },

  constructPath(incId, path) {
    path = path.replace('{0}', incId);
    return window.location.origin.concat(path);
  }

});
