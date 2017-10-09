/**
 * @file Investigate Route Events Actions
 * Route actions related to fetching a stream of events from a NetWitness Core query.
 * These actions assume that the state is accessible via `this.get('state')`.
 * @public
 */
import Ember from 'ember';
import service from 'ember-service/inject';
import {
  buildEventStreamInputs,
  executeEventsRequest,
  buildEventLogStreamInputs,
  executeLogDataRequest
} from './helpers/query-utils';

const { Mixin } = Ember;

const STREAM_LIMIT = 1000;
const STREAM_BATCH = 19;

export default Mixin.create({
  redux: service(),
  actions: {
    /**
     * Fetches a stream of events for the given query node. Stores the stream's state in node's `value.results.events`.
     * Re-uses any previous results for the same query, UNLESS `forceReload` is truthy.
     * @param {object} queryNode
     * @param {boolean} [forceReload=false] If truthy, any previous results for the same query are discarded.
     * @public
     */
    eventsGetFirst(queryNode, forceReload = false) {
      const { language } = this.get('redux').getState().investigate.dictionaries;
      if (!queryNode) {
        return;
      }
      const events = queryNode.get('value.results.events');
      const skipLoad = !forceReload && (events.get('status') || '').match(/streaming|complete|stopped/);
      if (skipLoad) {
        return;
      }

      // Prepare events state object for a new request.
      events.setProperties({
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
      const eventCountStatus = queryNode.get('value.results.eventCount.status');
      const eventCountData = queryNode.get('value.results.eventCount.data');
      const eventCountIsZero = (eventCountStatus === 'resolved') && (eventCountData === 0);
      if (eventCountIsZero) {
        events.set('status', 'complete');
        return;
      }
      // end @workaround

      const inputs = buildEventStreamInputs(
        queryNode.get('value.definition'),
        language,
        STREAM_LIMIT,
        STREAM_BATCH
      );

      executeEventsRequest(this.request, inputs, events);
    },

    /**
     * Streams additional events for the current query, if the query is not already streaming and not complete.
     * Any previous results found are appended to, not discarded.
     * @public
     */
    eventsGetMore(queryNode) {
      const { language } = this.get('redux').getState().investigate.dictionaries;
      if (!queryNode) {
        return;
      }

      // Wire up stream to state.events and start streaming.
      const events = queryNode.get('value.results.events');
      const len = events.get('data.length') || 0;
      const limit = STREAM_LIMIT; // for now, always fetch STREAM_LIMIT; future: consider computing limit from len?
      const anchor = len;
      const goal = len + limit;
      const lastSessionId = len ? events.get('data.lastObject.sessionId') : null;

      events.setProperties({
        anchor,
        goal
      });

      const inputs = buildEventStreamInputs(
        queryNode.get('value.definition'),
        language,
        limit,
        STREAM_BATCH,
        lastSessionId
      );

      executeEventsRequest(this.request, inputs, events);
    },

    /**
     * Stops the given query to fetch events while it is in progress.
     *
     * Executed, for instance, when a user wants to pause a query
     * @param {object} queryNode The query to be stopped.
     * @param {string} [newStatus] Indicates what the events state object's "status" should be updated to.
     * Typically it is set to "idle" by default. One exception: In the scenario when a query for events count has
     * returned zero, we know that there are no events coming, and so a "complete" status will be passed in.
     * @public
     */
    eventsStop(queryNode, newStatus) {
      if (!queryNode) {
        return;
      }
      const events = queryNode.get('value.results.events');
      if (events.get('status') === 'streaming') {
        events.get('stopStreaming')();
        // @workaround until event-count properly sends complete message
        if (newStatus) {
          events.set('status', newStatus);
        }
        // end @workaround
      }
    },

    /**
     * Resets the events list state for a given query node back to empty. Also stops the query if still in progress.
     * Used to reduce memory consumption from a node that is no longer currently active.
     * @param {object} queryNode The query to be cleared.
     * @public
     */
    eventsClear(queryNode) {
      if (!queryNode) {
        return;
      }
      this.send('eventsStop', queryNode);
      queryNode.get('value.results.events').reset();
    },

    /**
     * Kicks off the fetching of log data for a given array of events.
     * @param {object} queryNode The query which owns the given event records.
     * @param {object[]} events The array of event records.
     * @public
     */
    eventsLogsGet(queryNode, events = []) {
      const inputs = buildEventLogStreamInputs(
        queryNode.get('value.definition.serviceId'),
        events.mapBy('sessionId')
      );

      executeLogDataRequest(this.request, inputs, events);
    }
  }
});
