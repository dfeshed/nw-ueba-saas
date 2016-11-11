/* eslint-disable */

'use strict';

var isDevelopingAddon = require('../common').isDevelopingAddon;
var projectName = 'streaming-data';

module.exports = {
  name: projectName,

  // See ../common.js for details on this function
  isDevelopingAddon: isDevelopingAddon(projectName),

  included: function(app) {
    this._super.included.apply(this, arguments);

    // Websocket libraries: SockJS & STOMP
    app.import("bower_components/sockjs-client/dist/sockjs.js");
    app.import("bower_components/stomp-websocket/lib/stomp.js");
  },

  init: function() {
    this._super.init && this._super.init.apply(this, arguments);
    this.options = this.options || {};
    this.options.babel = this.options.babel || {};
    this.options.babel.stage = 0;
  }
};