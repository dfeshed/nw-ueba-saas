/* eslint-env node */
'use strict';

const { isDevelopingAddon } = require('../common');
const projectName = 'test-helpers';

module.exports = {
  name: projectName,

  // options: basicOptions(),

  // See ../common.js for details on this function
  isDevelopingAddon: isDevelopingAddon(projectName)
};
