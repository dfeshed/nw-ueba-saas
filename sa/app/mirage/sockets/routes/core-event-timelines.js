export default function(server) {

  // Mock the response for a promise request of 'core-event-timeline' with the entire mirage DB collection
  // "core-event-timelines" (after randomizing the counts for some variety in testing):
  server.route('core-event-timeline', 'query', function(message, frames, server) {
    let clone = [].concat(server.mirageServer.db['core-event-timelines']);
    clone.forEach((datum) => {
      datum.count = parseInt(datum.count * Math.random(), 10);
    });
    server.sendList(
      clone,
      null,
      null,
      frames);
  });
}
