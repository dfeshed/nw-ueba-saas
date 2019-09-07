/* eslint-env node */

const path = require('path');
const { isDevelopingAddon, basicOptions } = require('../common');
const projectName = 'context';

module.exports = {
  name: projectName,
  // options: emberCliBabelConfig,
  isDevelopingAddon: isDevelopingAddon(projectName),
  socketRouteGenerator: require('./config/socketRoutes'),
  mockDestinations: path.join(__dirname, 'tests', 'data', 'subscriptions'),

  init() {
    this._super.init && this._super.init.apply(this, arguments);
    this.options = this.options || {};
    this.options = {
      ...this.options,
      ...basicOptions
    };
  }
};
