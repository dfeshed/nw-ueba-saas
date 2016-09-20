import Ember from 'ember';
import IncidentSamples from 'sa/mirage/sockets/routes/incident_samples';

const { typeOf } = Ember;
let newJournalID = 1;


/**
 * @file MockServer message handlers that respond to requests regarding incident model(s).
 * Here we can register handlers for requests related to incidents, such as streaming a list of incidents, fetching
 * a single incident record by id, or updating incidents.
 * @public
 */

export default function(server) {
  server.route('incident', 'stream', function(message, frames, server) {
    let { filter } = frames[0].body;
    let statusFilter = (filter || []).findBy('field', 'statusSort') || {};
    let records = server.mirageServer.db.incidents;
    let filteredRecords = [];

    if (statusFilter && typeOf(statusFilter.value) !== 'undefined') {
      filteredRecords = records.where({ statusSort: statusFilter.value });
    } else if (statusFilter && typeOf(statusFilter.values) !== 'undefined') {
      statusFilter.values.forEach((filter) => {
        filteredRecords.pushObjects(records.where({ statusSort: filter }));
      });
    } else {
      filteredRecords = records;
    }

    server.streamList(
      filteredRecords,
      frames[0].body.page,
      null,
      frames);
  });

  server.route('incident', 'notify', function(message, frames, server) {
    let { filter } = frames[0].body;
    let idFilter = (filter || []).findBy('field', 'id') || {};
    let incidents;
    let db = server.mirageServer.db.incidents;

    // filter by incident id
    if (idFilter && typeOf(idFilter.value) !== 'undefined') {
      incidents = db.where({ 'id': idFilter.value });
    } else {
      // if not filter, status sort is default
      incidents = db.where({ 'statusSort': 0 });
    }
    // update the first 10 incidents
    let response = incidents.slice(0, 10);

    // to mock async add/update/delete change the notificationCode here
    // notificationCode can be 0/1/2 -> incident(s) in the response were added/updated/deleted respectively
    // TODO: not handling delete incident use case yet. Will add it once back-end is ready
    response.notificationCode = 0;
    if (response.notificationCode === 0) {
      // create a new incident
      response.push(IncidentSamples.newIncident, IncidentSamples.assignedIncident, IncidentSamples.inProgressIncident);
      let newIncident = db.where({ 'id': IncidentSamples.newIncident.id });
      if (newIncident.length <= 0) {
        // if one incident doesnt exist assume all 3 incidents aren't there in db and add them
        db.insert(IncidentSamples.newIncident);
        db.insert(IncidentSamples.assignedIncident);
        db.insert(IncidentSamples.inProgressIncident);
      }
    } else if (response.notificationCode === 1) {
      // update the first 10 incidents
      response.forEach((incident) => {
        incident.notes = incident.notes || [];

        // removing an existing note
        incident.notes.popObject();

        // updating existing notes
        incident.notes.forEach((note) => {
          note.notes += ' UPDATED';
        });

        // adding a few new Journals
        incident.notes.pushObject({ id: newJournalID++, notes: 'This journal entry wasnt here before', created: new Date(), author: 'admin', milestone: 'INSTALLATION' });
        incident.notes.pushObject({ id: newJournalID++, notes: 'This is a NEW journal entry', created: new Date(), author: 'admin', milestone: 'CONTAINMENT' });
        incident.notes.pushObject({ id: newJournalID++, notes: 'This is also a NEW journal entry', created: new Date(), author: 'ian', milestone: 'ERADICATION' });

        db.update(incident.id, incident);
        response.push(incident);
      });
      response.push(IncidentSamples.newIncident);
      db.insert(IncidentSamples.newIncident);
    }

    server.streamList(
      response,
      frames[0].body.page,
      null,
      frames);
  });

  server.route('incident', 'queryRecord', function(message, frames, server) {
    let frame = (frames && frames[0]) || {};
    let db = server.mirageServer.db.incidents;
    let { incidentId } = frame.body;
    let incident = db.find(incidentId);
    incident.id = incidentId;
    incident.type = 'incident';

    server.sendFrame('MESSAGE', {
      subscription: (frame.headers || {}).id || '',
      'content-type': 'application/json'
    }, {
      code: 0,
      data: incident,
      request: frame.body
    });
  });

  server.route('incident', 'updateRecord', function(message, frames, server) {
    let frame = (frames && frames[0]) || {};

    let updatedCount = 0;
    let db = server.mirageServer.db.incidents;
    if (frame.body.incidentId) {
      db.update(frame.body.incidentId, frame.body.updates);
      updatedCount = 1;
    } else {
      frame.body.incidentIds.forEach((incidentId) => {
        db.update(incidentId, frame.body.updates);
      });
      updatedCount = frame.body.incidentIds.length;
    }

    server.sendFrame('MESSAGE', {
      subscription: (frame.headers || {}).id || '',
      'content-type': 'application/json'
    }, {
      code: 0,
      data: updatedCount,
      request: frame.body
    });
  });
}
