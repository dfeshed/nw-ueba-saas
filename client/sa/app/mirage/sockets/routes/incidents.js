import Ember from 'ember';
import IncidentSamples from 'sa/mirage/sockets/routes/incident_samples';

/**
 * @file MockServer message handlers that respond to requests regarding incident model(s).
 * Here we can register handlers for requests related to incidents, such as streaming a list of incidents, fetching
 * a single incident record by id, or updating incidents.
 * @public
 */
let _alertsMap = null;

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
      let { filter } = frames[0].body,
        statusFilter = (filter || []).findBy('field', 'statusSort') || {},
        records = server.mirageServer.db.incident,
        filteredRecords = [];

      if (statusFilter && Ember.typeOf(statusFilter.value) !== 'undefined') {
        filteredRecords = records.where({ statusSort: statusFilter.value });
      } else if (statusFilter && Ember.typeOf(statusFilter.values) !== 'undefined') {
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

      let response = [],
        incident = server.mirageServer.db.incident.where({ 'statusSort': 0 });
      incident = incident.toArray();
      // update the first 10 incidents
      let someIncidents = incident.slice(0, 10);
      someIncidents.forEach((incident) => {
        incident.riskScore = Math.min(99, (10 + Math.round(100 * Math.random())));
        response.push(incident);
      });

      response.push(IncidentSamples.newIncident, IncidentSamples.assignedIncident, IncidentSamples.inProgressIncident);

      server.mirageServer.db.incident.insert(IncidentSamples.newIncident);
      server.mirageServer.db.incident.insert(IncidentSamples.assignedIncident);
      server.mirageServer.db.incident.insert(IncidentSamples.inProgressIncident);

      server.streamList(
        response,
        frames[0].body.page,
        null,
        frames);
    });
  });

  server.route('incident', 'findRecord', function(message, frames, server) {
    let frame = (frames && frames[0]) || {},
      incident = server.mirageServer.db.incident.find(frame.body.id),
      map = _makeAlertsMap(server.mirageServer.db.alerts);

    if (map) {
      incident.alerts = !map[incident.id] ? [] : map[incident.id].map((alert) => {
        let json = {};
        json.alert = alert.alert;
        return json;
      });
    }
    incident.riskScore = Math.min(99, (10 + Math.round(100 * Math.random())));
    incident.prioritySort = Math.round(3 * Math.random());
    incident.id = frame.body.id;
    incident.type = 'incident';

    server.sendFrame('MESSAGE', {
        subscription: (frame.headers || {}).id || '',
        'content-type': 'application/json'
      },
      {
        code: 0,
        data: incident,
        request: frame.body
      });
  });

  server.route('incident', 'updateRecord', function(message, frames, server) {
    let frame = (frames && frames[0]) || {},
      incident = server.mirageServer.db.incident.update(frame.body.id, frame.body);

    server.sendFrame('MESSAGE', {
      subscription: (frame.headers || {}).id || '',
      'content-type': 'application/json'
    }, {
      code: 0,
      data: incident,
      request: frame.body
    });
  });
}
