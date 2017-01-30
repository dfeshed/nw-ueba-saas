import Ember from 'ember';

const {
    Component
    } = Ember;

export default Component.extend({
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
