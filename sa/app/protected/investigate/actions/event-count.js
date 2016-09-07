/**
 * @file Investigation Route Events Total Actions
 * Route actions related to fetching the total count of events that match the current query.
 * These actions assume that the query is accessible via `this.get('state.query')`.
 * @public
 */
import Ember from 'ember';
import { makeServerInputsForQuery } from './helpers/query-utils';

const { Mixin } = Ember;

export default Mixin.create({
  actions: {
    /**
     * Fetches the count of event results for the given query node. Stores the request's state in node's `value.results.eventCount`.
     * @param {object} queryNode The query whose results count are to be fetched from server.
     * @param {boolean} [forceReload] If truthy, indicates that the count should be fetched from server. Otherwise,
     * re-uses previous server call results (if any) as long as it didn't error out.
     * @public
     */
    eventCountGet(queryNode, forceReload = false) {
      if (!queryNode) {
        return;
      }
      let eventCount = queryNode.get('value.results.eventCount');
      let skipLoad = !forceReload && (eventCount.get('status') || '').match(/wait|resolved/);
      if (skipLoad) {
        return;
      }

      // Cache references to the request in the state object.
      eventCount.setProperties({
        status: 'wait',
        data: undefined
      });

      this.request.promiseRequest({
        method: 'stream',
        modelName: 'core-event-count',
        query: makeServerInputsForQuery(queryNode.get('value.definition'))
      }).then(({ data }) => {
        eventCount.setProperties({
          status: 'resolved',
          data
        });

        // @workaround ASOC-22125: Due to a server issue, a query for records that doesn't match any events will never
        // return a response back to the client. That will cause our UI to wait endlessly. To workaround, when this
        // server call returns with the event count, check if it returns zero. If so, abort any in-progress call for
        // the event records, marking it complete (since we know there aren't any records coming back).
        if (data === 0) {
          this.send('eventsStop', queryNode, 'complete');
        }
      }).catch(function({ code }) {
        eventCount.setProperties({
          status: 'rejected',
          reason: code
        });
      });
    }
  }
});
