import Ember from 'ember';
import computed from 'ember-computed-decorators';
import moment from 'moment';

const {
  Component,
  inject: {
    service
  },
  isNone,
  typeOf
} = Ember;

export default Component.extend({
  layoutService: service('layout'),

  tagName: '',

  model: null,

  @computed('model')
  indicators(model) {
    if (isNone(model) || model.length <= 0) {
      return [];
    }
    let signatures = { 'Suspected C&C': 'C2', 'ModuleIOC': 'ECAT', 'Some rule': 'ESA', 'Suspected UBA': 'UBA' };
    let data = [];
    let signatureId = '';
    let alert = {};
    let firstAlertTime = (typeOf(model[ 0 ] === 'object')) ? moment(model[ 0 ].indicator.alert.timestamp) : 0;

    model.forEach((indicator, k) => {
      alert = indicator.indicator.alert;
      signatureId = alert.signature_id;
      if (signatures[signatureId]) {
        if (k > 0) {
          let currAlertTime = moment(alert.timestamp);
          if (firstAlertTime.diff(currAlertTime,'days') <= 0) {
            alert.hideDate = true;
          }
          firstAlertTime = moment(alert.timestamp);
        }

        let signatureKeys = Object.keys(signatures);
        alert.clientSource = signatures[signatureId];
        let index = signatureKeys.indexOf(signatureId);
        if (index >= 0) {
          data.push(indicator.indicator);
          delete signatures[signatureId];
        }
      }
    });
    return data;
  },

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
