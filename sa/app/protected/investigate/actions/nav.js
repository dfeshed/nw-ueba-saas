import Ember from 'ember';
import Query from '../state/query';
import TreeNode from 'sa/utils/tree/node';

const { Mixin } = Ember;

/**
 * @file Investigate Route Navigational Actions
 * Route actions related to navigating from one query/event to another.
 * These actions assume that the state is accessible via `this.get('state')`.
 * @public
 */

export default Mixin.create({
  actions: {
    /**
     * Moves the current query playhead to point to the given query node. Then fires subsequent action to ensure
     * that the current's events are fetched.
     * @param {object} queryNode The node to which the playhead will be moved.
     * @public
     */
    navGoto(queryNode) {
      this.set('state.query', queryNode);
      this.send('eventsGetFirst');
      this.send('eventCountGet');
      this.send('eventTimelineGet');
    },

    /**
     * Searches for an existing query in the queries tree that matches the given params; if not found,
     * adds it to the tree.  Then navigates to that query.
     * @param {object} filterParams Hash of filter parameters for the query to be searched for. The properties of
     * this object correspond to the public attributes of the sa/utils/query/query class.
     * @see sa/utils/query/query
     * @public
     */
    navFindOrAdd(filterParams) {

      // Do we already have a query that matches the incoming params?
      let state = this.get('state');
      let queryTree = state.get('queryTree');
      let queryNode = queryTree.find(filterParams);
      if (!queryNode) {

        // No matching query found, so add a new node to the query tree for this query.
        queryNode = TreeNode.create({
          value: Query.create(filterParams)
        });
        queryTree.add(queryNode, state.get('query'));
      }

      // Move the playhead to this query.
      this.send('navGoto', queryNode);
    }
  }
});
