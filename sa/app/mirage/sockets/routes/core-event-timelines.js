export default function(server) {

  // Mock the response for a promise request of 'core-event-timeline' with the entire mirage DB collection "core-event-timelines":
  server.route('core-event-timeline', 'query', function(message, frames, server) {
    server.sendList(
      server.mirageServer.db['core-event-timelines'],
      null,
      null,
      frames);
  });
}
