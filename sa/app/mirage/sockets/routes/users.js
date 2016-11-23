export default function(server) {
  server.route('users', 'stream', function(message, frames, server) {
    server.sendList(
      server.mirageServer.db.users,
      null,
      null,
      frames);
  });
}
