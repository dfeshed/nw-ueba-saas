/* eslint-env node */

const { isDevelopingAddon, basicOptions } = require('../common');
const projectName = 'rsa-context-menu';

module.exports = {
  name: projectName,
  options: basicOptions,
  isDevelopingAddon: isDevelopingAddon(projectName)
};
