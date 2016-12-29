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

  // Service to retrieve information from local storage
  respondMode: service(),

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

    if (params.detail_id !== 'c2') {
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
        this._restoreEventOverviewPanelState(alerts);
      },
      onError(response) {
        Logger.error('Error processing notify call for alerts model', response);
      }
    });
    return alerts;
  },

  /**
   * Re-create event overview panel view by obtaining view state from respond-mode service which
   * works with local storage to obtain last saved state of this panel.
   * @param alerts
   * @private
   */
  _restoreEventOverviewPanelState(alerts) {

    // Retrieve view state
    const { alertId, showPanel, showFullPanel } = this.get('respondMode.selectedEventOverviewOptions');

    if (alerts && showPanel) {

      // Retrieve alert by alert's ID. This alert contains an event that should be displayed to the user.
      const alert = alerts.findBy('id', alertId);

      // Display even overview panel
      if (alert) {
        this.send('displayEvents', alert);

        if (!showFullPanel) {
          this.send('expandCollapseEventOverviewPanel', showFullPanel);
        }
      } else {
        // Miss-match. Reset local storage data
        this.send('saveEventOverviewPanelState');
      }
    }
  },

  actions: {
    sortAction(field, desc) {
      // set the new sort field and refresh the model
      this.set('sort', [{ field, desc: (desc === true) }]);
      this.refresh();
    },

    willTransition(transition) {
      // Handle back button click.
      if (transition.targetName === 'protected.respond.incident.index') {
        // Event overview panel is removed. Update local storage state.
        this.send('saveEventOverviewPanelState');
      }
    },

    displayEvents(alert) {

      // Update local storage event overview panel state
      this.send('saveEventOverviewPanelState', alert.id, true, true);

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
