/* eslint-env node */
'use strict';

const path = require('path');
const { isDevelopingAddon } = require('../common');
const projectName = 'recon';

const subscriptionPath = path.join(__dirname, 'tests', 'data');
const preferencesMocks = require('../preferences').mockDestinations;
const contextMockDirectory = require('../context').mockDestinations;

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

  socketRouteGenerator: require('./config/socketRoutes'),

  mockDestinations: [subscriptionPath, preferencesMocks, contextMockDirectory]
};
