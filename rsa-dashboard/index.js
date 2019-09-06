/* eslint-env node */

const { isDevelopingAddon, emberCliBabelConfig } = require('../common');
const projectName = 'rsa-dashboard';

module.exports = {
  name: projectName,
  options: emberCliBabelConfig,
  isDevelopingAddon: isDevelopingAddon(projectName),
  socketRouteGenerator: require('./config/socketRoutes')
};
