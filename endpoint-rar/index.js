/* eslint-env node */

const path = require('path');
const { isDevelopingAddon, basicOptions } = require('../common');
const projectName = 'endpoint-rar';

module.exports = {
  name: projectName,
  options: basicOptions,
  isDevelopingAddon: isDevelopingAddon(projectName),
  socketRouteGenerator: require('./config/socketRoutes'),
  mockDestinations: path.join(__dirname, 'tests', 'data', 'subscriptions')
};