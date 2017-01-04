export default function(server) {
  server.route('events', 'stream', function(message, frames, server) {
    const frame = (frames && frames[0]) || {};
    // const events = server.mirageServer.db.events.where({ _id: frame.body.filter[0].value });
    let events = [];
    const indicator = server.mirageServer.db['incident-storyline'][0].relatedIndicators.find(function(indicator) {
      if (indicator.indicator.id === frame.body.filter[0].value) {
        return true;
      }
    });
    if (indicator) {
      events = indicator.indicator.alert.events;
    }

    server.streamList(
      events,
      frames[0].body.page,
      null,
      frames);

  });
}
