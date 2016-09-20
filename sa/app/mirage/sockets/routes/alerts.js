export default function(server) {
  server.route('alerts', 'stream', function(message, frames, server) {
    let frame = (frames && frames[0]) || {};
    let alerts = server.mirageServer.db.alerts.where({ incidentId: frame.body.filter[0].value });

    server.streamList(
      alerts,
      frames[0].body.page,
      null,
      frames);

  });
}
