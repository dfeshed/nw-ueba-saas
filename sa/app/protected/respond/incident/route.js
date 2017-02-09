import Ember from 'ember';
import NotificationHelper from 'sa/protected/respond/mixins/notificationHelper';

const {
  Route,
  Logger,
  set,
  inject: {
    service
  },
  typeOf
} = Ember;

export default Route.extend(NotificationHelper, {

  // the id of the current incident
  incidentId: null,

  layoutService: service('layout'),
  i18n: service(),

  // Service to retrieve information from local storage
  respondMode: service(),

  titleToken(model) {
    return model.incidentId;
  },

  title(tokens) {
    return this.get('i18n').t('pageTitle', {
      section: this.get('i18n').t('respond.incidentDetails.titleWithId', {
        id: tokens[0]
      })
    });
  },

  activate() {
    this.set('layoutService.actionConfig', 'incident');
    this.set('layoutService.displayJournal', true);
    this.set('layoutService.main', 'panelB');
    this.set('layoutService.panelA', 'quarter');
    this.set('layoutService.panelB', 'main');
    this.set('layoutService.panelD', 'hidden');
  },

  deactivate() {
    this.set('layoutService.actionConfig', 'app');
    this.set('layoutService.journalPanel', 'hidden');
    this.set('layoutService.displayJournal', false);
    this.set('layoutService.panelD', 'hidden');
  },

  model(params) {
    this.set('incidentId', params.incident_id);
    const details = { 'incidentId': params.incident_id, 'indicators': [], 'incident': [], 'categoryTags': [], 'users': [], 'events': [], 'services': [] };
    this.request.streamRequest({
      method: 'stream',
      modelName: 'storyline',
      query: {
        filter: [{ field: '_id', value: params.incident_id }],
        sort: [{ field: 'alert.timeStamp', descending: true }]
      },
      onResponse: ({ data }) => {
        const { relatedIndicators } = data;
        if (typeOf(relatedIndicators) !== 'undefined') {
          details.indicators.pushObjects(relatedIndicators);
        }
      },
      onError: (error) => {
        Logger.error(`Error loading storyline. Error: ${error}`);
        this.displayFlashErrorLoadingModel('storyline', false);
      },
      onTimeout: () => {
        Logger.error('Timeout loading storyline.');
        this.displayFlashErrorLoadingModel('storyline', false);
      }
    });

    this.request.streamRequest({
      method: 'queryRecord',
      modelName: 'incident',
      query: { 'incidentId': params.incident_id },
      onResponse: ({ data }) => {
        details.incident.pushObjects([data]);
      },
      onError() {
        Logger.error('Error loading incident');
        this.displayFlashErrorLoadingModel('incident', false);
      },
      onTimeout: () => {
        Logger.error('Timeout loading incident.');
        this.displayFlashErrorLoadingModel('incident', false);
      }
    });

    this.request.streamRequest({
      method: 'stream',
      modelName: 'category-tags',
      query: {},
      onResponse: ({ data }) => {
        details.categoryTags.pushObjects(data);
      },
      onError: (error) => {
        Logger.warn(`Error loading categoryTags. Error: ${error}`);
        this.displayFlashErrorLoadingModel('categoryTags');
      },
      onTimeout: () => {
        Logger.warn('Timeout loading categoryTags.');
        this.displayFlashErrorLoadingModel('categoryTags');
      }
    });

    this.request.streamRequest({
      method: 'findAll',
      modelName: 'core-service',
      query: {},
      onResponse: ({ data }) => {
        details.services.pushObjects(data);
      },
      onError: (error) => {
        Logger.warn(`Error loading core-service. Error: ${error}`);
        this.displayFlashErrorLoadingModel('coreService');
      },
      onTimeout: () => {
        Logger.warn('Timeout loading core-service.');
        this.displayFlashErrorLoadingModel('coreService');
      }
    });

    return details;
  },

  afterModel(resolvedModel) {
    const currentIncidentId = this.get('incidentId');

    this.request.streamRequest({
      method: 'notify',
      modelName: 'incident',
      query: {
        subDestinationUrlParams: 'all_incidents',
        filter: [{ field: 'id', value: currentIncidentId }]
      },
      streamOptions: {
        requireRequestId: false
      },
      onResponse: ({ data }) => {
        if (typeOf(data) === 'array') {
          const innerIncident = data.findBy('id', currentIncidentId);
          // Updating the whole incident when we received a notification
          if (innerIncident) {
            set(resolvedModel, 'incident', [innerIncident]);
          }
        }
      },
      onError() {
        Logger.error('Error processing notify call for incident model');
      }
    });

    this.request.streamRequest({
      method: 'stream',
      modelName: 'users',
      query: {},
      onResponse: ({ data: users }) => {
        resolvedModel.users.addObjects(users);
      },
      onError(error) {
        Logger.warn(`Error loading Users. Error: ${error}`);
        this.displayFlashErrorLoadingModel('users');
      },
      onTimeout: () => {
        Logger.warn('Timeout loading Users.');
        this.displayFlashErrorLoadingModel('users');
      }
    });
  },

  /**
   * @name _updateRecord
   * @description Saves the model and presents a flash notification
   * @private
   */
  _updateRecord(incident, updatedField, updatedValue = null, options = {}) {

    if (typeOf(updatedField) === 'object') {
      incident.setProperties(updatedField);
    } else {
      incident.set(updatedField, updatedValue);
    }
    incident.save().then(() => {
      Logger.debug('Incident was saved');
      this.displayEditFieldSuccessMessage(options);
    }).catch(() => {
      Logger.error('Error saving incident.');
      this.displayErrorFlashMessage('incident.edit.update.errorMessage');
    });
  },

  actions: {

    /**
     * @name saveAction
     * @description updates the incident with the updated values
     * @param {string} updatedField - field name to be updated or the hash of keys and values to set
     * @param {string} updatedValue - new value to be saved or null when `updatedField` is a hash of key-values
     * @param {Object} options - flash notification options to diplay "AttributeName was successfully ActionName".
     * When not present, a generic "Incident was updated" message is used instead.
     *  - actionName: updateRecord / deleteRecord / createRecord
     *  - attributeName: user friendly i18n-key attribute name
     *  - enableNotification: when false, no notification is displayed
     * @public
     */
    saveAction(updatedField, updatedValue, options) {
      Logger.debug(`Updating incident ${ this.get('incidentId') }`);
      const incident = this.store.peekRecord('incident', this.get('incidentId'));
      if (incident) {
        this._updateRecord(incident, updatedField, updatedValue, options);
      } else {
        this.store.queryRecord('incident', { incidentId: this.get('incidentId') }).then((incident) => {
          this._updateRecord(incident, updatedField, updatedValue, options);
        });
      }
    },

    /**
     * @name saveJournal
     * @description saves the journal entry. If the journal has already an ID an update is invoked,
     * otherwise it creates it
     * @public
     */
    saveJournal(jsonNote, mode) {
      const query = {
        incidentId: this.get('incidentId'),
        journalId: jsonNote.id
      };

      if (mode !== 'deleteRecord') {
        query.journalMap = {
          notes: jsonNote.notes,
          author: jsonNote.author,
          milestone: jsonNote.milestone
        };
      }

      // flash message attributes
      const attributeName = 'incident.fields.journal';
      const actionName = `incident.edit.actions.${mode}`;

      this.request.promiseRequest({
        method: mode,
        modelName: 'journal-entry',
        query
      }).then(() => {
        Logger.debug('Journal saved');
        this.displayEditFieldSuccessMessage({ attributeName, actionName });
      }).catch(() => {
        Logger.error('Journal was not saved.');
        this.displayErrorFlashMessage('incident.edit.update.errorMessage');
      });
    },

    /**
     * Use respond-mode service to update local storage based on view changes
     * @param alertId String ID of last viewed alert
     * @param showPanel boolean indicating if event overview panel should be shown
     * @param showFullPanel boolean indicating if event overview panel should be expanded or collapsed
     * @public
     */
    saveEventOverviewPanelState(alertId = null, showPanel = false, showFullPanel = true) {
      this.set('respondMode.selectedEventOverviewOptions', { alertId, showPanel, showFullPanel });
    },

    closeEventOverviewPanel() {

      // Update localStorage by resetting eventOverView panel information
      this.send('saveEventOverviewPanelState');

      this.set('layoutService.main', 'panelC');
      this.set('layoutService.panelB', 'half');
      this.set('layoutService.panelC', 'half');
      this.set('layoutService.panelD', 'hidden');
    },

    expandCollapseEventOverviewPanel(expandPanel) {

      // Update localStorage
      const { alertId } = this.get('respondMode.selectedEventOverviewOptions');
      this.send('saveEventOverviewPanelState', alertId, true, expandPanel);

      if (expandPanel) {
        this.set('layoutService.journalPanel', 'hidden');
        this.set('layoutService.panelA', 'hidden');
        this.set('layoutService.panelB', 'hidden');

        this.set('layoutService.main', 'panelD');
        this.set('layoutService.panelC', 'half');
        this.set('layoutService.panelD', 'half');
      } else {

        this.set('layoutService.main', 'panelC');
        this.set('layoutService.panelB', 'quarter');
        this.set('layoutService.panelC', 'half');
        this.set('layoutService.panelD', 'quarter');
      }
    }
  }
});
