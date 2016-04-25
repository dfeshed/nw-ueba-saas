/**
 * @file SubscriptionsCache
 * Cache of subscriptions currently open for a given STOMP client connection.
 * Used by websocket/client to re-use subscriptions.
 * @private
 */
import Ember from 'ember';

export default Ember.Object.extend({

  /**
   * Lookup of cached subscriptions.
   * A hashtable; each hash key is a subscription destination; each hash value is an array.
   * The array's items are objects (POJOs) with the properties {callback, sub}, where
   * `callback` is a reference to the callback function that was given for the requested subscription, and
   * `sub` is the resultant subscription object provided by the STOMP client for that destination+callback combination.
   * @private
   * @type {}
   */
  _lookup: Ember.computed(() => {
    return {};
  }),

  /**
   * Tries to retrieve the STOMP subscription object for a given destination + callback from the cache.
   * @param {string} destination The subscription destination.
   * @param {function} callback The callback function.
   * @returns {object} The STOMP subscription object, if found; null otherwise.
   * @public
   */
  find(destination, callback) {
    let cache = this.get('_lookup'),
      found = (cache[destination] || []).findBy('callback', callback);
    return found && found.sub;

  },

  /**
   * Caches a given STOMP subscription object for a given destination + callback. Overwrites any previously
   * cached subscription object for that same destination + callback combination.
   * @param {string} destination The subscription destination.
   * @param {function} callback The callback function.
   * @param {object} sub The native STOMP subscription object.
   * @public
   */
  add(destination, callback, sub) {
    let cache = this.get('_lookup'),
      arr = cache[destination];
    if (!arr) {
      cache[destination] = arr = [];
    }
    arr.push({
      callback,
      sub
    });
  },

  /**
   * Removes a given STOMP subscription object from cache.
   * @param {string} destination The subscription destination.
   * @param {object} sub The native STOMP subscription object.
   * @public
   */
  remove(destination, sub) {
    let cache = this.get('_lookup'),
      found = (cache[destination] || []).findBy('sub', sub);
    if (found) {
      cache[destination].removeObject(found);
    }
  }
});
