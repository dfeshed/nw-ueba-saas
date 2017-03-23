/* eslint-env node */
'use strict';

const path = require('path');
const { isDevelopingAddon } = require('../common');
const projectName = 'context';

module.exports = {
  name: projectName,

  // See ../common.js for details on this function
  isDevelopingAddon: isDevelopingAddon(projectName),

  init() {
    this._super.init && this._super.init.apply(this, arguments);
    this.options = this.options || {};
    this.options.babel = this.options.babel || {};
    this.options.babel.stage = 0;
  },

  socketRouteGenerator: require('./config/socketRoutes'),

  mockDestinations: path.join(__dirname, 'tests', 'server', 'subscriptions')
};
