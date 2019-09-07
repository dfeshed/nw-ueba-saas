/* eslint-env node */
'use strict';

const { isDevelopingAddon } = require('../common');
const projectName = 'test-helpers';

module.exports = {
  name: projectName,
  // See ../common.js for details on this function
  isDevelopingAddon: isDevelopingAddon(projectName)
};
