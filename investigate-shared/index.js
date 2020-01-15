/* eslint-env node */

const { isDevelopingAddon, basicOptions } = require('../common');
const projectName = 'investigate-shared';

module.exports = {
  name: projectName,
  options: basicOptions(),
  isDevelopingAddon: isDevelopingAddon(projectName)
};
