/* eslint-env node */
'use strict';

const path = require('path');
const { isDevelopingAddon, emberCliBabelConfig } = require('../common');
const projectName = 'rsa-data-filters';

module.exports = {
  name: projectName,
  options: emberCliBabelConfig,
  isDevelopingAddon: isDevelopingAddon(projectName),
  mockDestinations: path.join(__dirname, 'tests', 'data', 'subscriptions')
};
