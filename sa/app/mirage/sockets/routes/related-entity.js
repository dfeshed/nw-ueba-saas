export default function(server) {
  server.route('related-entity', 'stream', function(message, frames, server) {
    server.streamList(
      server.mirageServer.db['related-entity'],
      frames[0].body.page,
      null,
      frames);
  });
}
