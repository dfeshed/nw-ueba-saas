/* eslint-disable */
const path = require('path');
const subscriptionPath = path.join(__dirname, 'tests', 'data');

const contextMockDirectory = require('../context').mockDestinations;
const investigateMocks = require('../investigate').mockDestinations;

require('mock-server').startServer({
  subscriptionLocations: [
    subscriptionPath,
    ...investigateMocks,
    contextMockDirectory
  ]
});