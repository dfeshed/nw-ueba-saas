/* eslint-env node */
'use strict';

const EngineAddon = require('ember-engines/lib/engine-addon');

const common = require('../common');
const projectName = 'investigate';
const investigateEventsMocks = require('../investigate-events').mockDestinations;
const investigateFilesMocks = require('../investigate-files').mockDestinations;
const investigateHostsMocks = require('../investigate-hosts').mockDestinations;
const investigateProcessAnalysisMocks = require('../investigate-process-analysis').mockDestinations;
const reconMocks = require('../recon').mockDestinations;

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

  mockDestinations: [
    ...investigateEventsMocks,
    investigateProcessAnalysisMocks,
    ...investigateFilesMocks,
    ...investigateHostsMocks,
    ...reconMocks
  ],

  // See ../common.js for details on this function
  isDevelopingAddon: common.isDevelopingAddon(projectName),

  outputPaths: {
    vendor: {
      js: '/assets/investigate.js',
      css: '/assets/investigate.css'
    }
  }
});