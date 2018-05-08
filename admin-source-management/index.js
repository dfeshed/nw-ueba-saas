/* eslint-env node */
'use strict';

const path = require('path');
const EngineAddon = require('ember-engines/lib/engine-addon');
const { isDevelopingAddon } = require('../common');

const projectName = 'admin-source-management';
const subscriptionPath = path.join(__dirname, 'tests', 'data');

module.exports = EngineAddon.extend({
  name: projectName,

  // IMPORTANT: If you set this to true then imports inside tests will no longer
  // work. We can worry about lazy loading down the road when engines evolves.
  lazyLoading: false,

  init() {
    // Babel stuff declared here in order to enable admin-source-management working
    // by itself outside of sa.
    this._super.init && this._super.init.apply(this, arguments);
    this.options = this.options || {};
    this.options.babel = this.options.babel || {};
    this.options.babel.plugins = [
      'transform-object-rest-spread',
      'transform-decorators-legacy'
    ];
  },

  // This allows node environment variables to be added to the ember config.
  config() {
    return {
      mock: process.env.NOMOCK === undefined
    };
  },

  socketRouteGenerator: require('./config/socketRoutes'),

  // local mock endpoints last as any dupes = last one wins
  mockDestinations: [subscriptionPath],

  // See ../common.js for details on this function
  isDevelopingAddon: isDevelopingAddon(projectName),

  outputPaths: {
    vendor: {
      js: '/assets/admin-source-management.js',
      css: '/assets/admin-source-management.css'
    }
  }
});
