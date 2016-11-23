export default function(server) {
  server.route('events', 'stream', function(message, frames, server) {
    const frame = (frames && frames[0]) || {};

    const events = server.mirageServer.db.events.where({ _id: frame.body.filter[0].value });

    server.streamList(
      events,
      frames[0].body.page,
      null,
      frames);

  });
}
