/* eslint-env node */

const path = require('path');
const { isDevelopingAddon, basicOptions } = require('../common');
const projectName = 'respond-shared';

module.exports = {
  name: projectName,
  options: basicOptions(),
  socketRouteGenerator: require('./config/socketRoutes'),
  mockDestinations: path.join(__dirname, 'tests', 'data', 'subscriptions'),
  isDevelopingAddon: isDevelopingAddon(projectName)
};
