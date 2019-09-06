/* eslint-env node */

'use strict';

const { isDevelopingAddon, emberCliBabelConfig } = require('../common');
const projectName = 'rsa-list-manager';

module.exports = {
  name: projectName,
  options: emberCliBabelConfig,
  isDevelopingAddon: isDevelopingAddon(projectName)
};
