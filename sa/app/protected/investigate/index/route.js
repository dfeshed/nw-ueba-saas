import Ember from 'ember';

const { Route } = Ember;

export default Route.extend({
  model() {
    // Expose the parent route's state data to this child route's template.
    return this.modelFor('protected.investigate');
  },

  actions: {
    /**
     * Invoked by UI to kicks off a query to a given core service for a given time range & filter.
     * Actually just navigates to the route for the query results, which in turn will truly submit the query.
     * @param {string} serviceId The ID of the core service to be queried.
     * @param {number} startTime The start time to be queried, in number of seconds since 1970.
     * @param {number} endTime The end time to be queried, in number of seconds since 1970.
     * @param {number} metaFilter Optional where clause for meta filter.
     * @public
     */
    submitQuery(serviceId, startTime, endTime, metaFilter) {
      this.transitionTo('protected.investigate.query', [
        serviceId,
        startTime || 0,
        endTime || 0,
        metaFilter || ''
      ].join('/'));
    }
  }
});
