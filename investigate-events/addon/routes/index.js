import Route from 'ember-route';
import service from 'ember-service/inject';

import {
  initializeDictionaries,
  initializeServices
} from 'investigate-events/actions/data-creators';

export default Route.extend({
  accessControl: service(),
  redux: service(),

  beforeModel() {
    // Re-route back to the parent's protected route if we don't have permission
    if (!this.get('accessControl.hasInvestigateAccess')) {
      this.transitionToExternal('protected');
    } else {
      // Get services
      this.get('redux').dispatch(initializeServices());
    }
  },

  model() {
    // Expose the parent route's state data to this child route's template.
    // TODO - eventually remove this when all Reduxed-up
    return this.modelFor('application');
  },

  actions: {
    /**
     * Invoked by UI to kicks off a query to a given core service for a given
     * time range & filter. Actually just navigates to the route for the query
     * results, which in turn will truly submit the query.
     * @param {string} serviceId The ID of the core service to be queried
     * @param {number} startTime The start time to be queried, in milliseconds
     * @param {number} endTime The end time to be queried, in milliseconds
     * @param {number} metaFilter Optional where clause for meta filter
     * @public
     */
    submitQuery(serviceId, startTime, endTime, metaFilter) {
      // Reset the current query pointer before navigating to the results UI for
      // a new query.  For 2 reasons:
      // (1) It ensures the new query is added to the query tree at the root
      // level, not as a child of last query. We only want to add a child to a
      // query if the child is a drill from that query's results.
      // (2) Performance optimization: It ensures that the results UI does not
      // render the last query's results before fetching the new query's
      // results.
      // this.sendAction('navGoto', null);

      // Get `language` and `aliases` now that we know what service we're using
      this.get('redux').dispatch(initializeDictionaries());

      // Navigate to results UI.
      this.transitionTo('query', [
        serviceId,
        startTime || 0,
        endTime || 0,
        metaFilter || ''
      ].join('/'));
    }
  }
});
