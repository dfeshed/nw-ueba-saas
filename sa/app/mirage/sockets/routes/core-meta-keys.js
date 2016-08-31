export default function(server) {

  // Mock the response for store.query('core-meta-key') with the entire mirage DB collection "core-meta-keys":
  server.route('core-meta-key', 'query', function(message, frames, server) {
    server.sendList(
      server.mirageServer.db['core-meta-keys'],
      null,
      null,
      frames);
  });
}
