/* eslint-env node */

const path = require('path');
const { isDevelopingAddon, emberCliBabelConfig } = require('../common');
const projectName = 'context';

module.exports = {
  name: projectName,
  options: emberCliBabelConfig,
  isDevelopingAddon: isDevelopingAddon(projectName),
  socketRouteGenerator: require('./config/socketRoutes'),
  mockDestinations: path.join(__dirname, 'tests', 'data', 'subscriptions')
};
