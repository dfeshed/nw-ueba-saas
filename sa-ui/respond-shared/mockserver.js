/* eslint-env node */
const path = require('path');
const subscriptionPath = path.join(__dirname, 'tests', 'data');

const contextMockDirectory = require('../context').mockDestinations;

require('mock-server').startServer({
  subscriptionLocations: [
    subscriptionPath,
    contextMockDirectory
  ]
});