/* eslint-disable */
'use strict';
const EngineAddon = require('ember-engines/lib/engine-addon')

var path = require('path');
var isDevelopingAddon = require('../common').isDevelopingAddon;
var projectName = 'changeMe';

module.exports = EngineAddon.extend({
  name: projectName,

  lazyLoading: true,

  init: function() {
    this._super.init && this._super.init.apply(this, arguments);
    this.options = this.options || {};
    this.options.babel = this.options.babel || {};
    this.options.babel.stage = 0;
  },

  // socketRouteGenerator: require('./config/socketRoutes'),

  // mockDestinations: path.join(__dirname, 'tests', 'server', 'subscriptions')

  // See ../common.js for details on this function
  isDevelopingAddon: isDevelopingAddon(projectName),

  outputPaths: {
    vendor: {
      js: "/assets/changeMe.js",
      css: "/assets/changeMe.css"
    }
  }
})