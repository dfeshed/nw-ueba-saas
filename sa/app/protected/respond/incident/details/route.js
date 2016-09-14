import Ember from 'ember';

const {
  Route,
  Logger,
  inject: {
    service
  }
} = Ember;

export default Route.extend({
  layoutService: service('layout'),

  activate() {
    this.set('layoutService.journalPanel', 'hidden');
    this.set('layoutService.main', 'panelB');
    this.set('layoutService.panelA', 'hidden');
    this.set('layoutService.panelB', 'half');
    this.set('layoutService.panelC', 'half');
  },

  deactivate() {
    this.set('layoutService.journalPanel', 'hidden');
    this.set('layoutService.main', 'panelB');
    this.set('layoutService.panelA', 'quarter');
    this.set('layoutService.panelB', 'main');
    this.set('layoutService.panelC', 'hidden');
  },

  renderTemplate() {
    this.render({ outlet: 'indicator' });
  },

  // default sort
  sort: [{ field: 'risk_score', descending: true }],

  model(params, transition) {
    if (params.detail_id !== 'C2') {
      return null;
    }
    let alerts = [];
    let sort = this.get('sort');

    this.request.streamRequest({
      method: 'stream',
      modelName: 'alerts',
      query: {
        filter: [{ field: 'incidentId', value: transition.params['protected.respond.incident'].incident_id }],
        sort
      },
      onResponse: ({ data }) => {
        alerts.pushObjects(data);
      },
      onError(response) {
        Logger.error('Error processing notify call for alerts model', response);
      }
    });
    return alerts;
  },

  actions: {
    sortAction(field, desc) {
      // set the new sort field and refresh the model
      this.set('sort', [{ field, desc: (desc === true) }]);
      this.refresh();
    }
  }
});
