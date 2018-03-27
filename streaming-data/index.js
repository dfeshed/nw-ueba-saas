/* eslint-env node */

'use strict';

const { isDevelopingAddon } = require('../common');
const projectName = 'streaming-data';

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
    },
    'ember-cli-babel': {
      includePolyfill: true
    },
    babel: {
      plugins: [
        'transform-object-rest-spread',
        'transform-decorators-legacy'
      ]
    }
  },

  // See ../common.js for details on this function
  isDevelopingAddon: isDevelopingAddon(projectName),
};