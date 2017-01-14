export default function(server) {
  server.route('live-search-categories', 'findAll', function(message, frames, server) {
    const dbs = server.mirageServer.db;
    const data = dbs['live-search'][0].categories;
    server.sendList(
        data,
        null,
        null,
        frames);
  });

  server.route('live-search-resource-types', 'findAll', function(message, frames, server) {
    const dbs = server.mirageServer.db;
    const data = dbs['live-search'][0].resourceTypes;
    server.sendList(
        data,
        null,
        null,
        frames);
  });

  server.route('live-search-mediums', 'findAll', function(message, frames, server) {
    const dbs = server.mirageServer.db;
    const data = dbs['live-search'][0].media;
    server.sendList(
        data,
        null,
        null,
        frames);
  });


  server.route('live-search-meta-keys', 'findAll', function(message, frames, server) {
    const dbs = server.mirageServer.db;
    const data = dbs['live-search'][0].metaKeys;
    server.sendList(
        data,
        null,
        null,
        frames);
  });

  server.route('live-search-meta-values', 'findAll', function(message, frames, server) {
    const dbs = server.mirageServer.db;
    const data = dbs['live-search'][0].metaValues;
    server.sendList(
        data,
        null,
        null,
        frames);
  });

  server.route('live-search', 'query', function(message, frames, server) {
    const dbs = server.mirageServer.db;
    const data = dbs['live-search'][0].searchResult;
    server.sendList(
        data,
        null,
        null,
        frames);
  });
}
