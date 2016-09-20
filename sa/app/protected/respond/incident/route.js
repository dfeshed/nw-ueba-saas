import Ember from 'ember';

const {
  Route,
  Logger,
  set,
  inject: {
    service
  },
  typeOf
} = Ember;

export default Route.extend({

  // the id of the current incident
  incidentId: null,

  layoutService: service('layout'),

  activate() {
    this.set('layoutService.actionConfig', 'incident');
    this.set('layoutService.displayJournal', true);
    this.set('layoutService.main', 'panelB');
    this.set('layoutService.panelA', 'quarter');
    this.set('layoutService.panelB', 'main');
  },

  deactivate() {
    this.set('layoutService.actionConfig', null);
    this.set('layoutService.journalPanel', 'hidden');
    this.set('layoutService.displayJournal', false);
  },

  model(params) {
    this.set('incidentId', params.incident_id);
    let details = { 'indicators': [], 'incident': [], 'categoryTags': [], 'users': [] };
    this.request.streamRequest({
      method: 'stream',
      modelName: 'storyline',
      query: {
        filter: [{ field: '_id', value: params.incident_id }],
        sort: [{ field: 'alert.timeStamp', descending: true }]
      },
      onResponse: ({ data }) => {
        if (typeOf(data.relatedIndicators) !== 'undefined') {
          details.indicators.pushObjects(data.relatedIndicators);
        }
      },
      onError() {
        Logger.error('Error loading storyline');
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
      }
    });

    this.request.streamRequest({
      method: 'stream',
      modelName: 'category-tags',
      query: {},
      onResponse: ({ data }) => {
        details.categoryTags.pushObjects(data);
      },
      onError() {
        Logger.error('Error loading tags');
      }
    });

    return details;
  },

  afterModel(resolvedModel) {
    let currentIncidentId = this.get('incidentId');

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
          let innerIncident = data.findBy('id', currentIncidentId);
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

    this.get('store').findAll('user').then((user) => {
      set(resolvedModel, 'users', user);
    }).catch(() => {
      Logger.error('Error getting users');
    });
  },

  _updateRecord(incident, updatedField, updatedValue) {
    if (typeOf(updatedField) === 'object') {
      incident.setProperties(updatedField);
    } else {
      incident.set(updatedField, updatedValue);
    }
    incident.save().then(() => {
      Logger.debug('Incident was saved');
    }).catch(() => {
      Logger.error('Error saving incident.');
    });
  },

  actions: {

    /**
     * @name saveAction
     * @description updates the incident with the updated values
     * @param updatedField - field name to be updated or the hash of keys and values to set
     * @param updatedValue - new value to be saved
     * @public
     */
    saveAction(updatedField, updatedValue) {
      Logger.debug(`Updating incident ${ this.get('incidentId') }`);
      let incident = this.store.peekRecord('incident', this.get('incidentId'));
      if (incident) {
        this._updateRecord(incident, updatedField, updatedValue);
      } else {
        this.store.queryRecord('incident', { incidentId: this.get('incidentId') }).then((incident) => {
          this._updateRecord(incident, updatedField, updatedValue);
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
      let query = {
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
      this.request.promiseRequest({
        method: mode,
        modelName: 'journal-entry',
        query
      }).then(() => {
        Logger.debug('Journal saved');
      }).catch(() => {
        Logger.error('Journal was not saved.');
      });
    }
  }
});
