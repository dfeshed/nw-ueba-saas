import Ember from 'ember';

const {
  Route,
  Logger,
  inject: {
    service
  },
  set
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
    this.set('layoutService.panelD', 'hidden');
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
    const alerts = [];
    const sort = this.get('sort');

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
    },

    displayEvents(alert) {
      this.set('layoutService.main', 'panelD');
      this.set('layoutService.panelA', 'hidden');
      this.set('layoutService.panelB', 'hidden');
      this.set('layoutService.panelC', 'half');
      this.set('layoutService.panelD', 'half');
      this.set('layoutService.journalPanel', 'hidden');

      const parentModel = this.modelFor('protected.respond.incident');

      set(parentModel, 'events', []);

      this.request.streamRequest({
        method: 'stream',
        modelName: 'events',
        query: {
          filter: [{ field: '_id', value: alert.id }]
        },
        onResponse: ({ data }) => {
          set(parentModel, 'events', data);
        },
        onError(response) {
          Logger.error('Error processing notify call for events model', response);
        }
      });
    }
  }
});
