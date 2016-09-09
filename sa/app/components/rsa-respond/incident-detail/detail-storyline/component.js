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

  classNames: 'spacer',
  model: null,

  IsCandC: computed.notEmpty('alerts.firstObject.alert.events.firstObject.enrichment.command_control'),

  alerts: alias('model.alerts'),

  alertCount: alias('model.alertCount'),

  actions: {
    toggleJournal() {
      if (this.get('layoutService.journalPanel') === 'hidden') {
        this.set('layoutService.journalPanel', 'quarter');
        this.set('layoutService.panelA', 'hidden');
      } else {
        this.set('layoutService.journalPanel', 'hidden');
        this.set('layoutService.panelA', 'quarter');
      }
    }
  }
});
