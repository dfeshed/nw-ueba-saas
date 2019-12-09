// This service caches packets after they have been
// processed as processing is expensive

import Service from '@ember/service';

export default Service.extend({

  _cache: [],

  count: 0,

  add(packet) {
    this._cache.push(packet);
    this.count++;
    return packet;
  },

  clear() {
    this._cache = [];
    this.count = 0;
  },

  retrieve(idx) {
    return this._cache[idx];
  },

  /**
   * Returns all non-ignored packets
   */
  retrieveAll() {
    return this._cache.filter((d) => d.ignore !== true);
  },

  retrieveLast() {
    return this._cache.lastItem;
  }

});