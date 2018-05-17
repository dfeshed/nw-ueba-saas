/* eslint-env node */
'use strict';

const path = require('path');
const { isDevelopingAddon } = require('../common');
const projectName = 'rsa-context-menu';

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
