export default function(server) {

  // Mock the response for store.findAll('core-service') with the entire mirage DB collection "core-services":
  server.route('core-service', 'findAll', function(message, frames, server) {
    server.sendList(
      server.mirageServer.db['core-services'],
      null,
      null,
      frames);
  });
}
