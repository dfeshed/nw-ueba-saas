export default function(server) {
  server.route('category-tags', 'stream', function(message, frames, server) {
    server.sendList(
      server.mirageServer.db['category-tags'],
      null,
      null,
      frames);
  });
}
