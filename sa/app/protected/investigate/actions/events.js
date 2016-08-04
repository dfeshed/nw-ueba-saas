/**
 * @file Investigate Route Events Actions
 * Route actions related to fetching a stream of events from a Netwitness Core query.
 * These actions assume that the state is accessible via `this.get('state')`.
 * @public
 */
import Ember from 'ember';
import makeEventsStream from './helpers/make-events-stream';
import wireEventsStreamToState from './helpers/wire-events-stream-to-state';

const { getProperties, Mixin } = Ember;

const STREAM_LIMIT = 1000;
const STREAM_BATCH = 19;

export default Mixin.create({
  actions: {
    /**
     * Fetches a stream of events for the current query.
     * Re-uses any previous results for the same query, UNLESS `forceReload` is truthy.
     * @param {boolean} [forceReload=false] If truthy, any previous results for the same query are discarded.
     * @public
     */
    eventsGetFirst(forceReload = false) {
      let query = this.get('state.query.value');
      let oldQuery = this.get('state.events.query');
      let events = this.get('state.events');
      let skipLoad = !forceReload &&
        query && query.isEqual(oldQuery) &&
        (events.get('status') || '').match(/streaming|complete/);
      if (skipLoad) {
        return;
      }
      // Prepare state.events object for a new request.
      events.setProperties({
        query,
        data: []
      });

      // Wire up stream to state.events and start streaming.
      wireEventsStreamToState(
        makeEventsStream(this.store, query, STREAM_LIMIT, STREAM_BATCH),
        events,
        STREAM_LIMIT
      );
    },

    /**
     * Streams additional events for the current query, if the query is not already streaming and not complete.
     * Any previous results found are appended to, not discarded.
     * @public
     */
    eventsGetMore() {
      let query = this.get('state.query.value');
      let events = this.get('state.events');
      if (!query || !events) {
        return;
      }

      // Wire up stream to state.events and start streaming.
      let len = events.get('data.length') || 0;
      let limit = STREAM_LIMIT; // for now, always fetch STREAM_LIMIT; future: consider computing limit from len?
      let anchor = len;
      let goal = len + limit;
      let lastSessionId = len ? events.get('data.lastObject.sessionId') : null;
      wireEventsStreamToState(
        makeEventsStream(this.store, query, limit, STREAM_BATCH, lastSessionId),
        events,
        goal,
        anchor
      );
    },

    /**
     * Stops the current query to fetch events while it is in progress.
     * @public
     */
    eventsStop() {
      let events = this.get('state.events') || {};
      let { stream, status } = getProperties(events, 'stream', 'status');
      if (stream && (status === 'streaming')) {
        stream.stop();
        events.set('status', 'idle');
      }
    }
  }
});
