import Ember from 'ember';
import computed from 'ember-computed-decorators';

const {
  Component
} = Ember;

export default Component.extend({
  classNames: 'rsa-respond-detail-overview',

  model: null,

  @computed('model.summary')
  incidentSummary: {
    get: (summary) => summary,

    set(summary) {
      this.set('model.summary', summary);
      return summary;
    }
  },

  actions: {
    // invoked when the summary text area loses focus. saves the updates in the model
    saveSummary() {
      this.sendAction('saveAction', 'summary', this.get('incidentSummary'));
    }
  }
});
