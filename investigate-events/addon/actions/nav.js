/**
 * Investigate Route Navigational Actions
 * Route actions related to navigating from one query/event to another.
 * These actions assume that the state is accessible via `this.get('state')`.
 * @public
 */
import Mixin from 'ember-metal/mixin';
import service from 'ember-service/inject';
import QueryTreeNode from 'investigate-events/state/query-node';
import QueryDefinition from 'investigate-events/state/query-definition';
import { eventQueryUri } from 'investigate-events/helpers/event-query-uri';

export default Mixin.create({
  redux: service(),

  actions: {
    /**
     * Moves the current query playhead to point to the given query node. Then fires subsequent action to ensure
     * that the current's events are fetched.
     * @param {object} queryNode The node to which the playhead will be moved.
     * @public
     */
    navGoto(queryNode) {
      const wasQueryNode = this.get('state.queryNode');
      const wasLastQueryNode = this.get('state.lastQueryNode');
      if (queryNode === wasQueryNode) {
        // We are not navigating to another query node BUT we may have toggled a panel,
        // so we may still need to fetch results for any newly-opened panels.
        if (queryNode) {
          this.send('resultsGet', queryNode, false);
        }
        return;
      }
      // Before navigating to a query, close recon. If coming from another
      // route, it may already be closed; but here we make sure even if
      // navigating via breadcrumb or drills.
      this.send('reconClose', true);
      this.setProperties({
        'state.queryNode': queryNode,
        'state.lastQueryNode': wasQueryNode
      });
      // Before fetching query results, first fetch language & aliases info,
      // for presenting the results data correctly.
      this._getQueryLanguage(queryNode);
      this._getQueryAliases(queryNode);
      this.send('resultsGet', queryNode, false);
      // optimization: release data from 2nd-next-to-last-query
      if (wasLastQueryNode !== queryNode && wasLastQueryNode !== wasQueryNode) {
        this.send('resultsClear', wasLastQueryNode);
      }
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
      const state = this.get('state');
      const queryTree = state.get('queryTree');
      let queryNode = queryTree.find(filterParams);
      if (!queryNode) {
        // No matching query found,
        // so add a new node to the query tree for this query.
        queryNode = QueryTreeNode.create();
        queryNode.set('value.definition', QueryDefinition.create(filterParams));
        // Performance optimization: if the new node & previous node point to
        // the same service, copy over the language & aliases from the previous
        // node to the new node.  If this happens BEFORE we navigate to the new
        // node, there won't be a (brief) moment when we are browsing a node
        // without a language, which would be great because then Ember won't
        // destroy & re-create the entire Meta panel UI.
        const currentNode = state.get('queryNode');
        if (currentNode && currentNode.get('value.definition.serviceId') === filterParams.serviceId) {
          queryNode.setProperties({
            'value.language': currentNode.get('value.language'),
            'value.aliases': currentNode.get('value.aliases')
          });
        }
        queryTree.add(queryNode, currentNode);
      }
      // Move the playhead to this query.
      this.send('navGoto', queryNode);
    },

    /**
     * Requests a drill from a given query on a given meta key name-value pair.
     * The drill is performed by constructing a URL for the drill and navigating to that URL.
     * @param {object} queryNode The source query node we are drilling from.
     * @param {string} metaName The meta key identifier (e.g., "ip.src").
     * @param {*} metaValue The meta key value (raw, not aliased).
     * @public
     */
    navDrill(queryNode, metaName, metaValue) {
      this.transitionTo('query', eventQueryUri([
        queryNode.get('value.definition'),
        metaName,
        metaValue
      ]));
    }
  },

  /**
   * Ensures that the given query has its language info populated.
   * @param {object} queryNode
   * @returns {Promise}
   * @private
   */
  _getQueryLanguage(queryNode) {
    // Skip if the query already has the language.
    if (!queryNode || queryNode.get('value.language.status') === 'resolved') {
      return;
    }
    const state = this.get('redux').getState();
    const { serviceId } = state.queryNode;
    const language = state.dictionaries.languageCache[serviceId];
    if (language) {
      queryNode.set('value.language', language);
    } else {
      window.console.error('Language was not available.');
    }
  },

  /**
   * Ensures that the given query has its aliases info populated.
   * @param {object} queryNode
   * @returns {Promise}
   * @private
   */
  _getQueryAliases(queryNode) {
    // Skip if the query already has the aliases.
    if (!queryNode || (queryNode.get('value.aliases.status') === 'resolved')) {
      return;
    }
    const state = this.get('redux').getState();
    const { serviceId } = state.queryNode;
    const aliases = state.dictionaries.aliasesCache[serviceId];
    if (aliases) {
      queryNode.set('value.aliases', aliases);
    } else {
      window.console.error('Aliases were not available.');
    }
  }
});
