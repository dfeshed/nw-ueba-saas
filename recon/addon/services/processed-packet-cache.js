// This service caches packets after they have been
// processed as processing is expensive

import Service from '@ember/service';

export default Service.extend({

  cache: {},

  add(packet) {
    this.cache[packet.id] = packet;
  },

  retrieve(id) {
    return this.cache[id];
  },

  clear() {
    this.cache = {};
  }

});