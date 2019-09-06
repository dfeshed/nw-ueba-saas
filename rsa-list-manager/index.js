/* eslint-env node */

'use strict';

const { isDevelopingAddon, basicOptions } = require('../common');
const projectName = 'rsa-list-manager';

module.exports = {
  name: projectName,
  options: basicOptions,
  isDevelopingAddon: isDevelopingAddon(projectName)
};
