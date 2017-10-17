/**
 * @file Investigate Route Events Actions
 * Route actions related to fetching a stream of events from a NetWitness Core query.
 * These actions assume that the state is accessible via `this.get('state')`.
 * @public
 */
import Ember from 'ember';
// import service from 'ember-service/inject';
// import {
//   buildEventStreamInputs,
//   executeEventsRequest,
//   buildEventLogStreamInputs,
//   executeLogDataRequest
// } from './helpers/query-utils';

const { Mixin } = Ember;

// const STREAM_LIMIT = 1000;
// const STREAM_BATCH = 19;

export default Mixin.create({
  // redux: service(),
  // actions: {
  //   /**
  //    * Stops the given query to fetch events while it is in progress.
  //    *
  //    * Executed, for instance, when a user wants to pause a query
  //    * @param {object} queryNode The query to be stopped.
  //    * @param {string} [newStatus] Indicates what the events state object's "status" should be updated to.
  //    * Typically it is set to "idle" by default. One exception: In the scenario when a query for events count has
  //    * returned zero, we know that there are no events coming, and so a "complete" status will be passed in.
  //    * @public
  //    */
  //   eventsStop(queryNode, newStatus) {
  //     if (!queryNode) {
  //       return;
  //     }
  //     const events = queryNode.get('value.results.events');
  //     if (events.get('status') === 'streaming') {
  //       events.get('stopStreaming')();
  //       // @workaround until event-count properly sends complete message
  //       if (newStatus) {
  //         events.set('status', newStatus);
  //       }
  //       // end @workaround
  //     }
  //   },

  //   /**
  //    * Resets the events list state for a given query node back to empty. Also stops the query if still in progress.
  //    * Used to reduce memory consumption from a node that is no longer currently active.
  //    * @param {object} queryNode The query to be cleared.
  //    * @public
  //    */
  //   eventsClear(queryNode) {
  //     if (!queryNode) {
  //       return;
  //     }
  //     this.send('eventsStop', queryNode);
  //     queryNode.get('value.results.events').reset();
  //   },

  //   /**
  //    * Kicks off the fetching of log data for a given array of events.
  //    * @param {object} queryNode The query which owns the given event records.
  //    * @param {object[]} events The array of event records.
  //    * @public
  //    */
  //   eventsLogsGet(queryNode, events = []) {
  //     const inputs = buildEventLogStreamInputs(
  //       queryNode.get('value.definition.serviceId'),
  //       events.mapBy('sessionId')
  //     );

  //     executeLogDataRequest(this.request, inputs, events);
  //   }
  // }
});
