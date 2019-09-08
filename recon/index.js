/* eslint-env node */

const path = require('path');
const { isDevelopingAddon, basicOptions } = require('../common');
const projectName = 'recon';

const subscriptionPath = path.join(__dirname, 'tests', 'data');
const preferencesMocks = require('../preferences').mockDestinations;
const contextMockDirectory = require('../context').mockDestinations;

module.exports = {
  name: projectName,
  options: basicOptions,
  isDevelopingAddon: isDevelopingAddon(projectName),
  socketRouteGenerator: require('./config/socketRoutes'),
  mockDestinations: [subscriptionPath, preferencesMocks, contextMockDirectory]
};
