/* eslint-env node */
'use strict';

const path = require('path');
const EngineAddon = require('ember-engines/lib/engine-addon');

const common = require('../common');
const projectName = 'live-content';

module.exports = EngineAddon.extend({
  name: projectName,

  // IMPORTANT: If you set this to true then imports inside tests will no longer work.
  lazyLoading: false,

  init() {
    // babel stuff declared here in order to enable
    // investigate working by itself outside of sa
    this._super.init && this._super.init.apply(this, arguments);
    this.options = this.options || {};
    this.options.babel = this.options.babel || {};
    this.options.babel.plugins = [
      'transform-object-rest-spread',
      'transform-decorators-legacy'
    ];
  },

  // This allows node environment variables
  // to be added to the ember config
  config() {
    return {
      mock: process.env.NOMOCK === undefined
    };
  },

  socketRouteGenerator: require('./config/socketRoutes'),

  mockDestinations: path.join(__dirname, 'tests', 'data', 'subscriptions'),

  // See ../common.js for details on this function
  isDevelopingAddon: common.isDevelopingAddon(projectName),

  outputPaths: {
    vendor: {
      js: '/assets/live-content.js',
      css: '/assets/live-content.css'
    }
  }
});