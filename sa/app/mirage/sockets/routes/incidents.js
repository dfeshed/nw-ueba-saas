import Ember from 'ember';
import IncidentSamples from 'sa/mirage/sockets/routes/incident_samples';

const { typeOf } = Ember;

/**
 * @file MockServer message handlers that respond to requests regarding incident model(s).
 * Here we can register handlers for requests related to incidents, such as streaming a list of incidents, fetching
 * a single incident record by id, or updating incidents.
 * @public
 */
let _alertsMap = null;
let newIncID = 800;
let newJournalID = 1;

function _makeAlertsMap(dbAlerts) {
  if (dbAlerts && !_alertsMap) {
    _alertsMap = {};
    dbAlerts.forEach(function(alert) {
      let incId = alert.incidentId;
      if (incId) {
        let arr = _alertsMap[incId];
        if (!arr) {
          arr = _alertsMap[incId] = [];
        }
        arr.push(alert);
      }
    });
  }
  return _alertsMap;
}

export default function(server) {
  server.route('incident', 'stream', function(message, frames, server) {
    // Wait until after all incidents DB has been loaded from a JSON file.

    server.asyncFixturesPromise.then(() => {
      let { filter } = frames[0].body;
      let statusFilter = (filter || []).findBy('field', 'statusSort') || {};
      let records = server.mirageServer.db.incident;
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

      let map = _makeAlertsMap(server.mirageServer.db.alerts);

      filteredRecords = filteredRecords.map((incident) => {
        incident.riskScore = Math.min(99, (10 + Math.round(100 * Math.random())));
        if (map && !incident.alerts) {
          incident.alerts = !map[incident.id] ? [] : map[incident.id].map((alert) => {
            return { alert: alert.alert };
          });
        }
        return incident;
      });

      server.streamList(
        filteredRecords,
        frames[0].body.page,
        null,
        frames);
    });
  });

  server.route('incident', 'notify', function(message, frames, server) {
    // Wait until after all incidents DB has been loaded from a JSON file.

    server.asyncFixturesPromise.then(() => {
      let response = [];
      let { filter } = frames[0].body;
      let idFilter = (filter || []).findBy('field', 'id') || {};
      let incidents;
      // filter by incident id
      if (idFilter && typeOf(idFilter.value) !== 'undefined') {
        incidents = server.mirageServer.db.incident.where({ 'id': idFilter.value });
      } else {
        // if not filter, status sort is default
        incidents = server.mirageServer.db.incident.where({ 'statusSort': 0 });
      }
      let map = _makeAlertsMap(server.mirageServer.db.alerts);
      // update the first 10 incidents
      let someIncidents = incidents.slice(0, 10);
      someIncidents.forEach((incident) => {
        if (map) {
          incident.alerts = !map[incident.id] ? [] : map[incident.id].map((alert) => {
            let json = {};
            json.alert = alert.alert;
            return json;
          });
        }
        response.push(incident);
      });

      // to mock async add/update/delete change the notificationCode here
      // notificationCode can be 0/1/2 -> incident(s) in the response were added/updated/deleted respectively
      // TODO: not handling delete incident use case yet. Will add it once back-end is ready
      response.notificationCode = 1;
      if (response.notificationCode === 0) {
        // create a new incident

        IncidentSamples.newIncident.id = `INC-${ newIncID++ }`;
        IncidentSamples.assignedIncident.id = `INC-${ newIncID++ }`;
        IncidentSamples.inProgressIncident.id = `INC-${ newIncID++ }`;

        response.push(IncidentSamples.newIncident, IncidentSamples.assignedIncident, IncidentSamples.inProgressIncident);

        server.mirageServer.db.incident.insert(IncidentSamples.newIncident);
        server.mirageServer.db.incident.insert(IncidentSamples.assignedIncident);
        server.mirageServer.db.incident.insert(IncidentSamples.inProgressIncident);

      } else if (response.notificationCode === 1) {

        // update the first 10 incidents
        someIncidents.forEach((incident) => {
          incident.notes = incident.notes || [];

          // removing an existing note
          incident.notes.popObject();

          // updating existing notes
          incident.notes.forEach((note) => {
            note.notes += ' UPDATED';
          });

          // adding a few new Journals
          incident.notes.pushObject({ id: newJournalID++, notes: 'This journal entry wasnt here before', created: new Date(), author: 'admin', milestone: 'INSTALLATION' });
          incident.notes.pushObject({ id: newJournalID++, notes: 'This is a NEW journal entry', created: new Date(), author: 'local', milestone: 'CONTAINMENT' });
          incident.notes.pushObject({ id: newJournalID++, notes: 'This is also a NEW journal entry', created: new Date(), author: 'ian', milestone: 'ERADICATION' });

          server.mirageServer.db.incident.update(incident.id, incident);

          response.push(incident);
        });

        IncidentSamples.newIncident.id = `INC-${ newIncID++ }`;
        response.push(IncidentSamples.newIncident);

        server.mirageServer.db.incident.insert(IncidentSamples.newIncident);
      }

      server.streamList(
        response,
        frames[0].body.page,
        null,
        frames);
    });
  });

  server.route('incident', 'queryRecord', function(message, frames, server) {
    let frame = (frames && frames[0]) || {};
    let incident = server.mirageServer.db.incident.find(frame.body.incidentId);
    let map = _makeAlertsMap(server.mirageServer.db.alerts);

    if (map) {
      incident.alerts = !map[incident.id] ? [] : map[incident.id].map((alert) => {
        let json = {};
        json.alert = alert.alert;
        return json;
      });
    }
    incident.riskScore = Math.min(99, (10 + Math.round(100 * Math.random())));
    incident.prioritySort = Math.round(3 * Math.random());
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

    if (frame.body.incidentId) {
      server.mirageServer.db.incident.update(frame.body.incidentId, frame.body.updates);
      updatedCount = 1;
    } else {
      frame.body.incidentIds.forEach((incidentId) => {
        server.mirageServer.db.incident.update(incidentId, frame.body.updates);
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
