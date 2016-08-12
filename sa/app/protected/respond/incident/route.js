import Ember from 'ember';

const { Route,
  RSVP: {
    hash
    },
  Logger,
  run,
  set,
  isNone
  } = Ember;

export default Route.extend({

  // Array holding the list of all subscriptions
  currentStreams: [],

  // the id of the current incident
  incidentId: null,

  model(params) {
    this.set('incidentId', params.incident_id);

    return hash({
      incident: this.store.findRecord('incident', params.incident_id),
      users: this.store.findAll('user')
    });
  },

  afterModel(resolvedModel) {

    // Create the notify socket
    let notify = this.store.notify('incident', {
      subDestinationUrlParams: 'edit',
      filter: [{ field: 'id', value: resolvedModel.incident.id }]
    }, { requireRequestId: false });
    notify.subscribe((response) => {
      Logger.log(`Notify next() callback, notificationCode: ${ response.notificationCode }`);
      let { data } = response;

      // Updating the whole incident when we received a notification
      let innerIncident = data.findBy('id', resolvedModel.incident.id);
      set(resolvedModel, 'incident', innerIncident);
    }, () => {
      Logger.error('Error processing notify call for incident model');
    });
    notify.start();

    this.get('currentStreams').push(notify);
  },

  actions: {
    /**
     * @name willTransition
     * @description when the router will transit to another route, the opened stream are being closed
     * @public
     */
    willTransition() {
      let streamRequests = this.get('currentStreams');
      Logger.debug(`Closing ${ streamRequests.length } web-socket connections`);
      run(() => {
        streamRequests.forEach((streamRequest) => {
          streamRequest.stream.stop();
        });
      });
      this.set('currentStreams', []);
    },

    /**
     * @name addNewJournal
     * @description creates a new Journal Entry and saves it
     * @public
     */
    addNewJournal(jsonNote) {
      Logger.debug('addNewJournal. Adding new journal entry to Incident notes collection...');

      let incident = this.store.peekRecord('incident', this.get('incidentId'));
      if (isNone(incident.get('notes'))) {
        incident.set('notes', []);
      }
      incident.get('notes').pushObject(jsonNote);
      incident.save();
    },

    /**
     * @name editJournal
     * @description updates an existing journal
     * @public
     */
    editJournal({ id, author, milestone, notes }) {

      Logger.debug(`editJournal. Editing existing journal entry ${ id }, notes: ${ notes } ...`);

      let incident = this.store.peekRecord('incident', this.get('incidentId'));
      let currentNote = incident.get('notes').findBy('id', id);

      currentNote.notes = notes;
      currentNote.milestone = milestone;
      currentNote.author = author;
      incident.save();
    },

    /**
     * @name deleteJournal
     * @description deletes an existing journal
     * @public
     */
    deleteJournal(journalId) {
      Logger.debug(`deleteJournal. Deleting record ${ journalId }`);

      let incident = this.store.peekRecord('incident', this.get('incidentId'));
      let currentNote = incident.get('notes').findBy('id', journalId);

      if (currentNote) {
        incident.get('notes').removeObject(currentNote);
        incident.save();
      }
    }
  }
});
