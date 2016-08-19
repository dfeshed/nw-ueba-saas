/**
 * @file Investigate Route Events Actions
 * Route actions related to fetching a stream of events from a Netwitness Core query.
 * These actions assume that the state is accessible via `this.get('state')`.
 * @public
 */
import Ember from 'ember';
import { buildEventStreamInputs, executeEventsRequest } from './helpers/query-utils';

const { Mixin } = Ember;

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
        data: [],
        anchor: 0,
        goal: STREAM_LIMIT
      });

      // @workaround ASOC-22125: Due to a server issue, a query for records that doesn't match any events will never
      // return a response back to the client. That will cause our UI to wait endlessly. To workaround, before
      // submitting the query, check if the (separate) server call for the event count has already returned zero.
      // If so, skip the query for the records.  (If the event count hasn't come back yet, no worries, submit this
      // query for now. We'll also add a check for count=0 in the count response callback, and that check will
      // abort this server call if need be.)
      let eventCountStatus = this.get('state.eventCount.status');
      let eventCountData = this.get('state.eventCount.data');
      let eventCountQuery = this.get('state.eventCount.query');
      let eventCountIsZero = (eventCountStatus === 'resolved') &&
        (eventCountData === 0) &&
        query && query.isEqual(eventCountQuery);
      if (eventCountIsZero) {
        events.set('status', 'complete');
        return;
      }
      // end @workaround

      const inputs = buildEventStreamInputs(query, STREAM_LIMIT, STREAM_BATCH);

      executeEventsRequest(this.request, inputs, events);
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

      events.setProperties({
        anchor,
        goal
      });

      const inputs = buildEventStreamInputs(query, limit, STREAM_BATCH, lastSessionId);

      executeEventsRequest(this.request, inputs, events);
    },

    /**
     * Stops the current query to fetch events while it is in progress.
     *
     * Executed, for instance, when a user wants to pause a query
     *
     * @param {string} [newStatus="idle"] Indicates what the events state object's "status" should be updated to.
     * Typically it is set to "idle" by default. One exception: In the scenario when a query for events count has
     * returned zero, we know that there are no events coming, and so a "complete" status will be passed in.
     * @public
     */
    eventsStop(newStatus) {
      let events = this.get('state.events') || {};
      if (events.get('status') === 'streaming') {
        events.get('stopStreaming')();
        // @workaround until event-count properly sends complete message
        if (newStatus) {
          events.set('status', newStatus);
        }
        // end @workaround
      }
    }
  }
});