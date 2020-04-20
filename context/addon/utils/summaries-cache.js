import EmberObject from '@ember/object';
import computed from 'ember-computed-decorators';

// Computes a hash key for the given entity type + id pair.
const requestKey = (type, id) => `${type}::${id}`;

/**
 * @class Context Summaries cache
 * A client-side caching system for entities' summary-level context data.
 *
 * Summary-level data is cached on the client for performance reasons.  Typically a UI may display a given entity
 * in multiple places and request its summary-level data multiple times, without the latency of a server round-trip.
 * Therefore we provide a client-side cache, which is not strictly essential but very useful for UX reasons.
 *
 * @public
 */
export default EmberObject.extend({
  /**
   * A chronological list of requests (& their related data) for summary-level data.
   *
   * Each entry in this Array represents a request. Each entry is an POJO which has the following structure:
   * ```js
   * {
   *  type: 'IP', // entity type
   *  id: '10.20.30.40',  // entity id
   *  lastRequested: 1479998598247, // timestamp of last request for this entity
   *  callbacks: [ .. ],  // Array of Functions, possibly empty
   *  status: 'streaming|error|complete', // status of the data stream request
   *  stop: .., // Function, possibly null
   *  responses: [  // Array of data records from server for this entity
   *    { key: .., value: .., lastUpdated: .. },
   *    { key: .., value: .., lastUpdated: .. },
   *    ...
   *  ]
   * }
   * ```
   *
   * Whenever a request or callback is added to this list, the entry's `lastRequested` is updated to now.
   * Additionally, the Array is re-sorted chronologically (descending), such that the latest requests
   * are at the start of the Array.  This is done in order to facilitate finding & pruning old requests from the Array.
   *
   * @type {Object[]}
   * @public
   */
  @computed
  requests() {
    return [];
  },

  /**
   * Maximum number of requests that can be stored in cache.
   * Enables us to limit the memory consumption of this cache.
   *
   * @type {Number}
   * @private
   */
  requestsLimit: 1000,

  /**
   * Computes a lookup hash from `requests` for doing quick lookups of records by entity type & id.
   *
   * This hash is provided as a performance optimization. The hash acts an index of the `requests` array.
   * It enables us to lookup a request by entity type & id without having to loop thru the entire array
   * every time. (Of course, the downside of having an index is extra memory consumption.)
   *
   * Each hash key is an entity type + id pair (e.g., `<type>::<id>`). The corresponding hash value is a reference
   * to the record in `requests` for that entity (if any). For example:
   * ```js
   * {
   *   'IP::10.20.30.40': .., // points to a member of `requests` array
   *   'USER::admin': ..,   // points to another member of `requests` array
   *   ..
   * }
   * ```
   *
   * Warning: Since hash keys can include dots (`.`), do not use get(`requestsHash.${key}`) to retrieve hash values.
   * Ember will mistake the dotted key name for a nested path. Instead use get('requestsHash')[key].
   *
   * @type {Object}
   * @private
   */
  @computed()
  requestsHash() {
    return {};
  },

  /**
   * Returns the `requests` entry that matches a given entity type + id pair (if any).
   *
   * @param {String} type Entity type.
   * @param {String} id Entity identifier.
   * @returns {Object} The cached entry, if found; undefined otherwise.
   * @public
   */
  find(type, id) {
    const key = requestKey(type, id);
    return this.get('requestsHash')[key];
  },

  /**
   * Removes request entries from the cache.
   *
   * @param {{type: String, id: String}[]} entities The entity type + id pairs to be removed.
   * @public
   */
  remove(entities = []) {
    const { requests, requestsHash } = this.getProperties('requests', 'requestsHash');
    entities.forEach(({ type, id }) => {

      // Find the corresponding entry. If found, delete it.
      const key = requestKey(type, id);
      const entry = requestsHash[key];
      if (entry) {
        requests.removeObject(entry);
        delete requestsHash[key];
      }
    });
  },

  /**
   * Adds a new entry in cache for a given list of entity type + id pair, if not found in cache already.
   * If not found in cache already, the new cache entry's status is initially set to the given status.
   * Also adds the given callback & stop functions (if any) to the cache entry.  This method does not record any
   * data for the request, just the request itself.
   *
   * When this method is invoked, the entry's `lastRequested` property will be reset to now, and the entry
   * is moved to the start of the cache array.  This allows us to easily delete the oldest cache entries, once
   * the cache size exceeds its limit.
   *
   * @param {{type: String, id: String}[]} entities The list of entity type + id pairs to be added.
   * @param {Function} [callback] Optional function to be invoked when new data arrives for the entity.
   * @param {Function} [stop] Optional function that will stop future callbacks when invoked.
   * @public
   */
  add(entities, callback, stop, status) {
    const { requests, requestsHash } = this.getProperties('requests', 'requestsHash');
    const now = Number(new Date());
    let addedNewEntry = false;

    entities.forEach(({ type, id }) => {

      // Find the corresponding entry.
      const key = requestKey(type, id);
      let entry = requestsHash[key];
      if (entry) {

        // Found it. Remove it from its current array position.
        requests.removeObject(entry);
      } else {

        // Not found. Create a new entry & append to end of array.
        entry = requestsHash[key] = { type, id, records: [], callbacks: [], status };
        addedNewEntry = true;
      }

      // Now that we have an entry, update it and unshift it to the start of the array.
      entry.lastRequested = now;
      if (callback) {
        entry.callbacks.push(callback);
      }
      if (stop) {
        entry.stop = stop;
      }
      requests.unshiftObject(entry);
    });

    // If total count of all currently cached requests exceeds limit, drop the oldest request caches.
    if (addedNewEntry) {
      this.trim();
    }
  },

  /**
   * Checks if the size of `requests` has exceeded the `requestsLimit`, and if so, removes the oldest entries from
   * `requests` so that its size matches `requestsLimit`.
   *
   * @private
   */
  trim() {
    const { requests, requestsHash, requestsLimit: limit } = this.getProperties('requests', 'requestsHash', 'requestsLimit');
    const size = requests.length;
    if (size > limit) {

      // Remove the oldest entries from  both `requests` and `requestsHash`.
      // Optimization: don't use `this.remove()`; its `Array.removeObject()` calls would needlessly loop & be slower.
      requests
        .slice(limit, size)
        .forEach(({ type, id }) => {
          const key = requestKey(type, id);
          delete requestsHash[key];
        });
      requests.removeAt(limit, size - limit);

    }
  },

  /**
   * Updates the status and response data of a given cached request.
   *
   * This method can be invoked to update either the status of a request or its results set or both.
   * If a status is given, the cached status for the request will be updated.  If data records are given, they will be
   * appended to the cached data records for the request.
   * If the request for the specified entity is not found in the cache, does nothing.  This method does not modify the request's callbacks,
   * nor does it invoke them. It simply adds to the response data that has been cached for the given entity.
   *
   * @param {String} type Entity type.
   * @param {String} id Entity identifier.
   * @param {String} [status] Either 'streaming', 'error' or 'complete'.
   * @param {Object[]} [records] Array of objects (like `{key, value, lastUpdated}`).
   * @returns {Object} The updated cache entry object, if any changes were successfully recorded; null otherwise.
   * @public
   */
  update(type, id, status, records) {
    const entry = this.find(type, id);
    let changed = false;
    if (entry) {
      if (status && entry.status !== status) {
        entry.status = status;
        changed = true;
      }
      if (records) {
        entry.records.pushObjects(records);
        changed = true;
      }
    }
    return changed ? entry : null;
  }
});
