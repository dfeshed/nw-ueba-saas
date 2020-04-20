/* eslint-env node */
'use strict';

const { isDevelopingAddon, basicOptions } = require('../common');
const projectName = 'rsa-data-filters';

module.exports = {
  name: projectName,
  options: basicOptions(),
  isDevelopingAddon: isDevelopingAddon(projectName)
};
