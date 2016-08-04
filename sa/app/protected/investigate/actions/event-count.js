/**
 * @file Investigation Route Events Total Actions
 * Route actions related to fetching the total count of events that match the current query.
 * These actions assume that the query is accessible via `this.get('state.query')`.
 * @public
 */
import Ember from 'ember';
import makeServerInputsForQuery from './helpers/make-server-inputs-for-query';

const { Mixin } = Ember;

export default Mixin.create({
  actions: {
    /**
     * Fetches the list of available Core services from web server; stores it in `state.services`.
     * The server stream and the resulting records are stored in `state.currentEvents`'s `stream` & `records` respectively.
     * @param {boolean} [forceReload] If truthy, indicates that the records should be fetched from server. Otherwise,
     * re-uses previous server call (if any) as long as it didn't error out.
     * @public
     */
    eventCountGet(forceReload = false) {
      let query = this.get('state.query.value');
      let oldQuery = this.get('state.eventCount.query');
      let eventCount = this.get('state.eventCount');
      let skipLoad = !forceReload && query && query.isEqual(oldQuery) && (eventCount.get('status') !== 'rejected');
      if (skipLoad) {
        return;
      }

      if (!query) {
        return;
      }

      // Wire up the server call to state.eventCount and fire it.
      let stream = this.store.stream(
        'core-event-count',
        makeServerInputsForQuery(query)
      );
      stream.subscribe({
        onNext(response) {
          eventCount.setProperties({
            status: 'resolved',
            data: response.data
          });
          stream.completed();
        },
        onError(response) {
          eventCount.setProperties({
            status: 'rejected',
            reason: response.code
          });
        }
      });

      // Cache references to the query & stream in the route state.
      eventCount.setProperties({
        query,
        stream,
        status: 'wait'
      });

      stream.start();
    },

    /**
     * Stops the stream (if any) that is fetching the event count for the current query.
     * @public
     */
    eventCountStop() {
      let stream = this.get('state.eventCount.stream');
      if (stream) {
        stream.stop();
      }
    }
  }
});
