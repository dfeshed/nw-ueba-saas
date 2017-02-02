/* eslint-disable */
'use strict';

var path = require('path');
var isDevelopingAddon = require('../common').isDevelopingAddon;
var projectName = 'context';

module.exports = {
  name: projectName,

  // See ../common.js for details on this function
  isDevelopingAddon: isDevelopingAddon(projectName),

  init: function() {
    this._super.init && this._super.init.apply(this, arguments);
    this.options = this.options || {};
    this.options.babel = this.options.babel || {};
    this.options.babel.stage = 0;
  },

  socketRouteGenerator: require('./config/socketRoutes'),

  mockDestinations: path.join(__dirname, 'tests', 'server', 'subscriptions')
};
