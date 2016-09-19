/**
 * @file Investigation Route Event Timeline Actions
 * Route actions related to fetching the timeline of events that match the current query.
 * These actions assume that the query is accessible via `this.get('state.query')`.
 * @public
 */
import Ember from 'ember';
import { makeServerInputsForQuery } from './helpers/query-utils';

const { Mixin } = Ember;

export default Mixin.create({
  actions: {
    /**
     * Fetches the timeline data for the given query node. Stores the request's state in node's `value.results.eventTimeline`.
     * @param {object} queryNode
     * @param {boolean} [forceReload] If truthy, indicates that the records should be fetched from server. Otherwise,
     * re-uses previous server call for same query (if any) as long as it didn't error out.
     * @public
     */
    eventTimelineGet(queryNode, forceReload = false) {
      if (!queryNode) {
        return;
      }
      let eventTimeline = queryNode.get('value.results.eventTimeline');
      let skipLoad = !forceReload && (eventTimeline.get('status') || '').match(/wait|resolved/);
      if (skipLoad) {
        return;
      }

      // Cache references to the request in the state object.
      eventTimeline.setProperties({
        status: 'wait',
        data: undefined
      });

      this.request.promiseRequest({
        method: 'query',
        modelName: 'core-event-timeline',
        query: makeServerInputsForQuery(
          queryNode.get('value.definition'),
          queryNode.get('value.language.data')
        )
      }).then(function({ data }) {
        eventTimeline.setProperties({
          status: 'resolved',
          data
        });
      }).catch(function({ code }) {
        eventTimeline.setProperties({
          status: 'rejected',
          reason: code
        });
      });
    },

    /**
     * Resets the timeline data for a given query node back to empty.
     * Used to reduce memory consumption from a node that is no longer currently active.
     * @param {object} queryNode The query to be cleared.
     * @public
     */
    eventTimelineClear(queryNode) {
      if (!queryNode) {
        return;
      }
      queryNode.get('value.results.eventTimeline').reset();
    }
  }
});
