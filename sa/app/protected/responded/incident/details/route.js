import Ember from 'ember';
import NotificationHelper from 'sa/protected/responded/mixins/notificationHelper';

const {
  Route,
  Logger,
  inject: {
    service
  },
  set,
  isEmpty,
  typeOf
} = Ember;

export default Route.extend(NotificationHelper, {
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
    const alerts = [];
    if (params.detail_id !== 'catalyst') {
      const incidentModel = this.modelFor('protected.responded.incident');
      if (!isEmpty(incidentModel.indicators)) {
        const allIndicators = incidentModel ? incidentModel.indicators : [];
        const indicator = allIndicators.findBy('indicator.id', params.detail_id);
        if (indicator) {
          alerts.pushObjects([indicator.indicator]);
          this._restoreEventOverviewPanelState(alerts);
        }
      } else {
        this.request.streamRequest({
          method: 'stream',
          modelName: 'storyline',
          query: {
            filter: [{ field: '_id', value: transition.params['protected.responded.incident'].incident_id }],
            sort: [{ field: 'alert.timeStamp', descending: true }]
          },
          onResponse: ({ data }) => {
            const { relatedIndicators } = data;
            if (typeOf(relatedIndicators) !== 'undefined') {
              const indicator = relatedIndicators.findBy('indicator.id', params.detail_id);
              if (indicator) {
                alerts.pushObjects([indicator.indicator]);
                this._restoreEventOverviewPanelState(alerts);
              }
            }
          },
          onError: (error) => {
            Logger.error(`Error loading storyline. Error: ${error}`);
            this.displayFlashErrorLoadingModel('storyline');
          },
          onTimeout: () => {
            Logger.warn('Timeout loading storyline.');
            this.displayFlashErrorLoadingModel('storyline');
          }
        });
      }
    } else {
      const sort = this.get('sort');
      this.request.streamRequest({
        method: 'stream',
        modelName: 'alerts',
        query: {
          filter: [{ field: 'incidentId', value: transition.params['protected.responded.incident'].incident_id }],
          sort
        },
        onResponse: ({ data }) => {
          alerts.pushObjects(data);
          this._restoreEventOverviewPanelState(alerts);
        },
        onError: (error) => {
          Logger.error(`Error processing notify call for alerts model. Error: ${error}`);
          this.displayFlashErrorLoadingModel('alerts');
        },
        onTimeout: () => {
          Logger.warn('Timeout loading alerts.');
          this.displayFlashErrorLoadingModel('alerts');
        }
      });
    }
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
      if (transition.targetName === 'protected.responded.incident.index') {
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
      const parentModel = this.modelFor('protected.responded.incident');
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
        onError: (error) => {
          Logger.error(`Error loading events. Error: ${error}`);
          this.displayFlashErrorLoadingModel('events');
        },
        onTimeout: () => {
          Logger.error('Timeout loading events.');
          this.displayFlashErrorLoadingModel('events');
        }
      });
    }
  }
});
