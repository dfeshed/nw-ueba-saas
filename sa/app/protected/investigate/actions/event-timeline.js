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
     * Fetches the timeline data of Core events from web server; stores it in `state.eventTimeline`.
     * @param {boolean} [forceReload] If truthy, indicates that the records should be fetched from server. Otherwise,
     * re-uses previous server call for same query (if any) as long as it didn't error out.
     * @public
     */
    eventTimelineGet(forceReload = false) {
      let query = this.get('state.query.value');
      let oldQuery = this.get('state.eventTimeline.query');
      let eventTimeline = this.get('state.eventTimeline');
      let skipLoad = !forceReload && query && query.isEqual(oldQuery) && (eventTimeline.get('status') !== 'rejected');
      if (skipLoad) {
        return;
      }

      if (!query) {
        return;
      }

      // Cache references to the query & stream in the route state.
      eventTimeline.setProperties({
        query,
        status: 'wait',
        data: undefined
      });

      this.request.promiseRequest({
        method: 'query',
        modelName: 'core-event-timeline',
        query: makeServerInputsForQuery(query)
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
    }
  }
});
