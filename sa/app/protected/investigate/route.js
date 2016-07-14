import Ember from 'ember';
import Tree from 'sa/utils/tree/tree';

const {
  computed,
  Object: EmberObject,
  Route
} = Ember;

export default Route.extend({
  model() {
    return this.get('state');
  },

  /**
   * This `state` object acts as the "local app state" for the investigate route (and its subroutes).  It is somewhat
   * analogous similar to the "app state" concept in Flux. It should not be mutated (changed) directly by the components
   * in this route; rather, those components should call this route's actions, which are then responsible for
   * changing the state.
   * This state is intended to live for the lifetime of the user's session. It'll span various queries, not just one.
   * Therefore, we keep it cached in a computed property "state", rather than recreating it from scratch every time
   * this route's model hook is called. In the future, we may want to move it up to a parent route (like `protected`)
   * and extend it to include app state from other routes besides investigate (e.g., state from the respond route).
   * @returns {object}
   * @public
   */
  state: computed(function() {
    return EmberObject.extend({

      // List of available Core services. User can choose one to query.
      coreServices: computed(() => {
        return this.store.findAll('core-service');
      }),

      // Tree of queries executed (if any) during this user's session. Used for tracking an investigation's path.
      queries: computed(() => {
        return Tree.create();
      }),

      // Pointer to the last active query node in `queries` tree.
      currentQuery: null,

      // Pointer to the last selected event from query results.
      currentEvent: null

    }).create();
  }),

  actions: {
    /**
     * Adds a given query node as a child of another given node, and then
     * moves the playhead to the given child.
     * @param {object} childQueryNode The node to be added.
     * @param {object} parentQueryNode The node to which the child node will be added.
     * @public
     */
    addQueryNode(childQueryNode, parentQueryNode) {
      if (childQueryNode !== parentQueryNode) {
        this.get('state.queries').add(childQueryNode, parentQueryNode);
      }
    },

    /**
     * Moves the current query playhead to point to the given query node.
     * @param {object} queryNode The node to which the playhead will be moved.
     * @public
     */
    moveQueryPlayhead(queryNode) {
      this.set('state.currentQuery', queryNode);
    },

    /**
     * Moves the current event playhead to point to the specified event.
     * Practically this means the previous "current event" will be tossed and replaced, because unlike queries
     * we don't keep track of all the inspected events in a tree (for now :-)).
     * @param {string} serviceId The Core Service ID from which the event record originated.
     * @param {string} eventId The event ID.
     * @public
     */
    moveEventPlayhead(serviceId, eventId) {
      this.set('state.currentEvent', EmberObject.create({ serviceId, eventId }));
    }
  }
});
