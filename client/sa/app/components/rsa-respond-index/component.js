import Ember from 'ember';

export default Ember.Component.extend({
  model: null,

  respondMode: Ember.inject.service(),

  isCardMode: Ember.computed.equal('respondMode.selected', 'card'),

  actions: {
    gotoIncidentDetail(...args) {
      this.sendAction('gotoIncidentDetail', ...args);
    },
    saveIncident(...args) {
      this.sendAction('saveIncident', ...args);
    }
  }
});
