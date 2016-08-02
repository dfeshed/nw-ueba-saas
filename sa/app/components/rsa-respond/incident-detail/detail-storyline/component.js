import Ember from 'ember';

const {
  Component,
  computed,
  computed: {
    alias
  }
} = Ember;

export default Component.extend({
  model: null,

  IsCandC: computed.notEmpty('alerts.firstObject.alert.events.firstObject.enrichment.command_control'),

  alerts: alias('model.alerts'),

  alertCount: alias('model.alertCount')
});
