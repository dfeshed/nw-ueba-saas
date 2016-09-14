export default function(server) {
  server.route('storyline', 'stream', function(message, frames, server) {
    let dbs = server.mirageServer.db;
    let data = (typeof dbs['incident-storyline'] !== 'undefined') ?  dbs['incident-storyline'][ 0 ] : [];
    server.sendList(
      data,
      null,
      null,
      frames);
  });
}
