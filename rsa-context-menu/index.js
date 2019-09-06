/* eslint-env node */

const { isDevelopingAddon, emberCliBabelConfig } = require('../common');
const projectName = 'rsa-context-menu';

module.exports = {
  name: projectName,
  options: emberCliBabelConfig,
  isDevelopingAddon: isDevelopingAddon(projectName)
};
