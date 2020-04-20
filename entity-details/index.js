/* eslint-env node */
'use strict';

const path = require('path');
const EngineAddon = require('ember-engines/lib/engine-addon');

const common = require('../common');
const projectName = 'entity-details';

const subscriptionPath = path.join(__dirname, 'tests', 'data');

module.exports = EngineAddon.extend({
  name: projectName,

  // IMPORTANT: If you set this to true then imports inside tests will no longer work.
  // we can worry about lazy loading down the road when engines evolves
  lazyLoading: false,

  init() {
    // babel stuff declared here in order to enable
    // investigate working by itself outside of sa
    this._super.init && this._super.init.apply(this, arguments);
    this.options = this.options || {};
    this.options = {
      ...this.options,
      ...common.basicOptions()
    };
  },

  // This allows node environment variables
  // to be added to the ember config
  config() {
    return {
      mock: process.env.NOMOCK === undefined
    };
  },

  socketRouteGenerator: require('./config/socketRoutes'),

  mockDestinations: [
    subscriptionPath
  ],

  dataPath: `${subscriptionPath}/presidio`,

  // See ../common.js for details on this function
  isDevelopingAddon: common.isDevelopingAddon(projectName),

  outputPaths: {
    vendor: {
      js: '/assets/entity-details.js',
      css: '/assets/entity-details.css'
    }
  }
});