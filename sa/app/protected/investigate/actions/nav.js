import Ember from 'ember';
import QueryTreeNode from '../state/query-node';
import QueryDefinition from '../state/query-definition';
import { makeServerInputsForEndpointInfo } from './helpers/query-utils';

const {
  Mixin,
  RSVP
} = Ember;

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
      const wasQueryNode = this.get('state.queryNode');
      const wasLastQueryNode = this.get('state.lastQueryNode');
      if (queryNode === wasQueryNode) {
        return;
      }
      this.setProperties({
        'state.queryNode': queryNode,
        'state.lastQueryNode': wasQueryNode
      });

      // Before fetching query results, first fetch language & aliases info, for presenting the results data correctly.
      // If the fetches fail, still proceed; the presentation may be sub-optimal but still better than nothing!
      RSVP.allSettled([
        this._getQueryLanguage(queryNode),
        this._getQueryAliases(queryNode)
      ]).finally(() => {
        this.send('resultsGet', queryNode, false);
        this.send('resultsClear', wasLastQueryNode);  // optimization: release data from 2nd-next-to-last-query
      });
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
        queryNode = QueryTreeNode.create();
        queryNode.set('value.definition', QueryDefinition.create(filterParams));
        queryTree.add(queryNode, state.get('queryNode'));
      }

      // Move the playhead to this query.
      this.send('navGoto', queryNode);
    }
  },

  /**
   * Ensures that the given query has its language info populated. If not, kicks off call to retrieve it.
   * This information can be fetched via microservice call.
   * Optimization: Cache results in `state.languages` object to avoid repeated calls to server.
   * @param {object} queryNode
   * @returns {Promise}
   * @private
   */
  _getQueryLanguage(queryNode) {
    return new RSVP.Promise((resolve, reject) => {

      // Skip if the query already has the language.
      if (!queryNode || (queryNode.get('value.language.status') === 'resolved')) {
        resolve();
        return;
      }

      const serviceId = queryNode.get('value.definition.serviceId');
      const queryNodeLanguage = queryNode.get('value.language');

      // Define callbacks for server call.
      const success = function({ data }) {
        // store results in query node
        queryNodeLanguage.setProperties({
          status: 'resolved',
          data
        });
        // cache server data for future reference
        this.set(`state.languages.${serviceId}`, data);
        resolve();
      };
      const fail = function(reason) {
        // store result in query node
        queryNodeLanguage.setProperties({
          status: 'rejected',
          reason
        });
        reject();
      };

      // Check the languages cache before calling server. If data found there, skip server call.
      const language = this.get(`state.languages.${serviceId}`);
      if (language) {
        success({ data: language });
        return;
      }

      // Before calling server, init status to "wait".
      queryNodeLanguage.setProperties({
        status: 'wait',
        data: []
      });

      this.request.promiseRequest({
        method: 'query',
        modelName: 'core-meta-key',
        query: makeServerInputsForEndpointInfo(serviceId)
      })
        .then(success)
        .catch(fail);
    });
  },

  /**
   * Ensures that the given query has its aliases info populated. If not, kicks off call to retrieve it.
   * This information can be fetched via microservice call.
   * Optimization: Cache results in `state.aliases` object to avoid repeated calls to server.
   * @param {object} queryNode
   * @returns {Promise}
   * @private
   */
  _getQueryAliases(queryNode) {
    return new RSVP.Promise((resolve, reject) => {

      // Skip if the query already has the aliases.
      if (!queryNode || (queryNode.get('value.aliases.status') === 'resolved')) {
        resolve();
        return;
      }

      const serviceId = queryNode.get('value.definition.serviceId');
      const queryNodeAliases = queryNode.get('value.aliases');

      // Define callbacks for server call.
      const success = function({ data }) {
        // store results in query node
        queryNodeAliases.setProperties({
          status: 'resolved',
          data
        });
        // cache server data for future reference
        this.set(`state.aliases.${serviceId}`, data);
        resolve();
      };
      const fail = function(reason) {
        // store result in query node
        queryNodeAliases.setProperties({
          status: 'rejected',
          reason
        });
        reject();
      };

      // Check the aliases cache before calling server. If data found there, skip server call.
      const aliases = this.get(`state.aliases.${serviceId}`);
      if (aliases) {
        success({ data: aliases });
        return;
      }

      // Before calling server, init status to "wait".
      queryNodeAliases.setProperties({
        status: 'wait',
        data: []
      });

      this.request.promiseRequest({
        method: 'query',
        modelName: 'core-meta-alias',
        query: makeServerInputsForEndpointInfo(serviceId)
      })
        .then(success)
        .catch(fail);
    });
  }
});
