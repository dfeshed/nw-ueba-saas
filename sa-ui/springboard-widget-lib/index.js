/* eslint-env node */
'use strict';

const { isDevelopingAddon, basicOptions } = require('../common');
const projectName = 'springboard-widget-lib';

module.exports = {
  name: projectName,
  options: basicOptions(),
  isDevelopingAddon: isDevelopingAddon(projectName)
};
