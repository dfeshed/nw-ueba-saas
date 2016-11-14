export default function(server) {
  server.route('storyline', 'stream', function(message, frames, server) {
    const dbs = server.mirageServer.db;
    const data = (typeof dbs['incident-storyline'] !== 'undefined') ? dbs['incident-storyline'][ 0 ] : [];
    server.sendList(
      data,
      null,
      null,
      frames);
  });
}
