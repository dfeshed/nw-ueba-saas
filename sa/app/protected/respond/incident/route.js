import Ember from 'ember';

const {
  Route,
  RSVP: {
    hash
  },
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

    return hash({
      incident: this.store.queryRecord('incident', { incidentId: params.incident_id }),
      users: this.store.findAll('user'),
      categoryTags: this.store.findAll('category-tags')
    });
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
      onResponse: ({ data, notificationCode }) => {
        Logger.log(`Notify next() callback, notificationCode: ${ notificationCode }`);

        // Updating the whole incident when we received a notification
        let innerIncident = data.findBy('id', currentIncidentId);
        if (innerIncident) {
          // only update current incident, ignoring others
          set(resolvedModel, 'incident', innerIncident);
          // Update the store with the updated incident
          this.store.pushPayload({ 'incidents': [innerIncident] });
          innerIncident.id = currentIncidentId;
        }
      },
      onError(response) {
        Logger.error('Error processing notify call for incident model', response);
      }
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
      if (typeOf(updatedField) === 'object') {
        incident.setProperties(updatedField);
      } else {
        incident.set(updatedField, updatedValue);
      }
      incident.save().then(() => {
        Logger.debug('Incident was saved');
      }).catch((reason) => {
        Logger.error(`Error saving incident. Reason: ${ reason }`);
      });
    },

    /**
     * @name createJournal
     * @description creates a new Journal Entry and saves it
     * @public
     */
    createJournal(jsonNote) {
      let journal = this.store.createRecord('journal-entry', {
        incidentId: this.get('incidentId'),
        journalMap: {
          notes: jsonNote.notes,
          author: jsonNote.author,
          milestone: jsonNote.milestone
        }
      });

      journal.save().then((response) => {
        Logger.debug(`Journal created. Response: ${ response }`);
      }).catch((reason) => {
        Logger.error(`Journal was not created. Reason ${ reason }`);
      });
    },

    /**
     * @name updateJournal
     * @description updates an existing journal
     * @public
     */
    updateJournal(jsonNote) {

      this.request.promiseRequest({
        method: 'updateRecord',
        modelName: 'journal-entry',
        query: {
          incidentId: this.get('incidentId'),
          journalId: jsonNote.id,
          journalMap: {
            notes: jsonNote.notes,
            author: jsonNote.author,
            milestone: jsonNote.milestone
          }
        }
      }).then((response) => {
        Logger.debug(`Journal updated, response: ${ response }`);
      }).catch((reason) => {
        Logger.error(`Journal was not updated. Reason ${ reason }`);
      });
    },

    /**
     * @name deleteJournal
     * @description deletes an existing journal
     * @public
     */
    deleteJournal(journalId) {

      this.request.promiseRequest({
        method: 'deleteRecord',
        modelName: 'journal-entry',
        query: {
          incidentId: this.get('incidentId'),
          journalId
        }
      }).then((response) => {
        Logger.debug(`Journal deleted, response: ${ response }`);
      }).catch((reason) => {
        Logger.error(`Journal was not deleted. Reason ${ reason }`);
      });
    }
  }

});
