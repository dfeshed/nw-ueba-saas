/**
 * @file MockServer message handlers that respond to requests regarding incident model(s).
 * Here we can register handlers for requests related to incidents, such as streaming a list of incidents, fetching
 * a single incident record by id, or updating incidents.
 * @public
 */
export default function(server) {
  server.route('incident', 'stream', function(message, frames, server) {

    // Wait until after all incidents DB has been loaded from a JSON file.
    server.asyncFixturesPromise.then(() => {

      /*
      For demo: ignore any given requested filters; just apply the paging.
      For future reference, filtering could be accomplished here as follows:
      ```js
      let { filter } = frames[0].body,
        assigneeFilter = (filter || []).findBy('field', 'assignee') || {},
        records = server.mirageServer.db['incident'],
        filteredRecords = !assigneeFilter.value ? records : records.where({ assignee: assigneeFilter.value });
      ```
      */
      server.streamList(
        server.mirageServer.db.incident,
        frames[0].body.page,
        null,
        frames);
    });
  });
}
