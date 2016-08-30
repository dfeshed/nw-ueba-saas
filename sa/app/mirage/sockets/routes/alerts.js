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
  server.route('alerts', 'stream', function(message, frames, server) {
    let frame = (frames && frames[0]) || {};
    let incident = server.mirageServer.db.incident.find(frame.body.filter[0].value);
    let alerts = [];
    let map = _makeAlertsMap(server.mirageServer.db.alerts);

    if (map) {
      alerts = !map[incident.id] ? [] : map[incident.id].map((alert) => {
        let json = {};
        json.alert = alert.alert;
        return json;
      });
    }
    server.streamList(
      alerts,
      frames[0].body.page,
      null,
      frames);
  });
}
