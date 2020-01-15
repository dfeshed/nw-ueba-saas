/* eslint-env node */

const { isDevelopingAddon, basicOptions } = require('../common');
const projectName = 'rsa-dashboard';

module.exports = {
  name: projectName,
  options: basicOptions(),
  isDevelopingAddon: isDevelopingAddon(projectName),
  socketRouteGenerator: require('./config/socketRoutes')
};
