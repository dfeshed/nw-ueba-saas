import Ember from 'ember';

const {
  Component,
  computed,
  computed: {
    alias
  },
  inject: {
    service
  }
} = Ember;

export default Component.extend({
  layoutService: service('layout'),

  model: null,

  IsCandC: computed.notEmpty('alerts.firstObject.alert.events.firstObject.enrichment.command_control'),

  alerts: alias('model.alerts'),

  alertCount: alias('model.alertCount'),

  actions: {
    toggleJournal() {
      this.get('layoutService').toggleJournal();
    }
  }
});
