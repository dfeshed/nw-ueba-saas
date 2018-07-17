/* eslint-env node */
'use strict';

const path = require('path');
const { isDevelopingAddon } = require('../common');
const projectName = 'rsa-data-filters';

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
  isDevelopingAddon: isDevelopingAddon(projectName),

  mockDestinations: path.join(__dirname, 'tests', 'data', 'subscriptions')
};
