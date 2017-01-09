/* eslint-disable */

'use strict';

var isDevelopingAddon = require('../common').isDevelopingAddon;
var projectName = 'streaming-data';

module.exports = {
  name: projectName,

  options: {
    nodeAssets: {
      'sockjs-client-web': {
        srcDir: 'dist',
        import: ['sockjs.js']
      },
      '@mind-trace/stompjs': {
        srcDir: 'lib',
        import: ['stomp.js']
      }
    }
  },

  // See ../common.js for details on this function
  isDevelopingAddon: isDevelopingAddon(projectName),

  init: function() {
    this._super.init && this._super.init.apply(this, arguments);
    this.options = this.options || {};
    this.options.babel = this.options.babel || {};
    this.options.babel.stage = 0;
  }
};