'use strict';

const path = require('path');
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

  socketRouteGenerator: require('./config/socketRoutes'),

  mockDestinations: path.join(__dirname, 'tests', 'data', 'subscriptions'),

  // See ../common.js for details on this function
  isDevelopingAddon: isDevelopingAddon(projectName)
};
