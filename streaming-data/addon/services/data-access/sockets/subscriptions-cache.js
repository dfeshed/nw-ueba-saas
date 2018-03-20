/**
 * @file SubscriptionsCache
 * Cache of subscriptions currently open for a given STOMP client connection.
 * Used by websocket/client to re-use subscriptions.
 * @private
 */
import EmberObject, { computed } from '@ember/object';

export default EmberObject.extend({

  /**
   * Lookup of cached subscriptions.
   * A hashtable; each hash key is a subscription destination; each hash value is an array.
   * The array's items are objects (POJOs) with the properties {callback, sub}, where
   * `callback` is a reference to the callback function that was given for the requested subscription, and
   * `sub` is the resultant subscription object provided by the STOMP client for that destination+callback combination.
   * @private
   * @type {}
   */
  _lookup: computed(() => {
    return {};
  }),

  /**
   * Tries to retrieve the STOMP subscription object from the cache. Two+ keys are required for the lookup. First is the subscription
   * destination, and the second is a specific property (value and name) that further uniquely identifies the subscription. This
   * most commonly will be the callback function, but could also be a specific subcription ID.
   * @param {string} destination The subscription destination.
   * @param value The property value used to find the subscription
   * @param {string} property The property name that must have the provided value when finding the subscription
   * @returns {object} The STOMP subscription object, if found; null otherwise.
   * @public
   */
  find(destination, value, property = 'callback') {
    const cache = this.get('_lookup');
    const found = (cache[destination] || []).findBy(property, value);

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
    const cache = this.get('_lookup');
    let arr = cache[destination];

    if (!arr) {
      cache[destination] = arr = [];
    }
    arr.push({
      callback,
      sub,
      id: sub.id
    });
  },

  /**
   * Removes a given STOMP subscription object from cache.
   * @param {string} destination The subscription destination.
   * @param {object} sub The native STOMP subscription object.
   * @public
   */
  remove(destination, sub) {
    const cache = this.get('_lookup');
    const found = (cache[destination] || []).findBy('sub', sub);

    if (found) {
      cache[destination].removeObject(found);
    }
  }
});