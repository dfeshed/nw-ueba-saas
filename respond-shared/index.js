'use strict';

const { isDevelopingAddon } = require('../common');
const projectName = 'respond-shared';

module.exports = {
  name: projectName,

  options: {
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
  isDevelopingAddon: isDevelopingAddon(projectName)
};
