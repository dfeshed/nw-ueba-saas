/* eslint-env node */
'use strict';

const path = require('path');
const { isDevelopingAddon, basicOptions } = require('../common');
const projectName = 'rsa-data-filters';

module.exports = {
  name: projectName,
  options: basicOptions,
  isDevelopingAddon: isDevelopingAddon(projectName),
  mockDestinations: path.join(__dirname, 'tests', 'data', 'subscriptions')
};
