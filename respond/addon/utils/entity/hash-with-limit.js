import EmberObject from '@ember/object';

/**
 * @class HashWithLimit utility
 * Wraps a standard hash (POJO) with methods for adding/setting the hash's properties, while enforcing a maximum
 * limit on the number of properties that can be set.
 * @public
 */
export default EmberObject.extend({

  /**
   * Collection of stored key-value pairs.
   * @type {Object}
   * @readonly
   * @private
   */
  allItems: null,

  /**
   * Configurable maximum number of items that can be stored in `allItems`.
   * @type {Number}
   * @public
   */
  limit: 0,

  /**
   * If true indicates that `setItem()` has been called and failed, due to `limit` constraint.
   * Note that once `hasExceededLimit` is truthy, it remains truthy for the lifetime of this instance.
   * @readonly
   * @type {Boolean}
   * @public
   */
  hasExceededLimit: false,

  init() {
    this.set('allItems', {});
    this._super(...arguments);
  },

  /**
   * Retrieves a stored value from a given key in `allItems`. Note that if `key` is dotted, it is still treated as
   * a property name, not a property path.
   * @param {String} key The hash key from which the value is to be read.
   * @returns {*}
   * @public
   */
  getItem(key) {
    return this.get('allItems')[key];
  },

  /**
   * Attempts to store a given value in `allItems` under a given key.
   * Aborts if a `limit` has been set and this call would cause the count of stored values to exceed `limit`.
   * Note that if `key` is dotted, it is still treated as a property name, not a property path.
   * @param {String} key The hash key under which the given value is to be stored.
   * @param {*} value The value to be stored.
   * @returns {boolean} true if successful, false otherwise
   * @public
   */
  setItem(key, value) {
    const allItems = this.get('allItems');
    if (!allItems.hasOwnProperty(key)) {
      const limit = this.get('limit');
      const size = this.getSize();
      if (size && (size >= limit)) {
        this.set('hasExceededLimit', true);
        return false;
      }
    }
    allItems[key] = value;
    return true;
  },

  /**
   * Counts the number of keys currently in `allItems`.
   * @returns {Number}
   * @public
   */
  getSize() {
    return Object.keys(this.get('allItems')).length;
  }
});
