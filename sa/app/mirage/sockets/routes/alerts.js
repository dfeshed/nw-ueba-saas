export default function(server) {
  server.route('alerts', 'stream', function(message, frames, server) {
    // const frame = (frames && frames[0]) || {};
    // const alerts = server.mirageServer.db.alerts.where({ incidentId: frame.body.filter[0].value });
    const alerts = [];
    const storyline = server.mirageServer.db['incident-storyline'][0].relatedIndicators;
    const catalystIndicator = storyline.filterBy('group', '0');
    catalystIndicator.forEach(function(indicator) {
      alerts.pushObject(indicator.indicator);
    });
    server.streamList(
      alerts,
      frames[0].body.page,
      null,
      frames);

  });
}
