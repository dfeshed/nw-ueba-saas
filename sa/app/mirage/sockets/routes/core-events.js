import Ember from 'ember';

const MIN_EVENT_COUNT = 500;

const { get } = Ember;

export default function(server) {

  // Mock the response for store.stream('core-event') with records from the mirage DB collection "core-events":
  server.route('core-event', 'stream', function(message, frames, server) {
    let [ firstFrame ] = frames;
    let { body: { filter, stream: { limit } } } = firstFrame;
    let all = server.mirageServer.db['core-events'];

    // Generate more mock data if we only have a little.  We do this here, on-demand, rather than at app startup,
    // because it would slow down the app startup for all devs, even ones who never fetch this data.
    if (all.length < MIN_EVENT_COUNT) {
      server.mirageServer.createList('core-events', MIN_EVENT_COUNT - all.length);
      all = server.mirageServer.db['core-events'];
    }

    let query = (filter || []).findBy('field', 'query');
    let queryValue = query && query.value;
    let match = queryValue ? String(queryValue).match(/\(sessionid > ([0-9]+)\)/) : null;
    let sessionId = match && parseInt(match[1], 10);
    let results = !sessionId ? all : all.filter(function(evt) {
      return evt.sessionId > sessionId;
    });
    results = limit ? results.slice(0, limit) : results;

    server.streamList(
      results,
      null,       // don't page, results have already been sliced
      all.length, // total = hard-coded: size of entire mirage 'core-events' collection
      frames,
      0,
      get(all, 'lastObject') === get(results, 'lastObject')  // send `complete: true` if you reach the end of the collection
    );
  });
}
