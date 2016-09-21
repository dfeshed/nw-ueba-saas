import Ember from 'ember';
import computed from 'ember-computed-decorators';

const {
    Component
    } = Ember;

export default Component.extend({
  classNames: 'rsa-context-panel-header',
  tagName: 'section',

  entity: null,
  hostCount: 0,

  filesCount: 0,
  listsCount: 0,
  feedsCount: 0,
  contextData: null,
  active: 'hosts',
  actions: {
    activate(option) {
      this.set('active', option);
    },

    closeAction() {
      this.sendAction('closePanel');
    }
  },

  @computed('contextData.incidentsData')
  incidentsCount: (incidentsData) => incidentsData ? incidentsData.length : 0,

  @computed('contextData.alertsData')
  alertsCount: (alertsData) => alertsData ? alertsData.length : 0

});
