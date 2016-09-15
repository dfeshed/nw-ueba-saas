import Ember from 'ember';

const {
    Component
} = Ember;

export default Component.extend({
  classNames: 'rsa-context-panel-header',
  tagName: 'section',

  entity: null,
  hostCount: null,
  incidentsCount: null,
  alertsCount: null,
  filesCount: null,
  listsCount: null,
  feedsCount: null,

  active: 'hosts',

  actions: {
    activate(option) {
      this.set('active', option);
    },

    closeAction() {
      this.sendAction('closePanel');
    }
  }
});
