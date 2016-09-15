/**
 * @file MockServer message handlers that respond to requests regarding context model(s).
 * Here we can register handlers for requests related to context, such as streaming context data and prefetch.
 * @public
 */
export default function(server) {
  server.route('context', 'stream', function(message, frames, server) {

      // Wait until after all context DB has been loaded from a JSON file.
    server.asyncFixturesPromise.then(() => {

      server.streamList(
        server.mirageServer.db.context,
        frames[0].body.page,
        null,
        frames);
    });
  });

}
