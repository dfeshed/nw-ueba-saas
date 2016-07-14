import Ember from 'ember';
import Query from 'sa/utils/query';
import QueryTreeNode from 'sa/utils/tree/query-node';

const { Route } = Ember;

export default Route.extend({
  queryParams: {
    eventId: {
      refreshModel: true,
      replace: true
    }
  },

  /**
   * Returns the app state model from the parent route. Is also responsible for parsing in the coming query params
   * and ensuring that the incoming query is included in the app state.
   * (1) Checks if app state's current event match the incoming params; if not, updates the app state to match the params.
   * (2) Checks if a matching query is already in the app state's tree of queries; if not, adds it.
   * (3) Moves the app state's playhead to point to the query so it will presented to the end-user.
   * Note that we do not modify app state directly here; we merely dispatch actions to request changes.
   * @workaround Note: Ember.Route's send() works normally but throws an error message if you call it from within `model()`
   * and you are coming from a bookmark directly to this route. The message tells us to use `transition.send()` instead.
   * @param {object} params
   * @param {object} transition
   * @returns {object} The state model from the parent route.
   * @public
   */
  model(params, transition) {
    const state = this.modelFor('protected.investigate');

    let { filter, eventId } = params,
      [ serviceId, startTime, endTime, metaFilter ] = (filter || '').split('/'),
      filterParams = { serviceId, startTime, endTime, metaFilter };

    // Does the app state's current event match the incoming params?
    if ((state.get('currentEvent.eventId') !== eventId) || (state.get('currentEvent.serviceId') !== serviceId)) {
      transition.send('moveEventPlayhead', serviceId, eventId);
    }

    // Do we already have a query that matches the incoming params?
    let queryNode = state.get('queries').find(filterParams);
    if (!queryNode) {
      // No matching query found, so add a new node to the query tree for this query.
      queryNode = QueryTreeNode.create({ value: Query.create(filterParams) });
      transition.send('addQueryNode', queryNode, state.get('currentQuery'));
    }

    // Move the playhead to this query.
    transition.send('moveQueryPlayhead', queryNode);
    return state;
  }
});
