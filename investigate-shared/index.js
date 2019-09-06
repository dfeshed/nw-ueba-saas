/* eslint-env node */

const { isDevelopingAddon, emberCliBabelConfig } = require('../common');
const projectName = 'investigate-shared';

module.exports = {
  name: projectName,
  options: emberCliBabelConfig,
  isDevelopingAddon: isDevelopingAddon(projectName)
};
