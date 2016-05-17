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
      /*
      For demo: ignore any given requested filters; just apply the paging.
      For future reference, filtering could be accomplished here as follows:
      ```js
      let { filter } = frames[0].body,
        assigneeFilter = (filter || []).findBy('field', 'assignee') || {},
        records = server.mirageServer.db['incident'],
        filteredRecords = !assigneeFilter.value ? records : records.where({ assignee: assigneeFilter.value });
      ```
      */
      let { filter } = frames[0].body,
        assigneeFilter = (filter || []).findBy('field', 'statusSort') || {},
        records = server.mirageServer.db.incident,
        filteredRecords = records.where({ statusSort: assigneeFilter.value });

      let map = _makeAlertsMap(server.mirageServer.db.alerts);

      filteredRecords = filteredRecords.map((incident) => {
        incident.riskScore = 10 + Math.round(100 * Math.random());
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
}
