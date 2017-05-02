import Service from 'ember-service';
import computed from 'ember-computed-decorators';
import SummariesCache from 'context/utils/summaries-cache';
import service from 'ember-service/inject';
import { bind } from 'ember-runloop';
import { warn } from 'ember-debug';
import rsvp from 'rsvp';

/**
 * @class Context service
 * A global API for fetching context-related data from server, such as:
 * - the list of known entity types;
 * - summary-level context data about a given entity
 *   (e.g., the number of Incidents, Alerts & Feeds in which the entity was included as of some time frame).
 * @public
 */
export default Service.extend({
  request: service(),

  /**
   * Configuration object, injected from context addon's config/environment file.
   * Used for reading defaults, such as the meta key map for IM Normalized Alert Events.
   * This will be injected dynamically in the file 'app/services/context'.
   * @private
   */
  config: null,

  /**
   * Returns a promise for the list of enabled entity types. Disabled entity types are omitted.
   *
   * When this method is invoked multiple times, it re-uses the last promise if that promise is
   * either still pending or successful.
   *
   * The list of entity types is an Array of Strings.
   * @example
   * ```js
   * [ 'IP', 'USER', 'HOST', 'DOMAIN', 'FILE' ]
   * ```
   * @returns {Promise}
   * @public
   */
  types() {

    // If we already have a promise, re-use it
    let promise = this.get('_typesPromise');
    if (!promise) {

      // We don't have a promise already, create a new one
      promise = this.get('request').promiseRequest({
        modelName: 'entity-type',
        method: 'findAll',
        query: {}
      })
        // If promise fails, clear cached promise, don't re-use
        .catch((err) => {
          warn('Error fetching context entity types', err);
          this.set('_typesPromise', null);
        });

      // Cache promise for reuse
      this.set('_typesPromise', promise);
    }

    return promise;
  },

  // Cache of promise for list of entity types.
  _typesPromise: null,

  /**
   * Returns a promise for a mapping of entity types to meta keys for a given
   * endpoint (concentrator or broker).
   *
   * When this method is invoked multiple times with the same endpointId, it
   * re-uses the last promise for that endpoint, if that promise is either still
   * pending or successful.
   *
   * The mapping of entity types to meta keys is structured as follows:
   * @example
   * ```js
   * [ 'IP', 'USER', 'HOST', 'DOMAIN', 'FILE' ]
   * ```
   * @param {String} endpointId The ID of the concentrator or broker. The ID 'IM' is reserved. If 'IM' is given,
   * returns a mapping of fields for Normalized Alert Events used by NetWitness' Incident Management module.
   * @returns {Promise}
   * @public
   */
  metas(endpointId) {

    // If we already have a promise, re-use it
    const promises = this.get('_metasPromises');
    let promise = promises[endpointId];
    if (!promise) {
      const config = this.get('config') || {};

      // We don't have a promise already, create a new one to fetch the requested data.
      // Look for the data in our config; if not there, fetch it from server-side endpoint.
      if (endpointId in config.contextMetas) {

        // Return a (hard-coded) map for Incident Management Normalized Alert Event properties.
        promise = rsvp.resolve({
          code: 0,
          data: config.contextMetas[endpointId]
        });
      } else {

        // Fetch map data from concentrator/broker.
        promise = this.get('request').promiseRequest({
          modelName: 'entity-meta',
          method: 'findAll',
          query: {
            filter: [{
              field: 'endpointId',
              value: endpointId
            }]
          }
        })
        // If promise fails, clear cached promise, don't re-use
          .catch((err) => {
            warn('Error fetching context entity meta', endpointId, err);
            promises[endpointId] = null;
          });
      }

      // Once we have the data, flatten the JSON into a simple hash for ease of use (e.g. hash[metaKey] => entityType).
      promise = promise.then(({ data }) => {
        const hash = {};
        (data || [])
          .filterBy('enabled', true)  // omit disabled entries from our output hash
          .forEach(({ name, metaKeys }) => {
            (metaKeys || []).forEach((metaKey) => {
              hash[metaKey] = name;
            });
          });
        return hash;
      });

      // Cache promise for reuse
      promises[endpointId] = promise;
    }

    return promise;
  },

  // Cache of promise for list of entity types.
  @computed
  _metasPromises() {
    return {};
  },

  /**
   * Requests a stream of summary-level data records for a given list of entities.
   * Returns a stop function to cancel the request.
   *
   * The summary-level data is an Array of Objects; each Object has `key`, `value` & `lastUpdated` properties.
   * The data records can come from several asynchronous data sources in the backend, therefore they are streamed to
   * the UI, meaning that they may arrive in multiple batches rather than all at once.
   *
   * Each time one or more record arrives, the given `callback` is invoked with 3 input parameters:
   * - `type` (String): the entity type,
   * - `id` (String): the entity id, and
   * - `records` (Object[]): the Array of all the entity's data records that have arrived so far.
   *
   * If `callback` is not provided, this method still fetches the requested data and caches
   * it internally for future reference. This is useful for pre-loading data into the UI.
   *
   * @example
   * Here's a sample batch of 3 data records:
   * ```js
   * [
   *  // Count of incidents related to the given entity
   *  { key: 'incidents', value: 1, lastUpdated: 1479998598247 },
   *  // Count of alerts related to the given entity
   *  { key: 'alerts', value: 2, lastUpdated: 1479998598247 },
   *  // Um, not sure what this is, looks like ECAT data...?
   *  { key: 'machines', value: 'Critical', lastUpdated: 1479998598247 },
   *  ...
   * ]
   * ```
   *
   * @param {{id: String, type: String}[]} entities An array of entities (id + type pairs) for which to fetch data.
   * @param {Function} [callback] Optional function to be invoked each time new data arrives.
   * @returns {Function} The stop function.
   * @public
   */
  summary(entities, callback) {

    // Split the list of entities based on whether we already have the entity in cache.
    const summaries = this.get('_summariesCache');
    const cached = [];
    const uncached = [];
    entities.forEach((entity) => {
      const { type, id } = entity;
      const entry = summaries.find(type, id);
      if (entry) {
        cached.push(entry);
      } else {
        uncached.push(entity);
      }
    });

    // For the cached entities, register their callbacks & manually fire them.
    if (callback) {
      cached.forEach(({ type, id, callbacks, records }) => {
        callbacks.push(callback);
        callback(type, id, records);
      });
    }

    // For the uncached entities, submit a server request..
    return this._onRequest(uncached, callback);
  },

  // Cache of summary-level data records for entities
  _summariesCache: SummariesCache.create(),

  /**
   * Handles server request for summary data.
   * Responsible for wiring up the server stream, for caching the request & its callback, and
   * for generating stop function for cancelling the request.
   *
   * This method will transform the given array of entities into a filter structure expected by the server API, namely:
   * `[ { field: String, values: String[] }, ..]`.
   *
   * @example
   * ```js
   * // Server API expects a `query` input param with the following `filter` object:
   * [
   *   { field: 'IP', values: ['10.20.30.40', '128.20.30.40'] },
   *   { field: 'DOMAIN', values: ['g00gle.com'] },
   *   { field: ..some entity type.., values: [ .. ] },
   *   ..
   * ]
   * ```
   *
   * @param {{type: String, id: String}[]} entities The list of entity type-id pairs for whom data is being requested.
   * @param {Function} [callback] Optional callback to be invoked with server responses.
   * @private
   */
  _onRequest(entities, callback) {

    // Transform the `entities` input array into the filter structure expected by server API.
    const filter = [];
    entities.forEach(({ type, id }) => {
      let entry = filter.findBy('field', type);
      if (!entry) {
        entry = { field: type, values: [] };
        filter.pushObject(entry);
      }
      entry.values.push(id);
    });

    this.get('request').streamRequest({
      modelName: 'entity-summary',
      method: 'stream',
      query: {
        filter
      },
      onInit: (stop) => {
        this.get('_summariesCache').add(entities, callback, stop);
      },
      onResponse: (response) => {
        this._onResponse(response);
      }
    });

    return bind(this, this._summaryStop, entities);
  },

  /**
   * Handles server responses for summary data requests.
   * Responsible for transforming & caching the summary data, and for notifying all the registered callbacks.
   *
   * The server responses include entity data in a structure that is not ideal, but constrained by backwards
   * compatability requirements with 10.x. This method is responsible for parsing & transforming that data into easier
   * structures, which can then be added to our internal cache.
   *
   * Specifically, the server response structure is as follows:
   * The response is an Object with `code` and `data` properties. When the response is successful, `code` is `0` and
   * `data` is a hash. Each hash key is an entity id (e.g., "10.20.30.40") and each corresponding hash value
   * is an Object with `type` (the entity type) and `prefetchData` (an array of entity records) properties.  Each
   * record in the `prefetchData` array has 4 String properties: `name`, `count`, `severity` & `lastUpdated`.
   * @example
   * Here's a sample response that contains summary records for two entities (IPs):
   * ```js
   * {
   *   code: 0,
   *   data: {
   *     "192.168.1.1": {
   *       type: "IP",
   *       prefetchData: [
   *         {
   *           name: "Incidents",
   *           count: "2",
   *           severity: null,
   *           lastUpdated: "1479998598247"
   *         }
   *       ]
   *     },
   *     "1.1.1.1": {
   *      type: "IP",
   *      prefetchData: [
   *        {
   *           name: "Alerts",
   *           count: "3",
   *           severity: null,
   *           lastUpdated: "1479998598247"
   *        },
   *        {
   *           name: "Machines",
   *           count: null,
   *           severity: "Critical",
   *           lastUpdated: "1479998598247"
   *        }
   *      ]
   *     }
   *   }
   * }
   * ```
   *
   * @param {Object} response The server response payload.
   * @private
   */
  _onResponse(response = {}) {
    const summaries = this.get('_summariesCache');
    const { data = {} } = response;

    // For each entity in the response...
    Object.keys(data).forEach((id) => {

      // Parse out the records for that entity.
      const { type, prefetchData } = data[id];
      const records = prefetchData.map(({ name, count, severity, lastUpdated }) => ({
        name,
        count: Number(count),
        severity,
        lastUpdated: Number(lastUpdated)
      }));

      // Update the entity's cache data.
      const entry = summaries.update(type, id, records);

      // Notify all the cached callbacks of this update.
      if (entry) {
        (entry.callbacks || []).forEach((callback) => {
          callback(type, id, entry.records);
        });
      }
    });
  },

  /**
   * Cancels a stream request (created by calling `summary`) for a given list of entities.
   *
   * @param {Object[]} entities The list of entity type-id pairs.
   * @private
   */
  _summaryStop(entities = []) {
    const summaries = this.get('_summariesCache');
    const requests = summaries.get('requests');

    // For each entity whose request we want to stop...
    entities.forEach(({ type, id }) => {

      // Find an entry in the cache corresponding to that entity's request.
      const entry = summaries.find(type, id);

      // Does the entry also have a stop function? If so, call it.
      const { stop } = entry || {};
      if (stop) {
        stop();

        // Optimization: we don't need to call the same stop more than once.
        // So find & remove all other occurrences of this stop in the cache.
        requests.forEach((entry) => {
          if (entry.stop === stop) {
            entry.stop = null;
          }
        });
      }
    });
  }
});
