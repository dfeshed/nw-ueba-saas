import Ember from 'ember';
import QueryTreeNode from '../state/query-node';
import QueryDefinition from '../state/query-definition';
import { makeServerInputsForEndpointInfo } from './helpers/query-utils';
import { eventQueryUri } from 'sa/helpers/event-query-uri';

const {
  Logger,
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
        'state.lastQueryNode': wasQueryNode,
        'state.routeStatus': 'wait',
        'state.routeReason': undefined
      });

      // Before fetching query results, first fetch language & aliases info, for presenting the results data correctly.
      // If the fetches fail, still proceed; the presentation may be sub-optimal but still better than nothing!
      RSVP.allSettled([
        this._getQueryLanguage(queryNode),
        this._getQueryAliases(queryNode)
      ])
        .then(() => {
          this.set('state.routeStatus', 'resolved');
        })
        .catch((reason) => {
          this.setProperties({
            'state.routeStatus': 'rejected',
            'state.routeReason': reason
          });
        })
        .finally(() => {
          this.send('resultsGet', queryNode, false);
          // optimization: release data from 2nd-next-to-last-query
          if (wasLastQueryNode !== queryNode && wasLastQueryNode !== wasQueryNode) {
            this.send('resultsClear', wasLastQueryNode);
          }
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

        // Performance optimization: if the new node & previous node point to the same service,
        // copy over the language & aliases from the previous node to the new node.  If this happens BEFORE we navigate
        // to the new node, there won't be a (brief) moment when we are browsing a node without a language,
        // which would be great because then Ember won't destroy & re-create the entire Meta panel UI.
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
      this.transitionTo(
        'protected.investigate.query',
        eventQueryUri([ queryNode.get('value.definition'), metaName, metaValue ])
      );
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

      // Check the languages cache before calling server. If data found there, skip server call.
      const serviceId = queryNode.get('value.definition.serviceId');
      const language = this.get(`state.languages.${serviceId}`);
      if (language) {
        queryNode.set('value.language', language);
        resolve();
        return;
      }

      // Not cached, fetch language from server.
      this._fetchQueryLanguage(queryNode)
        .then(() => {
          // Cache server result for future reference.
          this.set(`state.languages.${serviceId}`, queryNode.get('value.Language'));
          resolve();
        })
        .catch(reject);
    });
  },

  /**
   * Fetches the language for a given query node from the server; stores result in query node's value.language.
   * @param {object} queryNode
   * @returns {Ember.RSVP.Promise}
   * @private
   */
  _fetchQueryLanguage(queryNode) {
    const serviceId = queryNode.get('value.definition.serviceId');
    const queryNodeLanguage = queryNode.get('value.language');

    // Before calling server, init status to "wait".
    queryNodeLanguage.setProperties({
      status: 'wait',
      data: []
    });

    Logger.info('Fetching language for service ID:', serviceId);
    return this.request.promiseRequest({
      method: 'query',
      modelName: 'core-meta-key',
      query: makeServerInputsForEndpointInfo(serviceId)
    })
      .then(({ data }) => {
        // store results in query node
        Logger.info('Language fetched successfully for service ID:', serviceId);
        queryNodeLanguage.setProperties({
          status: 'resolved',
          data
        });
      })
      .catch((reason) => {
        // store result in query node
        Logger.warn('Error fetching language for service ID:', serviceId);
        queryNodeLanguage.setProperties({
          status: 'rejected',
          reason
        });
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
      const success = ({ data }) => {
        Logger.info('Successfully fetched aliases for service ID:', serviceId);
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
        Logger.warn('Error fetching aliases for service ID:', serviceId);
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

      Logger.info('Fetching aliases for service ID:', serviceId);
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
